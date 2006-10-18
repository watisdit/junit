package org.junit.internal.runners;


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
			method.invokeTestMethod(notifier, fInterpreter);
	}

	@Override
	public Description getDescription() {
		Description spec= Description.createSuiteDescription(fTestClass
				.getName());
		for (JavaMethod method : fTestMethods)
			spec.addChild(method.description());
		return spec;
	}

	public void filter(Filter filter) throws NoTestsRemainException {
		fTestMethods.filter(filter);
	}

	public void sort(final Sorter sorter) {
		fTestMethods.filter(sorter);
	}
}