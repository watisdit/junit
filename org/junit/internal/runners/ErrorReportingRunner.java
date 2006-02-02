/**
 * 
 */
package org.junit.internal.runners;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

public class ErrorReportingRunner extends Runner {
	private final Description fDescription;

	private final Throwable fThrowable;

	public ErrorReportingRunner(Description description, Throwable throwable) {
		fDescription= description;
		fThrowable= throwable;
	}

	@Override
	public Description getDescription() {
		return fDescription;
	}

	@Override
	public void run(RunNotifier notifier) {
		notifier.fireTestStarted(fDescription);
		notifier.fireTestFailure(new TestFailure(fDescription, fThrowable));
		notifier.fireTestFinished(fDescription);
	}
}