package org.junit.runner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;



// TODO private listener
public class Result implements RunListener {
	private int fCount= 0;
	private int fIgnoreCount= 0;
	private List<Failure> fFailures= new ArrayList<Failure>();
	private long fRunTime= 0;
	private long fStartTime;

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

	public int getIgnoreCount() {
		return fIgnoreCount;
	}
	
	public boolean wasSuccessful() {
		return getFailureCount() == 0;
	}

	// RunListener implementation, not for external use
	public void testRunStarted(int testCount) throws Exception {
		fStartTime= System.currentTimeMillis();
	}

	public void testRunFinished(Result result) throws Exception {
		long endTime= System.currentTimeMillis();
		fRunTime+= endTime - fStartTime;
	}

	public void testStarted(Object test, String name) throws Exception {
		fCount++;
	}

	public void testFinished(Object test, String name) throws Exception {
	}

	public void testFailure(Failure failure) throws Exception {
		fFailures.add(failure);
	}

	public void testIgnored(Method method) throws Exception {
		fIgnoreCount++;
	}

	void addListenerTo(RunNotifier notifier) {
		notifier.addListener(this);
	}
}
