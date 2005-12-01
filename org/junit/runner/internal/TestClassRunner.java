package org.junit.runner.internal;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.ClassRunner;
import org.junit.runner.Failure;
import org.junit.runner.RunNotifier;

public class TestClassRunner extends ClassRunner {
	private TestIntrospector fTestIntrospector;

	public TestClassRunner(RunNotifier notifier, Class<? extends Object> klass) {
		super(notifier, klass);
		fTestIntrospector= new TestIntrospector(getTestClass());
	}

	@Override
	public int testCount() {
		return fTestIntrospector.getTestMethods(Test.class).size();
	}

	private class FailedBefore extends Exception {
		private static final long serialVersionUID= 1L;
	}
	
	@Override
	public void run() {
		List<Exception> errors= validateClass();
		if (!errors.isEmpty()) {
			for (Throwable each : errors)
				addFailure(each);
			return;
		}

		try {
			runBeforeClasses();
			runTestMethods();
		} catch (FailedBefore e) {
		} finally {
			runAfterClasses();
		}
	}

	protected List<Exception> validateClass() {
		return fTestIntrospector.validateTestMethods();
	}

	private void addFailure(Throwable exception) {
		getNotifier().fireTestFailure(new Failure(exception));
	}

	public void runBeforeClasses() throws FailedBefore {
		try {
			List<Method> beforeMethods= fTestIntrospector.getTestMethods(BeforeClass.class);
			for (Method method : beforeMethods)
				method.invoke(null);
		} catch (Exception e) {
			addFailure(e);
			throw new FailedBefore();
		}
	}

	protected void runTestMethods() {
		List<Method> methods= fTestIntrospector.getTestMethods(Test.class);
		for (Method method : methods)
			try {
				invokeTestMethod(method);
			} catch (Exception e) {
				addFailure(e);
			}
	}
	
	private void runAfterClasses() {
		List<Method> afterMethods= fTestIntrospector.getTestMethods(AfterClass.class);
		for (Method method : afterMethods)
			try {
				method.invoke(null);
			} catch (Exception e) {
				addFailure(e);
			}
	}
	
	protected void invokeTestMethod(Method method) throws Exception {
		Object test= getTestClass().getConstructor().newInstance();
		new TestMethodRunner(test, method, getNotifier()).run();
	}

}
