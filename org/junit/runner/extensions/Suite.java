package org.junit.runner.extensions;


import org.junit.runner.ClassRunner;
import org.junit.runner.InitializationErrorListener;
import org.junit.runner.RunNotifier;
import org.junit.runner.Runner;
import org.junit.runner.plan.Plan;
import org.junit.runner.request.Request;

public class Suite extends ClassRunner implements Filterable {
	private Runner fComposite;

	public Suite(Class< ? extends Object> klass) {
		super(klass);
	}

	@Override
	public boolean canRun(InitializationErrorListener notifier) {
		fComposite = Request.classes("All", getTestClasses()).getRunner(notifier);
		return fComposite.testCount() > 0;
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
		filter.apply(fComposite);
	}
}
