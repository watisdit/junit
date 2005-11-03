package org.junit.runner.internal;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Failure;
import org.junit.runner.RunNotifier;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;

public class RunnerFactory {
	public List<Runner> getRunners(RunNotifier notifier, Class... testClasses) {
		List<Runner> runners= new ArrayList<Runner>();
		for (Class<? extends Object> each : testClasses)
			try {
				runners.add(getRunner(each, notifier));
			} catch (Exception e) {
				notifier.fireTestFailure(new Failure(e));
			}
		return runners;
	}

	private Runner getRunner(Class< ? extends Object> testClass, RunNotifier notifier) throws Exception {
		Class klass= getRunnerClass(testClass);
		Constructor constructor= klass.getConstructor(RunNotifier.class, Class.class); // TODO good error message if no such constructor
		return (Runner) constructor.newInstance(new Object[] {notifier, testClass});
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
}
