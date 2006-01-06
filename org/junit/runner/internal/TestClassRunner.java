package org.junit.runner.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.ClassRunner;
import org.junit.runner.InitializationErrorListener;
import org.junit.runner.RunNotifier;
import org.junit.runner.Runner;
import org.junit.runner.extensions.Filter;
import org.junit.runner.extensions.Filterable;
import org.junit.runner.plan.Plan;

public class TestClassRunner extends ClassRunner implements Filterable {
	private TestIntrospector fTestIntrospector;
	protected final Runner fEnclosedRunner;

	public TestClassRunner(Class<? extends Object> klass) {
		this(klass, new TestClassMethodsRunner(klass));
	}
	
	public TestClassRunner(Class<? extends Object> klass, Runner runner) {
		super(klass);
		fEnclosedRunner = runner;
		fTestIntrospector = new TestIntrospector(getTestClass());
	}

	@Override
	protected void addFatalErrors(ArrayList<Exception> fatalErrors) {
		fTestIntrospector.validateStaticMethods(fatalErrors);
	}
	
	@Override
	public boolean canRun(InitializationErrorListener notifier) {
		return super.canRun(notifier) & fEnclosedRunner.canRun(notifier);
	}

	public static class FailedBefore extends Exception {
		private static final long serialVersionUID = 1L;
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
