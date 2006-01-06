package org.junit.runner.extensions;


import org.junit.runner.ClassRunner;
import org.junit.runner.InitializationErrorListener;
import org.junit.runner.RunNotifier;
import org.junit.runner.internal.RunnerFactory;
import org.junit.runner.plan.Plan;

public class Suite extends ClassRunner implements Filterable {
	private CompositeRunner fComposite;

	public Suite(Class< ? extends Object> klass) {
		super(klass);
		fComposite = new CompositeRunner(klass.getName());
	}

	@Override
	public boolean canRun(InitializationErrorListener notifier) {
		fComposite.addAll(new RunnerFactory().getRunners(notifier, getTestClasses()));
		return !fComposite.getRunners().isEmpty();
	}
	
	protected Class[] getTestClasses() {
		SuiteClasses annotation= getTestClass().getAnnotation(SuiteClasses.class);
		// TODO: if null?
		return annotation.value();
	}

	@Override
	public Plan getPlan() {
		return fComposite.getPlan();
	}

	@Override
	public void run(RunNotifier notifier) {
		fComposite.run(notifier);
	}

	public void filter(Filter filter) {
		fComposite.filter(filter);
	}
}
