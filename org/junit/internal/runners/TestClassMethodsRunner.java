package org.junit.internal.runners;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;

public class TestClassMethodsRunner extends Runner implements Filterable,
		Sortable {
	private final JavaMethodList fTestMethods;

	private final JavaClass fTestClass;

	private final JavaTestInterpreter fInterpreter;

	// This assumes that some containing runner will perform validation of the
	// test methods
	public TestClassMethodsRunner(JavaClass klass,
			JavaTestInterpreter javaTestInterpreter) {
		// TODO: DUP?
		fTestClass= klass;
		fTestMethods= klass.getMethods(Test.class, javaTestInterpreter);
		fInterpreter= javaTestInterpreter;
	}

	// TODO: rename method to element?

	@Override
	public void run(RunNotifier notifier) {
		if (fTestMethods.isEmpty())
			notifier.testAborted(getDescription(), new Exception(
					"No runnable methods"));
		for (JavaMethod method : fTestMethods)
			invokeTestMethod(method, notifier);
	}

	@Override
	public Description getDescription() {
		Description spec= Description.createSuiteDescription(fTestClass
				.getName());
		for (JavaMethod method : fTestMethods)
			spec.addChild(method.description());
		return spec;
	}

	private void invokeTestMethod(JavaMethod method, RunNotifier notifier) {
		Object test;
		try {
			test= method.getJavaClass().newInstance();
		} catch (InvocationTargetException e) {
			notifier.testAborted(method.description(), e.getCause());
			return;
		} catch (Exception e) {
			notifier.testAborted(method.description(), e);
			return;
		}
		TestEnvironment testEnvironment= new TestEnvironment(fInterpreter,
				new PerTestNotifier(notifier, method.description()), test);
		testEnvironment.run(method);
	}

	public void filter(Filter filter) throws NoTestsRemainException {
		fTestMethods.filter(filter);
	}

	public void sort(final Sorter sorter) {
		fTestMethods.filter(sorter);
	}
}