package org.junit.runner;

import org.junit.notify.RunNotifier;
import org.junit.plan.Plan;

public abstract class Runner {
	public abstract Plan getPlan();

	public abstract void run(RunNotifier notifier);
	
	public int testCount() {
		return getPlan().testCount();
	}
}