package org.junit.internal.runner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.Failure;

public class JUnit4Strategy implements RunnerStrategy {
	private final Class< ? extends Object> fTestClass;
	private final TestIntrospector fTestIntrospector;
	private TestNotifier fNotifier;

	public JUnit4Strategy(TestNotifier notifier, Class< ? extends Object> testClass) {
		fNotifier= notifier;
		fTestClass= testClass;
		fTestIntrospector= new TestIntrospector(fTestClass);
	}

	public void run() {
		List<Exception> errors= fTestIntrospector.validateTestMethods();
		if (!errors.isEmpty()) {
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
				if (fTestIntrospector.isIgnored(method)) {
					fNotifier.fireTestIgnored(method);
				} else {
					Constructor< ? extends Object> constructor= fTestClass.getConstructor();
					Object test= constructor.newInstance();
					invokeMethod(test, method);
				}
			}
			List<Method> afterMethods= fTestIntrospector.getTestMethods(AfterClass.class);
			for (Method method : afterMethods)
				method.invoke(null);
		} catch (Exception e) {
			addFailure(new Failure(e));
		}
	}

	void addFailure(Failure failure) {
		fNotifier.fireTestFailure(failure);
	}

	private void invokeMethod(Object test, Method method) {
		fNotifier.fireTestStarted(test, method.getName());
		try {
			setUp(test);
		} catch (InvocationTargetException e) {
			addFailure(new TestFailure(test, method.getName(), e.getTargetException()));
			return;
		} catch (Throwable e) {
			addFailure(new TestFailure(test, method.getName(), e)); // TODO:
			return;
		}
		try {
			method.invoke(test);
			Class< ? extends Throwable> expected= fTestIntrospector.expectedException(method);
			if (fTestIntrospector.expectsException(method)) {
				addFailure(new TestFailure(test, method.getName(), new AssertionError("Expected exception: " + expected.getName())));
			}
		} catch (InvocationTargetException e) {
			if (fTestIntrospector.isUnexpected(e.getTargetException(), method))
				addFailure(new TestFailure(test, method.getName(), e.getTargetException()));
		} catch (Throwable e) {
			addFailure(new TestFailure(test, method.getName(), e)); // TODO:
		} finally {
			try {
				tearDown(test);
			} catch (InvocationTargetException e) {
				addFailure(new TestFailure(test, method.getName(), e.getTargetException()));
			} catch (Throwable e) {
				addFailure(new TestFailure(test, method.getName(), e)); // TODO:
			}
		}
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
