/**
 * 
 */
package org.junit.runner.internal;

import org.junit.notify.RunNotifier;
import org.junit.plan.LeafPlan;
import org.junit.plan.Plan;
import org.junit.runner.Runner;

public class ErrorReportingRunner extends Runner {
	private final LeafPlan fPlan;

	private final Throwable fThrowable;

	public ErrorReportingRunner(LeafPlan plan, Throwable throwable) {
		fPlan = plan;
		fThrowable = throwable;
	}

	@Override
	public Plan getPlan() {
		return fPlan;
	}

	@Override
	public void run(RunNotifier notifier) {
		notifier.fireTestStarted(fPlan);
		notifier.fireTestFailure(new TestFailure(fPlan, fThrowable));
		notifier.fireTestFinished(fPlan);
	}
}