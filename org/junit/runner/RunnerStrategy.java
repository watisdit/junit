package org.junit.runner;



public interface RunnerStrategy {

	// TODO is there a better way to express this?
	// Runner strategies also need to implement a constructor RunnerStrategy(TestNotifier, Class)
	// perhaps there should be an initialize(TestNotifier, Class)?
	
	public void run();

	public int testCount();

}