package org.junit.tries;

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
		fRunner= runner; // I would like to get rid of this so this object can be used as an engine elsewhere
		fTestClass= testClass;
	}

	public void run() {
//TODO:		List<Throwable> errors= validateTestMethods();
//		if (! errors.isEmpty()) {
//			for (Throwable each : errors) 
//				addFailure(each);
//			return;
//		}
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
			addFailure(e);
		}
	}

	void addFailure(Throwable exception) {
		fRunner.addFailure(exception); // TODO Add a TestFailure that includes the test that failed
	}

	public static void validateTestMethod(Method method, boolean isStatic) throws Exception {
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

	private void invokeMethod(Object test, Method method) {
		fRunner.startTestCase(test, method.getName());
		try {
			setUp(test);
		} catch (InvocationTargetException e) {
			addFailure(e.getTargetException());
			return;
		} catch (Throwable e) {
			addFailure(e); // TODO: Write test for this
		}
		try {
			method.invoke(test, new Object[0]);
			Expected expected= expectedException(method);
			if (expected != null) {
				addFailure(new AssertionError("Expected exception: "  + expected.value().getName()));
			}
		} catch (InvocationTargetException e) {
			if (isUnexpected(e.getTargetException(), method))
				addFailure(e.getTargetException());
		} catch (Throwable e) {
			// TODO: Make sure this can't happen
			addFailure(e);
		} finally {
			try {
				tearDown(test);
			} catch (InvocationTargetException e) {
				addFailure(e.getTargetException());
			} catch (Throwable e) {
				addFailure(e); // TODO: Write test for this
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
		Method[] methods= klass.getMethods();
		for (Method each : methods) {
			Annotation annotation= each.getAnnotation(annotationClass);
			if (annotation != null) {
				validateTestMethod(each, isStatic);
				results.add(each);
			}
		}
		return results;
	}

}
