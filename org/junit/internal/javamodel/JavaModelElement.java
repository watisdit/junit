package org.junit.internal.javamodel;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public abstract class JavaModelElement {
	public abstract String getName();

	public void addFailure(RunNotifier runNotifier, Throwable targetException) {
		runNotifier
				.fireTestFailure(new Failure(description(), targetException));
	}

	protected abstract Description description();
}
