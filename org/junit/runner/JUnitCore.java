package org.junit.runner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.runner.Version;
import org.junit.runner.internal.OldTestClassRunner;
import org.junit.runner.internal.TestClassRunner;
import org.junit.runner.internal.TestNotifier;
import org.junit.runner.internal.TextListener;


public class JUnitCore implements TestNotifier {
	
	private Result fResult= new Result();
	
	private List<RunListener> fListeners= new ArrayList<RunListener>();

	public JUnitCore() {
	}

	public static void main(String... args) {
		new JUnitCore().runMain(args);
	}
	
	// TODO: better name?  Needed?
	public static Result runClasses(Class... classes) {
		JUnitCore core= new JUnitCore();
		RunListener listener= new TextListener();
		core.addListener(listener);
		return core.run(classes);
	}
	
	private void runMain(String... args) {
		System.out.println("JUnit version " + Version.id());
		int i= 0;
		// TODO some kind of plugin mechanism here?
		List<Class> classes= new ArrayList<Class>();
		for (; i < args.length; i++) {
			try {
				classes.add(Class.forName(args[i]));
			} catch (ClassNotFoundException e) {
				System.out.println("Could not find class: " + args[i]);
			}
		}
		RunListener listener= new TextListener();
		addListener(listener);
		run(classes.toArray(new Class[0]));
	}

	public String getVersion() {
		return Version.id();
	}
	
	public Result run(Class... testClasses) {
		List<Runner> strategies= new ArrayList<Runner>();
		for (Class<? extends Object> each : testClasses)
			try {
				strategies.add(getStrategy(each));
			} catch (Exception e) {
				fireTestFailure(new Failure(e));
			}
		run(strategies);
		return fResult;
	}

	public void run(junit.framework.Test test) { 
		ArrayList<Runner> strategies= new ArrayList<Runner>();
		OldTestClassRunner strategy= new OldTestClassRunner();
		strategy.initialize(this, test);
		strategies.add(strategy);
		run(strategies);
	}
	
	public void run(List<Runner> strategies) {
		int count= 0;
		for (Runner each : strategies)
			count+= each.testCount();
		fireTestRunStarted(count);
		long startTime= System.currentTimeMillis();
		for (Runner each : strategies)
			each.run();
		long endTime= System.currentTimeMillis();
		fResult.fRunTime+= endTime - startTime;
		fireTestRunFinished();
	}
	
	private Runner getStrategy(Class<? extends Object> testClass) throws Exception {
		Class klass= null;
		RunWith annotation= testClass.getAnnotation(RunWith.class);
		if (annotation != null) {
			klass= annotation.value();
		} else if (isPre4Test(testClass)) {
			klass= OldTestClassRunner.class; 
		} else {
			klass= TestClassRunner.class;
		}
		Constructor constructor= klass.getConstructor();
		Runner result= (Runner) constructor.newInstance(new Object[] {});
		result.initialize(this, testClass);
		return result;
	}

	private boolean isPre4Test(Class< ? extends Object> testClass) {
		return junit.framework.TestCase.class.isAssignableFrom(testClass);
	}
	
	public void addListener(RunListener listener) {
		fListeners.add(listener);
	}

	public void removeListener(RunListener listener) {
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
	
	private void fireTestRunStarted(final int testCount) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testRunStarted(testCount);
			};
		}.run();
	}
	
	private void fireTestRunFinished() {
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testRunFinished(fResult);
			};
		}.run();
	}
	
	public void fireTestStarted(final Object test, final String name) {
		fResult.fCount++;
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testStarted(test, name);
			};
		}.run();
	}

	public void fireTestFailure(final Failure failure) {
		fResult.fFailures.add(failure);
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testFailure(failure);
			};
		}.run();
	}

	public void fireTestIgnored(final Method method) {
		fResult.fIgnoreCount++;
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
