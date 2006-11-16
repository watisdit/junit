package org.junit.internal.runners;

import org.junit.internal.javamodel.JavaClass;
import org.junit.internal.javamodel.JavaMethodList;
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

	// This assumes that some containing runner will perform validation of the
	// test methods
	public TestClassMethodsRunner(JavaClass klass,
			JavaTestInterpreter javaTestInterpreter) {
		fTestMethods= klass.getTestMethods(javaTestInterpreter);
	}

	// TODO: rename method to element?

	@Override
	public void run(RunNotifier notifier) {
		fTestMethods.run(notifier);
	}

	@Override
	public Description getDescription() {
		return fTestMethods.getDescription();
	}

	public void filter(Filter filter) throws NoTestsRemainException {
		fTestMethods.filter(filter);
	}

	public void sort(final Sorter sorter) {
		fTestMethods.sort(sorter);
	}
}