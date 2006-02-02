package org.junit.internal.runners;

import java.lang.reflect.Method;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;

public class TestClassRunner extends Runner implements Filterable, Sortable {
	protected final Runner fEnclosedRunner;
	private final Class<? extends Object> fTestClass;

	public TestClassRunner(Class<? extends Object> klass) throws InitializationError {
		this(klass, new TestClassMethodsRunner(klass));
	}
	
	public TestClassRunner(Class<? extends Object> klass, Runner runner) throws InitializationError {
		fTestClass= klass;
		fEnclosedRunner= runner;
		MethodValidator methodValidator= new MethodValidator(klass);
		methodValidator.validateStaticMethods();
		methodValidator.assertValid();
	}

	@Override
	public void run(final RunNotifier notifier) {
		BeforeAndAfterRunner runner= new BeforeAndAfterRunner(getTestClass(), BeforeClass.class, AfterClass.class) {		
			@Override
			protected void runUnprotected() {
				fEnclosedRunner.run(notifier);
			}
		
			@Override
			protected void invokeMethod(Method method) throws Exception {
				method.invoke(null);
			}
		
			@Override
			protected void addFailure(Throwable targetException) {
				notifier.fireNonTestFailure(targetException);
			}
		
		};
		
		runner.runProtected();
	}

	@Override
	public Description getDescription() {
		return fEnclosedRunner.getDescription();
	}

	// TODO: dup?
	public void filter(Filter filter) {
		filter.apply(fEnclosedRunner);
	}

	public void sort(Sorter sorter) {
		sorter.apply(fEnclosedRunner);
	}

	protected Class<? extends Object> getTestClass() {
		return fTestClass;
	}
}
