package org.junit.internal.runner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


public class TestIntrospector {
	
	private final Class< ? extends Object> fTestClass;
	
	public TestIntrospector(Class<? extends Object> testClass) {
		fTestClass= testClass;
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

	public List<Method> getTestMethods(Class<? extends Annotation> annotationClass) {
		List<Method> results= new ArrayList<Method>();
		for (Class eachClass : getSuperClasses(fTestClass)) {
			Method[] methods= eachClass.getDeclaredMethods();
			for (Method eachMethod : methods) {
				Annotation annotation= eachMethod.getAnnotation(annotationClass);
				if (annotation != null && ! isShadowed(eachMethod, results)) 
					results.add(eachMethod);
			}
		}
		if (runsTopToBottom(annotationClass))
			Collections.reverse(results);
		return results;
	}

	public boolean isIgnored(Method eachMethod) {
		return eachMethod.getAnnotation(Ignore.class) != null;
	}

	private boolean runsTopToBottom(Class< ? extends Annotation> annotation) {
		return annotation.equals(Before.class) || annotation.equals(BeforeClass.class);
	}
	
	private boolean isShadowed(Method method, List<Method> results) {
		for (Method each : results) {
			if (each.getName().equals(method.getName()))
				return true;
		}
		return false;
	}

	private List<Class> getSuperClasses(Class< ? extends Object> testClass) {
		ArrayList<Class> results= new ArrayList<Class>();
		Class<? extends Object> current= testClass;
		while (current != null) {
			results.add(current);
			current= current.getSuperclass();
		}
		return results;
	}

	private void validateTestMethods(Class<? extends Annotation> annotation, boolean isStatic, List<Exception> results) throws Exception {
		List<Method> methods= getTestMethods(annotation);
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

