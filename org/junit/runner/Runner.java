package org.junit.runner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import org.junit.Factory;
import org.junit.internal.runner.JUnit4RunnerStrategy;
import org.junit.internal.runner.PreJUnit4RunnerStrategy;
import org.junit.internal.runner.TestNotifier;


public class Runner implements TestNotifier {

	private int fCount= 0;
	private int fIgnoreCount= 0;
	private List<Failure> fFailures= new ArrayList<Failure>();
	private List<TestListener> fListeners= new ArrayList<TestListener>();
	private long fRunTime;

	public void run(Class... testClasses) {
		List<RunnerStrategy> strategies= new ArrayList<RunnerStrategy>();
		for (Class<? extends Object> each : testClasses)
			try {
				strategies.add(getStrategy(each));
			} catch (Exception e) {
				fireTestFailure(new Failure(e));
			}
		run(strategies);
	}

	public void run(junit.framework.Test test) { 
		ArrayList<RunnerStrategy> strategies= new ArrayList<RunnerStrategy>();
		strategies.add(new PreJUnit4RunnerStrategy(this, test));
		run(strategies);
	}
	
	private void run(List<RunnerStrategy> strategies) {
		int count= 0;
		for (RunnerStrategy each : strategies)
			count+= each.testCount();
		fireTestRunStarted(count);
		long startTime= System.currentTimeMillis();
		for (RunnerStrategy each : strategies)
			each.run();
		long endTime= System.currentTimeMillis();
		fRunTime+= endTime - startTime;
		fireTestRunFinished();
	}
	
	// TODO: Support AllTests here
	@SuppressWarnings("unchecked") 
	private RunnerStrategy getStrategy(Class<? extends Object> testClass) throws Exception {
		Factory annotation= testClass.getAnnotation(Factory.class);
		if (annotation != null) {
			Constructor constructor= annotation.value().getConstructor(new Class[] {TestNotifier.class, Class.class}); //TODO test if no such constructor
			return (RunnerStrategy) constructor.newInstance(new Object[] {this, testClass});
		} else if (isPre4Test(testClass))
			return new PreJUnit4RunnerStrategy(this, (Class< ? extends TestCase>) testClass); 
		else
			return new JUnit4RunnerStrategy(this, testClass);
	}

	private boolean isPre4Test(Class< ? extends Object> testClass) {
		return junit.framework.TestCase.class.isAssignableFrom(testClass);
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

	public int getIgnoreCount() {
		return fIgnoreCount;
	}
	
	public void addListener(TestListener listener) {
		fListeners.add(listener);
	}

	public void removeListener(TestListener listener) {
		fListeners.remove(listener);
	}

	public boolean wasSuccessful() {
		return getFailureCount() == 0;
	}

	private abstract class SafeNotifier {
		void run() {
			for (Iterator<TestListener> all= fListeners.iterator(); all.hasNext();) {
				try {
					notifyListener(all.next());
				} catch (Exception e) {
					all.remove();
					fireTestFailure(new Failure(e)); // Remove the offending listener first to avoid an infinite loop
				}
			}
		}
		
		abstract protected void notifyListener(TestListener each) throws Exception;
	}
	
	private void fireTestRunStarted(final int testCount) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(TestListener each) throws Exception {
				each.testRunStarted(testCount);
			};
		}.run();
	}
	
	private void fireTestRunFinished() {
		new SafeNotifier() {
			@Override
			protected void notifyListener(TestListener each) throws Exception {
				each.testRunFinished(Runner.this);
			};
		}.run();
	}
	
	public void fireTestStarted(final Object test, final String name) {
		fCount++;
		new SafeNotifier() {
			@Override
			protected void notifyListener(TestListener each) throws Exception {
				each.testStarted(test, name);
			};
		}.run();
	}

	public void fireTestFailure(final Failure failure) {
		fFailures.add(failure);
		new SafeNotifier() {
			@Override
			protected void notifyListener(TestListener each) throws Exception {
				each.testFailure(failure);
			};
		}.run();
	}

	public void fireTestIgnored(final Method method) {
		fIgnoreCount++;
		new SafeNotifier() {
			@Override
			protected void notifyListener(TestListener each) throws Exception {
				each.testIgnored(method);
			};
		}.run();
	}

	public void fireTestFinished(final Object test, final String name) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(TestListener each) throws Exception {
				each.testFinished(test, name);
			};
		}.run();
	}
	
}
