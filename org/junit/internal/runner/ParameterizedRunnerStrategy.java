package org.junit.internal.runner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Parameter;
import org.junit.Parameters;
import org.junit.Test;

public class ParameterizedRunnerStrategy implements RunnerStrategy {

	private final Class< ? extends Object> fTestClass;
	private final TestNotifier fNotifier;

	public ParameterizedRunnerStrategy(TestNotifier notifier, Class< ? extends Object> testClass) {
		this.fNotifier= notifier;
		this.fTestClass= testClass;
	}

	public void run() {
		try {
			Object parameters= getParametersList();
			for (int i= 0; i < Array.getLength(parameters); i++)
				runParameter(Array.get(parameters, i));
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: what to do?
		}
	}

	private void runParameter(Object parameters) throws Exception {
		for (Method eachMethod : new TestIntrospector(fTestClass).getTestMethods(Test.class)) {			
			Object test= fTestClass.newInstance();
			setParameters(test, parameters);
			new JUnit4RunnerStrategy(fNotifier, fTestClass).invokeTestMethod(test, eachMethod);
		}
		// test.toString();
	}

	private void setParameters(Object test, Object parameters) throws Exception {
		// TODO: require non-static fields
		for (Field each : fTestClass.getDeclaredFields()) {
			Annotation[] annotations= each.getAnnotations();
			for (Annotation annotation : annotations)
				if (annotation.annotationType() == Parameter.class)
					setParameter(test, (Parameter) annotation, each, parameters);
		}
	}
	
	private void setParameter(Object test, Parameter annotation, Field field, Object parameters) throws Exception {
		int index= annotation.value();
		if (field.getType() == int.class)
			field.setInt(test, Array.getInt(parameters, index));
		else
			// TODO
			throw new Exception("Haven't handled this case");
		// field.set(test, Array.get(parameters, index));
	}

	public int testCount() {
		try {
			Object object= getParametersList();
			return Array.getLength(object);
		} catch (Exception e) {
			// TODO: what's right?
			return 0;
		}
		// TODO: what if it's not an array?
	}

	private Object getParametersList() throws Exception {
		Field data= getParametersField();
		Object object= data.get(null);
		return object;
	}

	private Field getParametersField() throws Exception {
		for (Field each : fTestClass.getDeclaredFields()) {
			if (Modifier.isStatic(each.getModifiers())) {
				Annotation[] annotations= each.getAnnotations();
				for (Annotation annotation : annotations) {
					if (annotation.annotationType() == Parameters.class)
						return each;
				}
			}
		}
		throw new Exception("No data field");
	}

}
