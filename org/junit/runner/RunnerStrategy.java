package org.junit.runner;

import org.junit.internal.runner.TestNotifier;

public interface RunnerStrategy {

	void initialize(TestNotifier notifier, Class< ? extends Object> klass);
	
	void run();

	int testCount();

}