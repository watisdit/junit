package org.junit.runner;

import java.util.ArrayList;
import java.util.List;

import org.junit.notify.Failure;
import org.junit.notify.RunListener;
import org.junit.plan.LeafPlan;
import org.junit.plan.Plan;

public class Result {
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

	private class Listener implements RunListener {
		public void testRunStarted(Plan plan) throws Exception {
			fStartTime= System.currentTimeMillis();
		}

		public void testRunFinished(Result result) throws Exception {
			long endTime= System.currentTimeMillis();
			fRunTime+= endTime - fStartTime;
		}

		public void testStarted(LeafPlan plan) throws Exception {
			fCount++;
		}

		public void testFinished(LeafPlan plan) throws Exception {
		}

		public void testFailure(Failure failure) throws Exception {
			fFailures.add(failure);
		}

		public void testIgnored(LeafPlan plan) throws Exception {
			fIgnoreCount++;
		}
	}

	public RunListener createListener() {
		return new Listener();
	}
}
