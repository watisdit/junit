package org.junit.runner;


public interface TestListener {

	void testRunStarted();
	
	void testRunFinished(Runner runner); //TODO: Something is wrong here... Should take an event anyway
	
	public void testStarted(Object test, String name);

	public void testFailure(Failure failure);

	void testIgnored(Object method);


}
