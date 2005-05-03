package org.junit.runner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

public class JUnit4TestRunner implements RunnerStrategy {
	private Runner fRunner;
	private final Class fTestClass;

	JUnit4TestRunner(Runner runner, Class testClass) {
		fRunner= runner; //TODO: I would like to get rid of fRunner so this object can be used as an engine elsewhere and we can get rid of those stupid static methods and their extra testClass parameter
		fTestClass= testClass;
	}

	public void run() {
		List<Exception> errors= validateTestMethods(fTestClass);
		if (! errors.isEmpty()) {
			for (Throwable each : errors) 
				addFailure(new Failure(each));
			return;
		}
		try {
			List<Method> beforeMethods= getStaticTestMethods(fTestClass, BeforeClass.class);
			for (Method method : beforeMethods)
				method.invoke(null, new Object[0]);
			List<Method> methods= getTestMethods(fTestClass, Test.class);
			for (Method method : methods) {
				Constructor constructor= fTestClass.getConstructor(new Class[0]);
				Object test= constructor.newInstance(new Object[0]);
				invokeMethod(test, method);
			}
			List<Method> afterMethods= getStaticTestMethods(fTestClass, AfterClass.class);
			for (Method method : afterMethods)
				method.invoke(null, new Object[0]);
		} catch (Exception e) {
			addFailure(new Failure(e));
		}
	}

	void addFailure(Failure failure) {
		fRunner.addFailure(failure);
	}

	private void invokeMethod(Object test, Method method) {
		fRunner.startTestCase(test, method.getName());
		try {
			setUp(test);
		} catch (InvocationTargetException e) {
			addFailure(new Failure(test, method.getName(), e.getTargetException()));
			return;
		} catch (Throwable e) {
			addFailure(new Failure(test, method.getName(), e)); // TODO: Write test for this
		}
		try {
			method.invoke(test, new Object[0]);
			Expected expected= expectedException(method);
			if (expected != null) {
				addFailure(new Failure(test, method.getName(), new AssertionError("Expected exception: "  + expected.value().getName())));
			}
		} catch (InvocationTargetException e) {
			if (isUnexpected(e.getTargetException(), method))
				addFailure(new Failure(test, method.getName(), e.getTargetException()));
		} catch (Throwable e) {
			// TODO: Make sure this can't happen
			addFailure(new Failure(test, method.getName(), e));
		} finally {
			try {
				tearDown(test);
			} catch (InvocationTargetException e) {
				addFailure(new Failure(test, method.getName(), e.getTargetException()));
			} catch (Throwable e) {
				addFailure(new Failure(test, method.getName(), e)); // TODO: Write test for this
			}
		}
	}

	private Expected expectedException(Method method) {
		return method.getAnnotation(Expected.class);
	}

	public static boolean isUnexpected(Throwable exception, Method method) {
		Expected annotation= method.getAnnotation(Expected.class);
		if (annotation == null)
			return true;
		return !annotation.value().equals(exception.getClass());
	}

	static public void tearDown(Object test) throws Exception {
		List<Method> afters= getTestMethods(test.getClass(), After.class);
		for (Method after : afters)
			after.invoke(test, new Object[0]);
	}

	static public void setUp(Object test) throws Exception {
		List<Method> befores= getTestMethods(test.getClass(), Before.class);
		for (Method before : befores)
			before.invoke(test, new Object[0]);
	}

	static public List<Method> getTestMethods(Class klass, Class annotationClass) throws Exception {
		return getTestMethods(klass, annotationClass, false);
	}

	static public List<Method> getStaticTestMethods(Class klass, Class annotationClass) throws Exception {
		return getTestMethods(klass, annotationClass, true);
	}
	
	private static List<Method> getTestMethods(Class klass, Class annotationClass, boolean isStatic) throws Exception {
		List<Method> results= new ArrayList<Method>();
		Method[] methods= klass.getDeclaredMethods();
		for (Method each : methods) {
			Annotation annotation= each.getAnnotation(annotationClass);
			if (annotation != null) {
				//validateTestMethod(each, isStatic);
				results.add(each);
			}
		}
		return results;
	}

	public static List<Exception> validateTestMethods(Class testClass) {
		List<Exception> results= new ArrayList<Exception>();
		try {
			testClass.getConstructor(new Class[0]);
		} catch (Exception e) {
			results.add(new Exception("Test class should have public zero-argument constructor", e));
		}
		try {
			validateTestMethods(testClass, BeforeClass.class, true, results);
			validateTestMethods(testClass, AfterClass.class, true, results);
			validateTestMethods(testClass, After.class, false, results);
			validateTestMethods(testClass, Before.class, false, results);
			validateTestMethods(testClass, Test.class, false, results);
		} catch (Exception e) {
			results.add(e);
		}
		return results;
	}

	private static void validateTestMethods(Class testClass, Class annotation, boolean isStatic, List<Exception> results) throws Exception {
		List<Method> methods= getTestMethods(testClass, annotation, isStatic);
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

	private static void validateTestMethod(Method method, boolean isStatic) throws Exception {
		if (Modifier.isStatic(method.getModifiers()) != isStatic) {
			String state= isStatic ? "should" : "should not";
			throw new Exception("Method " + method.getName() + "() " + state + " be static");
		}
		if (!Modifier.isPublic(method.getModifiers()))
			throw new Exception("Method " + method.getName() + " should be public");

		if (method.getReturnType() != Void.TYPE)
			throw new Exception("Method " + method.getName() + " should be void");
		if (method.getParameterTypes().length != 0)
			throw new Exception("Method " + method.getName() + " should have no parameters");
	}
	
}
