package org.junit.internal.runners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.description.TestDescription;
import org.junit.runner.notification.RunNotifier;

public class TestMethodRunner extends BeforeAndAfterRunner {
	private final Object fTest;
	private final Method fMethod;
	private final RunNotifier fNotifier;
	private final TestIntrospector fTestIntrospector;
	private final TestDescription fDescription;

	public TestMethodRunner(Object test, Method method, RunNotifier notifier, TestDescription description) {
		super(test.getClass(), Before.class, After.class);
		fTest= test;
		fMethod= method;
		fNotifier= notifier;
		fTestIntrospector= new TestIntrospector(test.getClass());
		fDescription= description;
	}

	public void run() {
		if (fTestIntrospector.isIgnored(fMethod)) {
			fNotifier.fireTestIgnored(fDescription);
			return;
		}
		fNotifier.fireTestStarted(fDescription);
		try {
			long timeout= fTestIntrospector.getTimeout(fMethod);
			if (timeout > 0)
				runWithTimeout(timeout);
			else
				runMethod();
		} finally {
			fNotifier.fireTestFinished(fDescription);
		}
	}

	private void runWithTimeout(long timeout) {
		ExecutorService service= Executors.newSingleThreadExecutor();
		Callable<Object> callable= new Callable<Object>() {
			public Object call() throws Exception {
				runMethod();
				return null;
			}
		};
		Future<Object> result= service.submit(callable);
		service.shutdown();
		try {
			boolean terminated= service.awaitTermination(timeout,
					TimeUnit.MILLISECONDS);
			if (!terminated)
				service.shutdownNow();
			result.get(timeout, TimeUnit.MILLISECONDS); // throws the exception if one occurred during the invocation
		} catch (Exception e) {
			addFailure(e);
		}		
	}
	
	private void runMethod() {
		runProtected();
	}
	
	@Override
	protected void runUnprotected() {
		try {
			fMethod.invoke(fTest);
			if (expectsException())
				addFailure(new AssertionError("Expected exception: " + expectedException().getName()));
		} catch (InvocationTargetException e) {
			if (!expectsException())
				addFailure(e.getTargetException());
			else if (isUnexpected(e.getTargetException()))
				addFailure(new AssertionError("Unexpected exception, expected<" + expectedException().getName() + "> but was<"
						+ e.getTargetException().getClass().getName() + ">"));
		} catch (Throwable e) {
			addFailure(e);
		}
	}

	@Override
	protected void invokeMethod(Method method) throws Exception {
		method.invoke(fTest);
	}
	
	@Override
	protected void addFailure(Throwable e) {
		fNotifier.fireTestFailure(new TestFailure(fDescription, e));
	}
	
	private boolean expectsException() {
		return expectedException() != null;
	}

	private Class<? extends Throwable> expectedException() {
		return fTestIntrospector.expectedException(fMethod);
	}

	private boolean isUnexpected(Throwable exception) {
		return ! exception.getClass().equals(expectedException());
	}
}

