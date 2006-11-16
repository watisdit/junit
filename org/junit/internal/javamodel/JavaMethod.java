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
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

// TODO: check names of various "run" methods

public class JavaMethod extends WrappedJavaModelElement {
	// TODO: push out
	private final Method fMethod;

	private final JavaClass fJavaClass;

	public JavaMethod(JavaClass javaClass, Method current) {
		super(javaClass.getInterpreter());
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
	public Description getDescription() {
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

	public void runWithoutBeforeAndAfter(RunNotifier notifier, Object test) {
		// TODO: is this envious of environment?
		try {
			getInterpreter().executeMethodBody(test, this);
			if (expectsException())
				addFailure(notifier, new AssertionError("Expected exception: "
						+ expectedException().getName()));
		} catch (InvocationTargetException e) {
			Throwable actual= e.getTargetException();
			if (!expectsException())
				addFailure(notifier, actual);
			else if (isUnexpected(actual)) {
				String message= "Unexpected exception, expected<"
						+ expectedException().getName() + "> but was<"
						+ actual.getClass().getName() + ">";
				addFailure(notifier, new Exception(message, actual));
			}
		} catch (Throwable e) {
			// TODO: DUP on environment.getRunNotifier
			addFailure(notifier, e);
		}
	}

	void invokeTestMethod(RunNotifier notifier) {
		Object test;
		try {
			test= getJavaClass().newInstance();
		} catch (Exception e) {
			notifier.testAborted(getDescription(), e);
			return;
		}
		run(notifier, test);
	}

	void runWithoutTimeout(final Object test, final RunNotifier notifier) {
		// TODO: is this envious now? Yes
		runWithBeforeAndAfter(new Runnable() {
			public void run() {
				runWithoutBeforeAndAfter(notifier, test);
			}
		}, test, notifier);
		// TODO: ugly
	}

	void runWiteTimeout(long timeout, final Object test,
			final RunNotifier notifier) {
		ExecutorService service= Executors.newSingleThreadExecutor();
		Callable<Object> callable= new Callable<Object>() {
			public Object call() throws Exception {
				runWithoutTimeout(test, notifier);
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
			addFailure(notifier, new Exception(String.format(
					"test timed out after %d milliseconds", timeout)));
		} catch (Exception e) {
			// TODO: DUP
			addFailure(notifier, e);
		}
	}

	void run(RunNotifier notifier, Object test) {
		if (isIgnored()) {
			notifier.fireTestIgnored(getDescription());
			return;
		}
		notifier.fireTestStarted(getDescription());
		try {
			long timeout= getTimeout();
			if (timeout > 0)
				runWiteTimeout(timeout, test, notifier);
			else
				runWithoutTimeout(test, notifier);
		} finally {
			notifier.fireTestFinished(getDescription());
		}
	}

	@Override
	public void run(RunNotifier notifier) {
		invokeTestMethod(notifier);
	}
}