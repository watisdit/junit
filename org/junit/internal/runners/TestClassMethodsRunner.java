package org.junit.internal.runners;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;

public class TestClassMethodsRunner extends Runner implements Filterable, Sortable {
	private final List<Method> fTestMethods;
	private final Class<? extends Object> fTestClass;
	
	public TestClassMethodsRunner(Class<? extends Object> klass) throws InitializationError {
		fTestClass= klass;
		MethodValidator validator= new MethodValidator(klass);
		validator.validateInstanceMethods();
		validator.assertValid();
		fTestMethods= new TestIntrospector(getTestClass()).getTestMethods(Test.class);
	}
	
	@Override
	public void run(RunNotifier notifier) {
			List<Method> methods= fTestMethods;
			for (Method method : methods) {
				try {
					invokeTestMethod(method, notifier);
				} catch (StoppedByUserException e) {
					throw e;
				} catch (Exception e) {
					notifier.fireNonTestFailure(e);
				}
			}
	}

	@Override
	public Description getDescription() {
		Description spec= Description.createSuiteDescription(getName());
		List<Method> testMethods= fTestMethods;
		for (Method method : testMethods)
				spec.addChild(methodDescription(method));
		return spec;
	}

	protected String getName() {
		return getTestClass().getName();
	}
	
	protected Object createTest() throws Exception {
		return getTestClass().getConstructor().newInstance();
	}

	protected void invokeTestMethod(Method method, RunNotifier notifier) throws Exception {
		new TestMethodRunner(createTest(), method, notifier, methodDescription(method)).run();
	}

	protected String testName(Method method) {
		return method.getName();
	}

	private Description methodDescription(Method method) {
		return Description.createTestDescription(getTestClass(), testName(method));
	}

	public void filter(Filter filter) {
		for (Iterator iter= fTestMethods.iterator(); iter.hasNext();) {
			Method method= (Method) iter.next();
			if (!filter.shouldRun(methodDescription(method)))
				iter.remove();
		}
	}

	public void sort(final Sorter sorter) {
		Collections.sort(fTestMethods, new Comparator<Method>() {
			public int compare(Method o1, Method o2) {
				return sorter.compare(methodDescription(o1), methodDescription(o2));
			}
		});
	}

	protected Class<? extends Object> getTestClass() {
		return fTestClass;
	}
}
