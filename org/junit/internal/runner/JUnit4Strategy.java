package org.junit.internal.runner;

import java.lang.reflect.Constructor;
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

public class JUnit4Strategy implements RunnerStrategy {
	private final Class< ? extends Object> fTestClass;
	private final TestIntrospector fTestIntrospector;
	private TestNotifier fNotifier;

	public JUnit4Strategy(TestNotifier notifier, Class< ? extends Object> testClass) {
		fNotifier= notifier;
		fTestClass= testClass;
		fTestIntrospector= new TestIntrospector(fTestClass);
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
			for (Method method : methods) {
				if (fTestIntrospector.isIgnored(method)) {
					fNotifier.fireTestIgnored(method);
				} else {
					long timeout= fTestIntrospector.getTimeout(method);
					if (timeout <= 0) {
						invoke(method);
					} else {
						invokeWithTimeout(method, timeout);
					} 					
				}
			}
			List<Method> afterMethods= fTestIntrospector.getTestMethods(AfterClass.class);
			for (Method method : afterMethods)
				method.invoke(null);
		} catch (Exception e) {
			addFailure(new Failure(e));
		}
	}

	private void invokeWithTimeout(final Method method, long timeout) throws InterruptedException, ExecutionException, TimeoutException {
		ExecutorService service= Executors.newSingleThreadExecutor();
		
		Callable<Object> callable= new Callable<Object>() {
			public Object call() throws Exception {
				invoke(method);
				return null;
			}
		};
		Future<Object> result= service.submit(callable);
		service.shutdown();
		boolean terminated= service.awaitTermination(timeout, TimeUnit.MILLISECONDS);
		if (!terminated) 
			service.shutdownNow();
		//TODO how do we handle the hardcoded timeout, it is required to handle the infinite loop case
		result.get(1000, TimeUnit.MILLISECONDS); // throws the exception if one occurred during the invocation
	}

	private void invoke(Method method) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Constructor<? extends Object> constructor= fTestClass.getConstructor();
		Object test= constructor.newInstance();
		invokeMethod(test, method);
	}

	void addFailure(Failure failure) {
		fNotifier.fireTestFailure(failure);
	}

	private void invokeMethod(Object test, Method method) {
		fNotifier.fireTestStarted(test, method.getName());
		try {
			setUp(test);
		} catch (InvocationTargetException e) {
			addFailure(new TestFailure(test, method.getName(), e.getTargetException()));
			return;
		} catch (Throwable e) {
			addFailure(new TestFailure(test, method.getName(), e)); // TODO:
			return;
		}
		try {
			method.invoke(test);
			Class< ? extends Throwable> expected= fTestIntrospector.expectedException(method);
			if (fTestIntrospector.expectsException(method)) {
				addFailure(new TestFailure(test, method.getName(), new AssertionError("Expected exception: " + expected.getName())));
			}
		} catch (InvocationTargetException e) {
			if (fTestIntrospector.isUnexpected(e.getTargetException(), method))
				addFailure(new TestFailure(test, method.getName(), e.getTargetException()));
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

	public void tearDown(Object test) throws Exception {
		List<Method> afters= fTestIntrospector.getTestMethods(After.class);
		for (Method after : afters)
			after.invoke(test);
	}

	public void setUp(Object test) throws Exception {
		List<Method> befores= fTestIntrospector.getTestMethods(Before.class);
		for (Method before : befores)
			before.invoke(test);
	}

}
