package org.junit.extensions;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.junit.internal.runner.PreJUnit4RunnerStrategy;
import org.junit.internal.runner.TestNotifier;
import org.junit.runner.RunnerStrategy;

public class AllTests implements RunnerStrategy {
	
	private PreJUnit4RunnerStrategy fStrategy;

	@SuppressWarnings("unchecked")
	public void initialize(TestNotifier notifier, Class< ? extends Object> testClass) {
		Test suite= new TestSuite((Class) testClass);
		fStrategy= new PreJUnit4RunnerStrategy();
		fStrategy.initialize(notifier, suite);
	}

	public void run() {
		fStrategy.run();
	}

	public int testCount() {
		return fStrategy.testCount();
	}
}
