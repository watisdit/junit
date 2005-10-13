package org.junit.runner;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Parameters;
import org.junit.Test;
import org.junit.internal.runner.TestIntrospector;
import org.junit.internal.runner.TestMethodRunner;
import org.junit.internal.runner.TestNotifier;

public class Parameterized implements RunnerStrategy {

	private Class< ? extends Object> fTestClass;
	private TestNotifier fNotifier;

	public void initialize(TestNotifier notifier, Class< ? extends Object> testClass) {
		this.fNotifier= notifier;
		this.fTestClass= testClass;
	}

	public int testCount() {
		try {
			Object object= getParametersList();
			return Array.getLength(object);
		} catch (Exception e) {
			// TODO: what's right? add a Failure and return 0?
			return 0;
		}
		// TODO: what if it's not an array?
	}
	
	public void run() {
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
		TestIntrospector testIntrospector= new TestIntrospector(fTestClass);
		for (Method eachMethod : testIntrospector.getTestMethods(Test.class)) {			
			Object test= createTest(parameters);
			new TestMethodRunner(test, eachMethod, fNotifier).run();
		}
	}

	private Object createTest(Object parameters) throws Exception {
		// TODO I'd prefer to look at the runtime types of the parameters and find a matching constructor, but I can't figure out how to fetch the actual types
		// Instead, this will only work with test classes that have a single constructor
		Constructor[] constructors= fTestClass.getConstructors();
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
		for (Method each : fTestClass.getMethods()) {
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
