package org.junit.runner;


public interface TestListener {

	void testRunStarted();
	
	void testRunFinished(Runner runner);
	
	public void testStarted(Object test, String name);

	public void testFailure(Failure failure);

	void testIgnored(Object method);


}
