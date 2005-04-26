package org.junit.tries;

import java.util.ArrayList;
import java.util.List;

import org.junit.TestListener;

public class Runner {

	private int fCount= 0;
	private List<Throwable> fFailures= new ArrayList<Throwable>();
	private List<TestListener> fListeners= new ArrayList<TestListener>();

	public void run(Class testClass) throws Exception {
		long startTime= System.currentTimeMillis();
		if (junit.framework.TestCase.class.isAssignableFrom(testClass)) {
			new PreJUnit4TestCaseRunner(this, testClass).runCompatibleTest();
		} else {
	//TODO: public void run(TestSuite suite) ...
			new JUnit4TestRunner(this, testClass).run();
		}
		long endTime= System.currentTimeMillis();
		for (TestListener each : fListeners)
			each.testRunFinished(this, endTime - startTime);
	}

	void addFailure(Throwable exception) {
		fFailures.add(exception); //TODO Add a TestFailure that includes the test that failed
		for (TestListener each : fListeners)
			each.failure(exception);
	}

	public int getRunCount() {
		return fCount;
	}

	public int getFailureCount() {
		return fFailures.size();
	}

	public List<Throwable> getFailures() {
		return fFailures;
	}

	public void addListener(TestListener listener) {
		fListeners.add(listener);
	}

	public boolean wasSuccessful() {
		return getFailureCount() == 0;
	}

	List<TestListener> getListeners() {
		return fListeners;
	}

	void startTestCase(Object test, String name) {
		fCount++;
		for (TestListener each : getListeners())
			each.testStarted(test, name);
	}

}
