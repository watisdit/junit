package org.junit.internal.runners;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sorter;

public class JavaMethodList extends JavaModelElement implements
		Iterable<JavaMethod> {
	private static final long serialVersionUID= 1L;

	private final JavaClass fJavaClass;

	private List<JavaMethod> fMethods= new ArrayList<JavaMethod>();

	public JavaMethodList(JavaClass javaClass) {
		fJavaClass= javaClass;
	}

	void filter(Filter filter) throws NoTestsRemainException {
		for (Iterator<JavaMethod> iter= fMethods.iterator(); iter.hasNext();) {
			JavaMethod method= iter.next();
			if (!filter.shouldRun(method.description()))
				iter.remove();
		}
		if (fMethods.isEmpty())
			throw new NoTestsRemainException();
	}

	void filter(final Sorter sorter) {
		Collections.sort(fMethods, new Comparator<JavaMethod>() {
			public int compare(JavaMethod o1, JavaMethod o2) {
				return sorter.compare(o1.description(), o2.description());
			}
		});
	}

	@Override
	public Class<? extends Annotation> getAfterAnnotation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<? extends Annotation> getBeforeAnnotation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JavaClass getJavaClass() {
		return fJavaClass;
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
	public Description description() {
		Description spec= Description.createSuiteDescription(fJavaClass
				.getName());
		for (JavaMethod method : this)
			spec.addChild(method.description());
		return spec;
	}

	void run(TestEnvironment environment) {
		if (isEmpty())
			// TODO: DUP
			environment.getRunNotifier().testAborted(description(), new Exception(
					"No runnable methods"));
		for (JavaMethod method : this) {
			method.invokeTestMethod(environment);
		}
	}
}
