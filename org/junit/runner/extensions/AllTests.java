package org.junit.runner.extensions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import junit.framework.Test;
import org.junit.runner.internal.OldTestClassRunner;

public class AllTests extends OldTestClassRunner {
	private static Test suite(Class< ? extends Object> klass) throws Throwable {
		Method suiteMethod= null;
		Test suite= null;
		try {
			suiteMethod= klass.getMethod("suite");
			if (! Modifier.isStatic(suiteMethod.getModifiers())) {
				throw new Exception(klass.getName() + ".suite() must be static");
			}
			suite= (Test) suiteMethod.invoke(null); // static method
		} catch (InvocationTargetException e) { // TODO need coverage
			throw e.getCause();
		}
		return suite;
	}
	
	@SuppressWarnings("unchecked")
	public AllTests(Class< ? extends Object> klass) throws Throwable {
		super(suite(klass));
	}
}
