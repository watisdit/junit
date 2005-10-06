package org.junit.internal.runner;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.Failure;
import org.junit.runner.RunnerStrategy;

public class JUnit4RunnerStrategy implements RunnerStrategy {
	private final Class< ? extends Object> fTestClass;
	private final TestIntrospector fTestIntrospector;
	private TestNotifier fNotifier;

	public JUnit4RunnerStrategy(TestNotifier notifier, Class< ? extends Object> testClass) {
		fNotifier= notifier;
		fTestClass= testClass;
		fTestIntrospector= new TestIntrospector(fTestClass);
	}

	public int testCount() {
		return fTestIntrospector.getTestMethods(Test.class).size();
	}

	private class FailedBefore extends Exception {
		private static final long serialVersionUID= 1L;
	}
	
	public void run() {
		List<Exception> errors= fTestIntrospector.validateTestMethods();
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

	private void addFailure(Throwable exception) {
		fNotifier.fireTestFailure(new Failure(exception));
	}

	private void runBeforeClasses() throws FailedBefore {
		try {
			List<Method> beforeMethods= fTestIntrospector.getTestMethods(BeforeClass.class);
			for (Method method : beforeMethods)
				method.invoke(null);
		} catch (Exception e) {
			addFailure(e);
			throw new FailedBefore();
		}
	}

	private void runTestMethods() {
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
	
	private void invokeTestMethod(Method method) throws Exception {
		Object test= fTestClass.getConstructor().newInstance();
		new TestMethodRunner(test, method, fNotifier).run();
	}

}
