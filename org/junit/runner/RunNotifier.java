package org.junit.runner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.runner.plan.LeafPlan;
import org.junit.runner.plan.Plan;


public class RunNotifier implements InitializationErrorListener {

	private List<RunListener> fListeners= new ArrayList<RunListener>();
	
	public void addListener(RunListener listener) {
		fListeners.add(listener);
	}

	void removeListener(RunListener listener) {
		fListeners.remove(listener);
	}

	private abstract class SafeNotifier {
		void run() {
			for (Iterator<RunListener> all= fListeners.iterator(); all.hasNext();) {
				try {
					notifyListener(all.next());
				} catch (Exception e) {
					all.remove();
					fireNonTestFailure(e); // Remove the offending listener first to avoid an infinite loop
				}
			}
		}
		
		abstract protected void notifyListener(RunListener each) throws Exception;
	}
	
	void fireTestRunStarted(final Plan plan) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testRunStarted(plan);
			};
		}.run();
	}
	
	void fireTestRunFinished(final Result result) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testRunFinished(result);
			};
		}.run();
	}
	
	public void fireTestStarted(final LeafPlan plan) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testStarted(plan);
			};
		}.run();
	}

	public void fireTestFailure(final Failure failure) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testFailure(failure);
			};
		}.run();
	}

	public void fireTestIgnored(final LeafPlan plan) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testIgnored(plan);
			};
		}.run();
	}

	public void fireTestFinished(final LeafPlan plan) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testFinished(plan);
			};
		}.run();
	}

	public void initializationError(Throwable e) {
		fireNonTestFailure(e);
	}

	public void fireNonTestFailure(Throwable exception) {
		fireTestFailure(new Failure(exception));
	}
}