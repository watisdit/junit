package org.junit.internal.runner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Expected;
import org.junit.Test;
import org.junit.runner.Failure;
import org.junit.runner.Runner;
import org.junit.runner.TestFailure;

public class JUnit4Strategy implements RunnerStrategy {
	private Runner fRunner; //TODO: Find a way to decouple the strategy from a concrete Runner class
	private final Class<? extends Object> fTestClass;
	private final TestIntrospector fTestIntrospector;

	public JUnit4Strategy(Runner runner, Class<? extends Object> testClass) {
		fRunner= runner;
		fTestClass= testClass;
		fTestIntrospector= new TestIntrospector(fTestClass);
	}

	public void run() {
		List<Exception> errors= fTestIntrospector.validateTestMethods();
		if (! errors.isEmpty()) {
			for (Throwable each : errors) 
				addFailure(new Failure(each));
			return;
		}
		try {
			List<Method> beforeMethods= fTestIntrospector.getTestMethods(BeforeClass.class);
			for (Method method : beforeMethods)
				method.invoke(null);
			List<Method> methods= fTestIntrospector.getTestMethods(Test.class);
			for (Method method : methods) {
				Constructor<? extends Object> constructor= fTestClass.getConstructor();
				Object test= constructor.newInstance();
				invokeMethod(test, method);
			} 
			List<Method> afterMethods= fTestIntrospector.getTestMethods(AfterClass.class);
			for (Method method : afterMethods)
				method.invoke(null);
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
			addFailure(new TestFailure(test, method.getName(), e.getTargetException()));
			return;
		} catch (Throwable e) {
			addFailure(new TestFailure(test, method.getName(), e)); // TODO: Write test for this
			return;
		}
		try {
			method.invoke(test);
			Expected expected= expectedException(method);
			if (expected != null) {
				addFailure(new TestFailure(test, method.getName(), new AssertionError("Expected exception: "  + expected.value().getName())));
			}
		} catch (InvocationTargetException e) {
			if (fTestIntrospector.isUnexpected(e.getTargetException(), method))
				addFailure(new TestFailure(test, method.getName(), e.getTargetException()));
		} catch (Throwable e) {
			addFailure(new TestFailure(test, method.getName(), e)); // TODO: Write test for this
		} finally {
			try {
				tearDown(test);
			} catch (InvocationTargetException e) {
				addFailure(new TestFailure(test, method.getName(), e.getTargetException()));
			} catch (Throwable e) {
				addFailure(new TestFailure(test, method.getName(), e)); // TODO: Write test for this
			}
		}
	}

	private Expected expectedException(Method method) {
		return method.getAnnotation(Expected.class);
	}

	public void tearDown(Object test) throws Exception {
		List<Method> afters= fTestIntrospector.getTestMethods(After.class);
		for (Method after : afters)
			after.invoke(test);
	}

	public void setUp(Object test) throws Exception {
		List<Method> befores= fTestIntrospector.getTestMethods(Before.class);
		for (Method before : befores)
			before.invoke(test);
	}
	
}
