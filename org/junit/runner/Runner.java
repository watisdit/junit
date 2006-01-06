package org.junit.runner;

import java.util.ArrayList;

import org.junit.runner.plan.Plan;



public abstract class Runner {
	public abstract Plan getPlan();

	public abstract void run(RunNotifier notifier);
	
	public boolean canRun(InitializationErrorListener notifier) {
		ArrayList<Exception> fatalErrors = new ArrayList<Exception>();
		addFatalErrors(fatalErrors);
		for (Exception exception : fatalErrors) {
			notifier.initializationError(exception);
		}
		return fatalErrors.isEmpty();
	}

	protected void addFatalErrors(ArrayList<Exception> fatalErrors) {
		// add nothing
	}

	public int testCount() {
		return getPlan().testCount();
	}
}