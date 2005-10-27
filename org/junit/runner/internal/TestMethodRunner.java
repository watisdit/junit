package org.junit.runner.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunNotifier;

public class TestMethodRunner {

	private class FailedBefore extends Exception {
		private static final long serialVersionUID= 1L;
	}

	private final Object fTest;
	final Method fMethod;
	private final RunNotifier fNotifier;
	private final TestIntrospector fTestIntrospector;

	public TestMethodRunner(Object test, Method method, RunNotifier notifier) {
		fTest= test;
		fMethod= method;
		fNotifier= notifier;
		fTestIntrospector= new TestIntrospector(test.getClass());
	}

	public void run() throws Exception {
		if (fTestIntrospector.isIgnored(fMethod)) {
			fNotifier.fireTestIgnored(fMethod);
			return;
		}
		long timeout= fTestIntrospector.getTimeout(fMethod);
		if (timeout > 0)
			runWithTimeout(timeout);
		else
			runMethod();
	}

	private void runWithTimeout(long timeout) throws Exception {
		ExecutorService service= Executors.newSingleThreadExecutor();
		Callable<Object> callable= new Callable<Object>() {
			public Object call() throws Exception {
				runMethod();
				return null;
			}
		};
		Future<Object> result= service.submit(callable);
		service.shutdown();
		boolean terminated= service.awaitTermination(timeout, TimeUnit.MILLISECONDS);
		if (!terminated)
			service.shutdownNow();
		result.get(timeout, TimeUnit.MILLISECONDS); // throws the exception if one occurred during the invocation
	}
	
	private void runMethod() {
		fNotifier.fireTestStarted(fTest, fMethod.getName());
		try {
			quietlyRunMethod();
		} finally {
			fNotifier.fireTestFinished(fTest, fMethod.getName());
		}
	}

	private void quietlyRunMethod() {
		try {
			runBefores();
			runTestMethod();
		} catch (FailedBefore e) {
		} finally {
			runAfters();
		}
	}
	
	// Stop after first failed @Before
	private void runBefores() throws FailedBefore {
		try {
			List<Method> befores= fTestIntrospector.getTestMethods(Before.class);
			for (Method before : befores)
				before.invoke(fTest);
		} catch (InvocationTargetException e) {
			addFailure(e.getTargetException());
			throw new FailedBefore();
		} catch (Throwable e) {
			addFailure(e);
			throw new FailedBefore();
		}
	}

	private void runTestMethod() {
		Class< ? extends Throwable> expected= fTestIntrospector.expectedException(fMethod);
		try {
			invokeMethod();
			if (expectsException())
				addFailure(new AssertionError("Expected exception: " + expected.getName()));
		} catch (InvocationTargetException e) {
			if (!expectsException())
				addFailure(e.getTargetException());
			else if (isUnexpected(e.getTargetException()))
				addFailure(new AssertionError("Unexpected exception, expected<" + expected.getName() + "> but was<"
						+ e.getTargetException().getClass().getName() + ">"));
		} catch (Throwable e) {
			addFailure(e);
		}
	}

	protected void invokeMethod() throws Exception {
		fMethod.invoke(fTest);
	}

	// Try to run all @Afters regardless
	private void runAfters() {
		List<Method> afters= fTestIntrospector.getTestMethods(After.class);
		for (Method after : afters)
			try {
				after.invoke(fTest);
			} catch (InvocationTargetException e) {
				addFailure(e.getTargetException());
			} catch (Throwable e) {
				addFailure(e); // TODO:
			}
	}
	
	private boolean expectsException() {
		return fTestIntrospector.expectedException(fMethod) != null;
	}

	private boolean isUnexpected(Throwable exception) {
		Class< ? extends Throwable> expected= fTestIntrospector.expectedException(fMethod);
		return ! exception.getClass().equals(expected);
	}

	private void addFailure(Throwable e) {
		fNotifier.fireTestFailure(new TestFailure(fTest, fMethod.getName(), e));
	}
}

