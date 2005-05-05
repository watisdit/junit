package org.junit.runner;

import java.util.ArrayList;
import java.util.List;


public class Runner {

	private int fCount= 0;
	private List<Failure> fFailures= new ArrayList<Failure>();
	private List<TestListener> fListeners= new ArrayList<TestListener>();

	public void run(Class... testClasses) throws Exception {
		for (TestListener each : fListeners)
			each.testRunStarted();
		long startTime= System.currentTimeMillis();
		for (Class<? extends Object> each : testClasses)
			getStrategy(each).run();
		long endTime= System.currentTimeMillis();
		for (TestListener each : fListeners)
			each.testRunFinished(this, endTime - startTime);
	}
	
	private RunnerStrategy getStrategy(Class<? extends Object> testClass) {
		if (junit.framework.TestCase.class.isAssignableFrom(testClass))
			return new PreJUnit4TestCaseRunner(this, testClass); 
		return new JUnit4TestRunner(this, testClass);
	}

	public void run(junit.framework.Test test) {
		for (TestListener each : fListeners)
			each.testRunStarted();
		long startTime= System.currentTimeMillis();
		new PreJUnit4TestCaseRunner(this, test).run();
		long endTime= System.currentTimeMillis();
		for (TestListener each : fListeners)
			each.testRunFinished(this, endTime - startTime);
	}

	void addFailure(Failure failure) {
		fFailures.add(failure);
		for (TestListener each : fListeners)
			each.testFailure(failure);
	}

	public int getRunCount() {
		return fCount;
	}

	public int getFailureCount() {
		return fFailures.size();
	}

	public List<Failure> getFailures() {
		return fFailures;
	}

	public void addListener(TestListener listener) {
		fListeners.add(listener);
	}

	public boolean wasSuccessful() {
		return getFailureCount() == 0;
	}

	private List<TestListener> getListeners() {
		return fListeners;
	}

	void startTestCase(Object test, String name) {
		fCount++;
		for (TestListener each : getListeners())
			each.testStarted(test, name);
	}

}
