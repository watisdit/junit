package org.junit.runner;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import junit.runner.Version;
import org.junit.runner.internal.OldTestClassRunner;
import org.junit.runner.internal.TestClassRunner;
import org.junit.runner.internal.RunNotifier;
import org.junit.runner.internal.TextListener;


public class JUnitCore {
	
	private Result fResult;
	private RunNotifier fNotifier;

	public JUnitCore() {
		fResult= new Result();
		fNotifier= new RunNotifier();
		addListener(fResult);
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
				fNotifier.fireTestFailure(new Failure(e));
			}
		run(strategies);
		return fResult;
	}

	public void run(junit.framework.Test test) { 
		ArrayList<Runner> strategies= new ArrayList<Runner>();
		OldTestClassRunner strategy= new OldTestClassRunner();
		strategy.initialize(fNotifier, test);
		strategies.add(strategy);
		run(strategies);
	}
	
	public void run(List<Runner> strategies) {
		int count= 0;
		for (Runner each : strategies)
			count+= each.testCount();
		fNotifier.fireTestRunStarted(count);
		for (Runner each : strategies)
			each.run();
		fNotifier.fireTestRunFinished(fResult);
	}
	
	private Runner getStrategy(Class<? extends Object> testClass) throws Exception {
		Class klass= getRunnerClass(testClass);
		Constructor constructor= klass.getConstructor();
		Runner result= (Runner) constructor.newInstance(new Object[] {});
		result.initialize(fNotifier, testClass);
		return result;
	}

	private Class getRunnerClass(Class< ? extends Object> testClass) {
		Class klass;
		RunWith annotation= testClass.getAnnotation(RunWith.class);
		if (annotation != null) {
			klass= annotation.value();
		} else if (isPre4Test(testClass)) {
			klass= OldTestClassRunner.class; 
		} else {
			klass= TestClassRunner.class;
		}
		return klass;
	}

	private boolean isPre4Test(Class< ? extends Object> testClass) {
		return junit.framework.TestCase.class.isAssignableFrom(testClass);
	}
	
	public void addListener(RunListener listener) {
		fNotifier.addListener(listener);
	}

	public void removeListener(RunListener listener) {
		fNotifier.removeListener(listener);
	}
}
