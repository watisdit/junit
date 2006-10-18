package org.junit.internal.runners;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public abstract class JavaModelElement {
	public abstract String getName();

	public abstract Class<? extends Annotation> getBeforeAnnotation();

	public abstract Class<? extends Annotation> getAfterAnnotation();

	public abstract JavaClass getJavaClass();

	public void addFailure(RunNotifier runNotifier, Throwable targetException) {
		runNotifier.fireTestFailure(new Failure(description(), targetException));
	}

	protected abstract Description description();

	void runAfters(Object test, RunNotifier runNotifier) {
		// TODO: train wreck
		for (Method after : getJavaClass().getMethods(
				getAfterAnnotation())) {
			try {
				after.invoke(test);
			} catch (Throwable e) {
				addFailure(runNotifier, e);
			}
		}
	}

	void runBefores(Object test, RunNotifier runNotifier)
			throws FailedBefore {
		try {
			List<Method> befores= getJavaClass().getMethods(
					getBeforeAnnotation());
	
			// TODO: no auto-correct if wrong type on left side of new-style
			// for?
			for (Method before : befores)
				before.invoke(test);
		} catch (Throwable e) {
			addFailure(runNotifier, e);
			throw new FailedBefore();
		}
	}

	void runWithBeforeAndAfter(Runnable protectThis, Object test, RunNotifier runNotifier) {
		try {
			runBefores(test, runNotifier);
			protectThis.run();
		} catch (FailedBefore e) {
		} finally {
			runAfters(test, runNotifier);
		}
	}
}
