package org.junit.runner.extensions;


public class Enclosed extends Suite {
	public Enclosed(Class<? extends Object> klass) {
		super(klass);
	}
	
	@Override
	protected Class[] getTestClasses() {
		return getTestClass().getClasses();
	}
}
