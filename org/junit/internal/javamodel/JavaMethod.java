/**
 * 
 */
package org.junit.internal.javamodel;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Test.None;
import org.junit.internal.runners.MethodAnnotation;
import org.junit.internal.runners.TestEnvironment;
import org.junit.runner.Description;

// TODO: check names of various "run" methods

public class JavaMethod extends WrappedJavaModelElement {
	// TODO: push out
	private final Method fMethod;

	private final JavaClass fJavaClass;

	public JavaMethod(JavaClass javaClass, Method current) {
		fJavaClass= javaClass;
		fMethod= current;
	}

	public Annotation getAnnotation(MethodAnnotation methodAnnotation) {
		return fMethod.getAnnotation(methodAnnotation.getAnnotationClass());
	}

	boolean isIgnored() {
		return fMethod.getAnnotation(Ignore.class) != null;
	}

	long getTimeout() {
		return getTestAnnotation().timeout();
	}

	private Test getTestAnnotation() {
		return fMethod.getAnnotation(Test.class);
	}

	Class<? extends Throwable> expectedException() {
		Test annotation= getTestAnnotation();
		if (annotation.expected() == None.class)
			return null;
		else
			return annotation.expected();
	}

	boolean isUnexpected(Throwable exception) {
		return !expectedException().isAssignableFrom(exception.getClass());
	}

	boolean expectsException() {
		return expectedException() != null;
	}

	public Object invoke(Object object) throws IllegalAccessException,
			InvocationTargetException {
		return fMethod.invoke(object);
	}

	@Override
	public String getName() {
		return fMethod.getName();
	}

	// TODO: push out
	@Override
	public Description description() {
		return Description.createTestDescription(fJavaClass.getTestClass(),
				getName());
	}

	// TODO: push out
	@Override
	public JavaClass getJavaClass() {
		return fJavaClass;
	}

	@Override
	public Class<? extends Annotation> getAfterAnnotation() {
		return After.class;
	}

	@Override
	public Class<? extends Annotation> getBeforeAnnotation() {
		return Before.class;
	}

	public void runWithoutBeforeAndAfter(TestEnvironment environment,
			Object test) {
		// TODO: is this envious of environment?
		try {
			environment.getInterpreter().executeMethodBody(test, this);
			if (expectsException())
				addFailure(environment.getRunNotifier(), new AssertionError(
						"Expected exception: " + expectedException().getName()));
		} catch (InvocationTargetException e) {
			Throwable actual= e.getTargetException();
			if (!expectsException())
				addFailure(environment.getRunNotifier(), actual);
			else if (isUnexpected(actual)) {
				String message= "Unexpected exception, expected<"
						+ expectedException().getName() + "> but was<"
						+ actual.getClass().getName() + ">";
				addFailure(environment.getRunNotifier(), new Exception(message,
						actual));
			}
		} catch (Throwable e) {
			// TODO: DUP on environment.getRunNotifier
			addFailure(environment.getRunNotifier(), e);
		}
	}

	void invokeTestMethod(TestEnvironment testEnvironment) {
		Object test;
		try {
			test= getJavaClass().newInstance();
		} catch (Exception e) {
			testEnvironment.getRunNotifier().testAborted(description(), e);
			return;
		}
		run(testEnvironment, test);
	}

	void runWithoutTimeout(final Object test,
			final TestEnvironment testEnvironment) {
		// TODO: is this envious now? Yes
		runWithBeforeAndAfter(new Runnable() {
			public void run() {
				runWithoutBeforeAndAfter(testEnvironment, test);
			}
		}, test, testEnvironment.getRunNotifier());
		// TODO: ugly
	}

	void runWiteTimeout(long timeout, final Object test,
			final TestEnvironment testEnvironment) {
		ExecutorService service= Executors.newSingleThreadExecutor();
		Callable<Object> callable= new Callable<Object>() {
			public Object call() throws Exception {
				runWithoutTimeout(test, testEnvironment);
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
			addFailure(testEnvironment.getRunNotifier(), new Exception(String
					.format("test timed out after %d milliseconds", timeout)));
		} catch (Exception e) {
			// TODO: DUP
			addFailure(testEnvironment.getRunNotifier(), e);
		}
	}

	void run(TestEnvironment environment, Object test) {
		if (isIgnored()) {
			environment.getRunNotifier().fireTestIgnored(description());
			return;
		}
		environment.getRunNotifier().fireTestStarted(description());
		try {
			long timeout= getTimeout();
			if (timeout > 0)
				runWiteTimeout(timeout, test, environment);
			else
				runWithoutTimeout(test, environment);
		} finally {
			environment.getRunNotifier().fireTestFinished(description());
		}
	}
}