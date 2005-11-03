package org.junit.runner.extensions;

import java.util.List;

import org.junit.runner.ClassRunner;
import org.junit.runner.RunNotifier;
import org.junit.runner.Runner;
import org.junit.runner.internal.RunnerFactory;

public class Suite extends ClassRunner {

	private List<Runner> fRunners;

	public Suite(RunNotifier notifier, Class< ? extends Object> klass) {
		super(notifier, klass);
		fRunners= new RunnerFactory().getRunners(notifier, getTestClasses());
	}

	@Override
	public void run() {
		for (Runner each : fRunners)
			each.run();
	}

	@Override
	public int testCount() {
		int count= 0;
		for (Runner each : fRunners)
			count+= each.testCount();
		return count;
	}

	private Class[] getTestClasses() {
		SuiteClasses annotation= getTestClass().getAnnotation(SuiteClasses.class);
		// TODO: if null?
		return annotation.value();
	}
}
