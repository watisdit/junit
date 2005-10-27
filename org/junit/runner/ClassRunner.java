package org.junit.runner;


public abstract class ClassRunner extends Runner {

	private final Class<? extends Object> fTestClass;

	public ClassRunner(RunNotifier notifier, Class< ? extends Object> klass) {
		super(notifier);
		fTestClass= klass;
	}

	protected Class<? extends Object> getTestClass() {
		return fTestClass;
	}

}
