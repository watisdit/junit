package org.junit.runner;

import org.junit.runner.internal.RunNotifier;

public interface Runner {

	void initialize(RunNotifier notifier, Class< ? extends Object> klass);
	
	void run();

	int testCount();

}