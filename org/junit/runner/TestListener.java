package org.junit.runner;


public interface TestListener {

	void testRunStarted(int testCount);
	
	void testRunFinished(Runner runner); //TODO: Something is wrong here... Should take an event anyway
	
	void testStarted(Object test, String name);

	void testFinished(Object test, String name);

	void testFailure(Failure failure);

	void testIgnored(Object method);

}
