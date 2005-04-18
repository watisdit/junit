package junit;

import tries.Runner;

public interface ITestListener {

	void testStarted(Object test, String name);

	void testRunFinished(Runner runner);

	void failure(Throwable exception);

}
