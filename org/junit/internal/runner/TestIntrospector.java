package org.junit.internal.runner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Expected;
import org.junit.Test;

public class TestIntrospector {
	
	private final Class< ? extends Object> fTestClass;

	public TestIntrospector(Class<? extends Object> testClass) {
		fTestClass= testClass;
	}

	public boolean isUnexpected(Throwable exception, Method method) {
		Expected annotation= method.getAnnotation(Expected.class);
		if (annotation == null)
			return true;
		return !annotation.value().equals(exception.getClass());
	}

	public List<Method> getTestMethods(Class<? extends Annotation> annotationClass) {
		return getTestMethods(annotationClass, false);
	}

	public List<Method> getStaticTestMethods(Class<? extends Annotation> annotationClass) {
		return getTestMethods(annotationClass, true);
	}

	public List<Exception> validateTestMethods() {
		List<Exception> results= new ArrayList<Exception>();
		try {
			fTestClass.getConstructor();
		} catch (Exception e) {
			results.add(new Exception("Test class should have public zero-argument constructor", e));
		}
		try {
			validateTestMethods(BeforeClass.class, true, results);
			validateTestMethods(AfterClass.class, true, results);
			validateTestMethods(After.class, false, results);
			validateTestMethods(Before.class, false, results);
			validateTestMethods(Test.class, false, results);
		} catch (Exception e) {
			results.add(e);
		}
		return results;
	}

	private List<Method> getTestMethods(Class<? extends Annotation> annotationClass, boolean isStatic) {
		List<Method> results= new ArrayList<Method>();
		Method[] methods= fTestClass.getDeclaredMethods();
		for (Method each : methods) {
			Annotation annotation= each.getAnnotation(annotationClass);
			if (annotation != null) 
				results.add(each);
		}
		return results;
	}
	
	private void validateTestMethods(Class<? extends Annotation> annotation, boolean isStatic, List<Exception> results) throws Exception {
		List<Method> methods= getTestMethods(annotation, isStatic);
		for (Method each : methods) {
			if (Modifier.isStatic(each.getModifiers()) != isStatic) {
				String state= isStatic ? "should" : "should not";
				results.add(new Exception("Method " + each.getName() + "() " + state + " be static"));
			}
			if (!Modifier.isPublic(each.getModifiers()))
				results.add(new Exception("Method " + each.getName() + " should be public"));
			if (each.getReturnType() != Void.TYPE)
				results.add(new Exception("Method " + each.getName() + " should be void"));
			if (each.getParameterTypes().length != 0)
				results.add(new Exception("Method " + each.getName() + " should have no parameters"));
		}
	}

}

