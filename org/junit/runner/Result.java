package org.junit.runner;

import java.util.ArrayList;
import java.util.List;

public class Result {
	int fCount= 0;
	int fIgnoreCount= 0;
	List<Failure> fFailures= new ArrayList<Failure>();
	long fRunTime;

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
}
