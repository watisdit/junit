package org.junit.tests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunNotifier;
import org.junit.runner.StoppedByUserException;
import org.junit.runner.internal.TestMethodRunner;
import org.junit.runner.plan.LeafPlan;
import org.junit.runner.request.Request;

public class UserStopTest {
	private RunNotifier fNotifier;

	@Before public void createNotifier() {
		fNotifier = new RunNotifier();
		fNotifier.pleaseStop();		
	}
	
	@Test(expected=StoppedByUserException.class) public void userStop() throws StoppedByUserException {
		fNotifier.fireTestStarted(null);
	}

	@Test(expected=StoppedByUserException.class) public void stopMethodRunner() throws Exception {
		new TestMethodRunner(this, getClass().getMethod("userStop"), fNotifier,
				new LeafPlan(getClass(), "userStop")).run();
	}
	
	public static class OneTest {
		@Test public void foo() {}
	}
	
	@Test(expected=StoppedByUserException.class) public void stopClassRunner() throws Exception {
		Request.aClass(OneTest.class).getRunner(fNotifier).run(fNotifier);
	}
}
