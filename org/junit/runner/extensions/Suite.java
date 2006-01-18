package org.junit.runner.extensions;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.notify.RunNotifier;
import org.junit.plan.Plan;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runner.internal.ClassRunner;

public class Suite extends ClassRunner implements Filterable {
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface SuiteClasses {
		public Class[] value();
	}
	
	private Runner fComposite;

	public Suite(Class< ? extends Object> klass) {
		super(klass);
		fComposite = Request.classes("All", getTestClasses()).getRunner();
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
