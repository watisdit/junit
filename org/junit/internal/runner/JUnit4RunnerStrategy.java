package org.junit.internal.runner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.Failure;
import org.junit.runner.RunnerStrategy;

public class JUnit4RunnerStrategy implements RunnerStrategy {
	private final Class< ? extends Object> fTestClass;
	private final TestIntrospector fTestIntrospector;
	private TestNotifier fNotifier;

	public JUnit4RunnerStrategy(TestNotifier notifier, Class< ? extends Object> testClass) {
		fNotifier= notifier;
		fTestClass= testClass;
		fTestIntrospector= new TestIntrospector(fTestClass);
	}

	public int testCount() {
		return fTestIntrospector.getTestMethods(Test.class).size();
	}

	public void run() {
		List<Exception> errors= fTestIntrospector.validateTestMethods();
		if (!errors.isEmpty()) {
			for (Throwable each : errors)
				addFailure(new Failure(each));
			return;
		}
		try {
			List<Method> beforeMethods= fTestIntrospector.getTestMethods(BeforeClass.class);
			for (Method method : beforeMethods)
				method.invoke(null);
			List<Method> methods= fTestIntrospector.getTestMethods(Test.class);
			for (Method method : methods)
				invokeTestMethod(method);
			List<Method> afterMethods= fTestIntrospector.getTestMethods(AfterClass.class);
			for (Method method : afterMethods)
				method.invoke(null);
		} catch (Exception e) {
			addFailure(new Failure(e));
		}
	}

	private void addFailure(Failure failure) {
      		fNotifier.fireTestFailure(failure);
	}
	
	private void invokeTestMethod(Method method) throws Exception {
		Object test= fTestClass.getConstructor().newInstance();
		invokeTestMethod(test, method);
	}

	public void invokeTestMethod(Object test, Method method) throws Exception {
		if (fTestIntrospector.isIgnored(method)) {
			fNotifier.fireTestIgnored(method);
			return;
		}
		Test annotation= method.getAnnotation(Test.class);
		long timeout= annotation.timeout();
		if (timeout <= 0) {
			invoke(test, method);
		} else {
			invokeWithTimeout(test, method, timeout);
		}
	}

	private void invokeWithTimeout(final Object test, final Method method, long timeout) throws InterruptedException, ExecutionException, TimeoutException {
		ExecutorService service= Executors.newSingleThreadExecutor();
		Callable<Object> callable= new Callable<Object>() {
			public Object call() throws Exception {
				invoke(test, method);
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

	private void invoke(Object test, Method method) {
		invokeMethod(test, method);
	}

	private void invokeMethod(Object test, Method method) {
		fNotifier.fireTestStarted(test, method.getName());
		try {
			quietlyInvokeMethod(test, method);
		} finally {
			fNotifier.fireTestFinished(test, method.getName());
		}
	}

	private void quietlyInvokeMethod(Object test, Method method) {
		try {
			setUp(test);
		} catch (InvocationTargetException e) {
			addFailure(new TestFailure(test, method.getName(), e.getTargetException()));
			return;
		} catch (Throwable e) {
			addFailure(new TestFailure(test, method.getName(), e)); // TODO:
			return;
		}
		Class< ? extends Throwable> expected= expectedException(method);
		try {
			method.invoke(test);
			if (expectsException(method))
				addFailure(new TestFailure(test, method.getName(), new AssertionError("Expected exception: " + expected.getName())));
		} catch (InvocationTargetException e) {
			if (expected == null)
				addFailure(new TestFailure(test, method.getName(), e.getTargetException()));
			else if (isUnexpected(e.getTargetException(), method))
				addFailure(new TestFailure(test, method.getName(), new AssertionError("Unexpected exception, expected<" + expected.getName() + "> but was<" + e.getTargetException().getClass().getName() + ">")));
		} catch (Throwable e) {
			addFailure(new TestFailure(test, method.getName(), e)); // TODO:
		} finally {
			try {
				tearDown(test);
			} catch (InvocationTargetException e) {
				addFailure(new TestFailure(test, method.getName(), e.getTargetException()));
			} catch (Throwable e) {
				addFailure(new TestFailure(test, method.getName(), e)); // TODO:
			}
		}
	}

	private void setUp(Object test) throws Exception {
		List<Method> befores= fTestIntrospector.getTestMethods(Before.class);
		for (Method before : befores)
			before.invoke(test);
	}
	
	private void tearDown(Object test) throws Exception {
		List<Method> afters= fTestIntrospector.getTestMethods(After.class);
		for (Method after : afters)
			after.invoke(test);
	}

	private boolean isUnexpected(Throwable exception, Method method) {
		Class< ? extends Throwable> expected= expectedException(method);
		return !exception.getClass().equals(expected);
	}

	private boolean expectsException(Method method) {
		return expectedException(method) != null;
	}

	private Class< ? extends Throwable> expectedException(Method method) {
		Test annotation= method.getAnnotation(Test.class);
		if (annotation.expected() == Test.None.class)
			return null;
		else
			return annotation.expected();
	}

}
