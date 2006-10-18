package org.junit.internal.runners;

import java.lang.reflect.InvocationTargetException;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public class PerTestNotifier {
	// TODO: push out

	private final RunNotifier fNotifier;

	private final Description fDescription;

	public PerTestNotifier(RunNotifier notifier, Description description) {
		fNotifier= notifier;
		fDescription= description;
	}

	public void addFailure(Throwable targetException) {
		if (targetException instanceof InvocationTargetException) {
			InvocationTargetException i= (InvocationTargetException) targetException;
			addFailure(i.getTargetException());
			return;
		}
		fNotifier.fireTestFailure(new Failure(fDescription, targetException));
	}

	public void fireTestIgnored() {
		fNotifier.fireTestIgnored(fDescription);
	}

	public void fireTestFinished() {
		fNotifier.fireTestFinished(fDescription);
	}

	public void fireTestStarted() {
		fNotifier.fireTestStarted(fDescription);
	}
}
