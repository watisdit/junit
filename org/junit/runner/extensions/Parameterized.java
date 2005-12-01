package org.junit.runner.extensions;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.Failure;
import org.junit.runner.RunNotifier;
import org.junit.runner.internal.TestClassRunner;
import org.junit.runner.internal.TestIntrospector;
import org.junit.runner.internal.TestMethodRunner;

public class Parameterized extends TestClassRunner {
	
	public Parameterized(RunNotifier notifier, Class< ? extends Object> klass) {
		super(notifier, klass);
	}

	@Override
	public int testCount() {
		try {
			Object object= getParametersList();
			return Array.getLength(object);
		} catch (Exception e) {
			getNotifier().fireTestFailure(new Failure(e));
			return 0;
		}
		// TODO: what if it's not an array?
	}
	
	@Override
	protected List<Exception> validateClass() {
		// TODO: do more?
		return Collections.emptyList();
	}
	
	@Override
	protected void runTestMethods() {
		try {
			Object parameters= getParametersList();
			for (int i= 0; i < Array.getLength(parameters); i++)
				runParameter(Array.get(parameters, i));
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: what to do? Create a Failure
		}
	}

	private void runParameter(Object parameters) throws Exception {
		TestIntrospector testIntrospector= new TestIntrospector(getTestClass());
		for (Method eachMethod : testIntrospector.getTestMethods(Test.class)) {			
			Object test= createTest(parameters);
			new TestMethodRunner(test, eachMethod, getNotifier()).run();
		}
	}

	private Object createTest(Object parameters) throws Exception {
		Constructor[] constructors= getTestClass().getConstructors();
		assertEquals(1, constructors.length);
		Constructor constructor= constructors[0];
		Object[] boxed= new Object[Array.getLength(parameters)];
		for (int i= 0; i < Array.getLength(parameters); i++)
			boxed[i]= Array.get(parameters, i);
		return constructor.newInstance(boxed);
	}

	private Object getParametersList() throws Exception {
		return getParametersMethod().invoke(null, new Object[0]);
	}

	private Method getParametersMethod() throws Exception {
		for (Method each : getTestClass().getMethods()) {
			if (Modifier.isStatic(each.getModifiers())) {
				Annotation[] annotations= each.getAnnotations();
				for (Annotation annotation : annotations) {
					if (annotation.annotationType() == Parameters.class)
						return each;
				}
			}
		}
		throw new Exception("No parameters method");
	}

}
