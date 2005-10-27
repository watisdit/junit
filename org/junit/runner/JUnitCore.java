package org.junit.runner;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import junit.runner.Version;
import org.junit.runner.internal.OldTestClassRunner;
import org.junit.runner.internal.TestClassRunner;
import org.junit.runner.internal.TextListener;


public class JUnitCore {
	
	private Result fResult;
	private RunNotifier fNotifier;

	public JUnitCore() {
		fResult= new Result();
		fNotifier= new RunNotifier();
		fResult.addListenerTo(fNotifier);
	}

	public static void main(String... args) {
		new JUnitCore().runMain(args);
	}
	
	public static Result runClasses(Class... classes) {
		return new JUnitCore().run(classes);
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
		List<Runner> runners= new ArrayList<Runner>();
		for (Class<? extends Object> each : testClasses)
			try {
				runners.add(getRunner(each));
			} catch (Exception e) {
				fNotifier.fireTestFailure(new Failure(e));
			}
		run(runners);
		return fResult;
	}

	public void run(junit.framework.Test test) { 
		ArrayList<Runner> runners= new ArrayList<Runner>();
		OldTestClassRunner runner= new OldTestClassRunner(fNotifier, test);
		runners.add(runner);
		run(runners);
	}
	
	public void run(List<Runner> runners) {
		int count= 0;
		for (Runner each : runners)
			count+= each.testCount();
		fNotifier.fireTestRunStarted(count);
		for (Runner each : runners)
			each.run();
		fNotifier.fireTestRunFinished(fResult);
	}
	
	private Runner getRunner(Class<? extends Object> testClass) throws Exception {
		Class klass= getRunnerClass(testClass);
		Constructor constructor= klass.getConstructor(RunNotifier.class, Class.class); // TODO good error message if no such constructor
		Runner result= (Runner) constructor.newInstance(new Object[] {fNotifier, testClass});
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
