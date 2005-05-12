package org.junit.runner;

import java.util.ArrayList;
import java.util.List;

import org.junit.internal.runner.JUnit4Strategy;
import org.junit.internal.runner.PreJUnit4Strategy;
import org.junit.internal.runner.RunnerStrategy;


public class Runner {

	private int fCount= 0;
	private List<Failure> fFailures= new ArrayList<Failure>();
	private List<TestListener> fListeners= new ArrayList<TestListener>();
	private long fRunTime;

	public void run(Class... testClasses) {
		for (TestListener each : fListeners)
			each.testRunStarted();
		long startTime= System.currentTimeMillis();
		for (Class<? extends Object> each : testClasses)
			getStrategy(each).run();
		long endTime= System.currentTimeMillis();
		fRunTime+= endTime - startTime;
		for (TestListener each : fListeners)
			each.testRunFinished(this);
	}
	
	private RunnerStrategy getStrategy(Class<? extends Object> testClass) {
		if (junit.framework.TestCase.class.isAssignableFrom(testClass))
			return new PreJUnit4Strategy(this, testClass); 
		return new JUnit4Strategy(this, testClass);
	}

	public void run(junit.framework.Test test) { 
		for (TestListener each : fListeners)
			each.testRunStarted();
		long startTime= System.currentTimeMillis();
		new PreJUnit4Strategy(this, test).run();
		long endTime= System.currentTimeMillis();
		fRunTime+= endTime - startTime;
		for (TestListener each : fListeners)
			each.testRunFinished(this);
	}

	public void addFailure(Failure failure) {
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

	public long getRunTime() {
		return fRunTime;
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

	public void startTestCase(Object test, String name) {
		fCount++;
		for (TestListener each : getListeners())
			each.testStarted(test, name);
	}

}
