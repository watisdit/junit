package org.junit.runner.internal;

import java.lang.reflect.Method;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.notify.RunNotifier;
import org.junit.plan.Plan;
import org.junit.runner.Runner;
import org.junit.runner.extensions.Filter;
import org.junit.runner.extensions.Filterable;

public class TestClassRunner extends ClassRunner implements Filterable {
	protected final Runner fEnclosedRunner;

	public TestClassRunner(Class<? extends Object> klass) throws InitializationError {
		this(klass, new TestClassMethodsRunner(klass));
	}
	
	public TestClassRunner(Class<? extends Object> klass, Runner runner) throws InitializationError {
		super(klass);
		fEnclosedRunner = runner;
		MethodValidator methodValidator = new MethodValidator(klass);
		methodValidator.validateStaticMethods();
		methodValidator.assertValid();
	}

	@Override
	public void run(final RunNotifier notifier) {
		BeforeAndAfterRunner runner = new BeforeAndAfterRunner(getTestClass(), BeforeClass.class, AfterClass.class) {		
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
	public Plan getPlan() {
		return fEnclosedRunner.getPlan();
	}

	public void filter(Filter filter) {
		filter.apply(fEnclosedRunner);
	}
}
