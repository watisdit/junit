package org.junit.runner.extensions;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.junit.runner.Runner;
import org.junit.runner.internal.OldTestClassRunner;
import org.junit.runner.internal.RunNotifier;

public class AllTests implements Runner {
	
	private OldTestClassRunner fStrategy;

	@SuppressWarnings("unchecked")
	public void initialize(RunNotifier notifier, Class< ? extends Object> testClass) {
		Test suite= new TestSuite((Class) testClass);
		fStrategy= new OldTestClassRunner();
		fStrategy.initialize(notifier, suite);
	}

	public void run() {
		fStrategy.run();
	}

	public int testCount() {
		return fStrategy.testCount();
	}
}
