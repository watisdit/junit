package org.junit;

import org.junit.tries.Runner;

public interface TestListener {

	void testStarted(Object test, String name);

	void testRunFinished(Runner runner, long runTime);

	void failure(Throwable exception);

}
