package org.junit.internal.runners;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

public class TestMethodRunner extends BeforeAndAfterRunner {
	private final Object fTest;

	private final JavaMethod fJavaMethod;

	private final JavaTestInterpreter fInterpreter;

	public TestMethodRunner(Object test, JavaMethod method,
			RunNotifier notifier, Description description,
			JavaTestInterpreter interpreter) {
		super(test.getClass(), Before.class, After.class, test,
				new PerTestNotifier(notifier, description));
		fTest= test;
		fJavaMethod= method;
		fInterpreter= interpreter;
	}

	public void run() {
		if (fJavaMethod.isIgnored()) {
			fPerTestNotifier.fireTestIgnored();
			return;
		}
		fPerTestNotifier.fireTestStarted();
		try {
			long timeout= fJavaMethod.getTimeout();
			if (timeout > 0)
				runWithTimeout(timeout);
			else
				runMethod();
		} finally {
			fPerTestNotifier.fireTestFinished();
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
			result.get(timeout, TimeUnit.MILLISECONDS); // throws the exception
			// if one occurred
			// during the invocation
		} catch (TimeoutException e) {
			fPerTestNotifier.addFailure(new Exception(String.format(
					"test timed out after %d milliseconds", timeout)));
		} catch (Exception e) {
			fPerTestNotifier.addFailure(e);
		}
	}

	private void runMethod() {
		runProtected();
	}

	@Override
	protected void runUnprotected() {
		fJavaMethod.runUnprotected(fInterpreter, fTest, fPerTestNotifier);
	}
}
