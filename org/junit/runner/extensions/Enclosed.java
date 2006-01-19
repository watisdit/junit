package org.junit.runner.extensions;

import org.junit.runner.internal.InitializationError;

public class Enclosed extends Suite {
	public Enclosed(Class<? extends Object> klass) throws InitializationError {
		super(klass, klass.getClasses());
	}
}
