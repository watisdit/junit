package org.junit.tries;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import junit.framework.AssertionFailedError;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Expected;
import org.junit.Test;

public class JUnit4TestRunner {
	private Runner fRunner;
	private final Class fTestClass;

	JUnit4TestRunner(Runner runner, Class testClass) {
		fRunner= runner; // I would like to get rid of this so this object can be used as an engine elsewhere
		fTestClass= testClass;
	}

	void run() throws Exception {
		List<Method> beforeMethods= getAnnotatedMethods(fTestClass, BeforeClass.class);
		for (Method method : beforeMethods) {
			if (validateOneTimeMethod(method)) {
				addFailure(new Exception("@beforeClass methods have to be public static"));
				return;
			}
			method.invoke(null, new Object[0]);
		}
		List<Method> methods= getAnnotatedMethods(fTestClass, Test.class);
		for (Method method : methods) {
			Constructor constructor= fTestClass.getConstructor(new Class[0]);
			Object test= constructor.newInstance(new Object[0]);
			invokeMethod(test, method);
		}
		List<Method> afterMethods= getAnnotatedMethods(fTestClass, AfterClass.class);
		for (Method method : afterMethods) {
			method.invoke(null, new Object[0]);
		}
	}

	void addFailure(Throwable exception) {
		fRunner.addFailure(exception); // TODO Add a TestFailure that includes the test that failed
	}

	// TODO: Have this throw an exception if the method is invalid
	public static boolean validateOneTimeMethod(Method method) {
		return !Modifier.isStatic(method.getModifiers());
	}

	// TODO: public void run(TestSuite suite) ...

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
		boolean failed= false;
		try {
			method.invoke(test, new Object[0]);
			if (expectedException(method)) {
				addFailure(new AssertionFailedError()); // TODO: Error string specifying exception that should have been thrown
				failed= true;
			}
		} catch (InvocationTargetException e) {
			if (isUnexpected(e.getTargetException(), method)) {
				addFailure(e.getTargetException());
				failed= true;
			}
		} catch (Throwable e) {
			// TODO: Make sure this can't happen
			addFailure(e);
		} finally {
			try {
				tearDown(test);
			} catch (InvocationTargetException e) {
				if (!failed)
					addFailure(e.getTargetException());
			} catch (Throwable e) {
				addFailure(e); // TODO: Write test for this
			}
		}
	}

	private boolean expectedException(Method method) {
		return method.getAnnotation(Expected.class) != null;
	}

	public static boolean isUnexpected(Throwable exception, Method method) {
		Expected annotation= method.getAnnotation(Expected.class);
		if (annotation == null)
			return true;
		return !annotation.value().equals(exception.getClass());
	}

	static public void tearDown(Object test) throws Exception {
		List<Method> afters= getAnnotatedMethods(test.getClass(), After.class);
		for (Method after : afters)
			after.invoke(test, new Object[0]);
	}

	static public void setUp(Object test) throws Exception {
		List<Method> befores= getAnnotatedMethods(test.getClass(), Before.class);
		for (Method before : befores)
			before.invoke(test, new Object[0]);
	}

	static public List<Method> getAnnotatedMethods(Class klass, Class annotationClass) {
		List<Method> results= new ArrayList<Method>();
		Method[] methods= klass.getMethods();
		for (Method each : methods) {
			Annotation annotation= each.getAnnotation(annotationClass);
			if (annotation != null)
				results.add(each);
		}
		return results;
	}

}
