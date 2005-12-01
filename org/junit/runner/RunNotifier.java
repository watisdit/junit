package org.junit.runner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class RunNotifier {

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
					fireTestFailure(new Failure(e)); // Remove the offending listener first to avoid an infinite loop
				}
			}
		}
		
		abstract protected void notifyListener(RunListener each) throws Exception;
	}
	
	void fireTestRunStarted(final int testCount) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testRunStarted(testCount);
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
	
	public void fireTestStarted(final Object test, final String name) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testStarted(test, name);
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

	public void fireTestIgnored(final Method method) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testIgnored(method);
			};
		}.run();
	}

	public void fireTestFinished(final Object test, final String name) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testFinished(test, name);
			};
		}.run();
	}

}