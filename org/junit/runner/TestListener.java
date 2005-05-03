package org.junit.runner;


public interface TestListener {

	void testRunStarted();
	void testRunFinished(Runner runner, long runTime);

	void testStarted(Object test, String name);
	void testFailure(Failure failure);

}
