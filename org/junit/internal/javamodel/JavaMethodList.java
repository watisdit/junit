package org.junit.internal.javamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;

public class JavaMethodList extends JavaModelElement implements
		Iterable<JavaMethod>, Filterable, Sortable {
	private static final long serialVersionUID= 1L;

	private final JavaClass fJavaClass;

	private List<JavaMethod> fMethods= new ArrayList<JavaMethod>();

	public JavaMethodList(JavaClass javaClass) {
		super(javaClass.getInterpreter());
		fJavaClass= javaClass;
	}

	public void filter(Filter filter) throws NoTestsRemainException {
		for (Iterator<JavaMethod> iter= fMethods.iterator(); iter.hasNext();) {
			JavaMethod method= iter.next();
			if (!filter.shouldRun(method.getDescription()))
				iter.remove();
		}
		if (fMethods.isEmpty())
			throw new NoTestsRemainException();
	}

	public void sort(final Sorter sorter) {
		Collections.sort(fMethods, new Comparator<JavaMethod>() {
			public int compare(JavaMethod o1, JavaMethod o2) {
				return sorter.compare(o1.getDescription(), o2.getDescription());
			}
		});
	}

	@Override
	public String getName() {
		return String.format("%s methods", fJavaClass.getName());
	}

	public Iterator<JavaMethod> iterator() {
		return fMethods.iterator();
	}

	public void add(JavaMethod eachMethod) {
		fMethods.add(eachMethod);
	}

	public boolean isEmpty() {
		return fMethods.isEmpty();
	}

	public void reverse() {
		Collections.reverse(fMethods);
	}

	@Override
	public Description getDescription() {
		Description spec= Description.createSuiteDescription(fJavaClass
				.getName());
		for (JavaMethod method : this)
			spec.addChild(method.getDescription());
		return spec;
	}

	@Override
	public void run(RunNotifier notifier) {
		if (isEmpty())
			// TODO: DUP
			notifier.testAborted(getDescription(), new Exception(
					"No runnable methods"));
		for (JavaMethod method : this) {
			method.invokeTestMethod(notifier);
		}
	}
}
