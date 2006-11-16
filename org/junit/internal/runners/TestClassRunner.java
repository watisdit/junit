package org.junit.internal.runners;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;

public class TestClassRunner extends Runner implements Filterable, Sortable {
	protected final Runner fEnclosedRunner;

	private final Class<?> fTestClass;

	private final JavaTestInterpreter fInterpreter;

	public TestClassRunner(Class<?> klass) throws InitializationError {
		this(klass, new JavaTestInterpreter());
	}

	public TestClassRunner(Class<?> klass, JavaTestInterpreter interpreter)
			throws InitializationError {
		fTestClass= klass;
		fInterpreter= interpreter;
		fEnclosedRunner= interpreter.buildRunner(klass);
	}

	@Override
	public void run(final RunNotifier notifier) {
		Runnable protectThis= new Runnable() {
			public void run() {
				fEnclosedRunner.run(notifier);
			}
		};

		fInterpreter.buildClass(getTestClass()).runWithBeforeAndAfter(protectThis, null,
				notifier);
	}

	@Override
	public Description getDescription() {
		return fEnclosedRunner.getDescription();
	}

	// TODO: good behavior when createTest fails

	// TODO: dup?
	public void filter(Filter filter) throws NoTestsRemainException {
		filter.apply(fEnclosedRunner);
	}

	public void sort(Sorter sorter) {
		sorter.apply(fEnclosedRunner);
	}

	protected Class<?> getTestClass() {
		return fTestClass;
	}
}
