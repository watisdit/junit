package org.junit.runner;

import org.junit.runner.internal.TestNotifier;

public interface Runner {

	void initialize(TestNotifier notifier, Class< ? extends Object> klass);
	
	void run();

	int testCount();

}