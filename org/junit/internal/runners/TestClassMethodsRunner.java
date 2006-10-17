package org.junit.internal.runners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public class TestClassMethodsRunner extends Runner implements Filterable, Sortable {
	private final List<JavaMethod> fTestMethods;
	private final JavaClass fTestClass;
	private final JavaTestInterpreter fInterpreter;

	// This assumes that some containing runner will perform validation of the test methods	
	public TestClassMethodsRunner(JavaClass klass, JavaTestInterpreter javaTestInterpreter) {
		
		// TODO: DUP?
		fTestClass= klass;
		fTestMethods= klass.getTestMethods(Test.class);
		fInterpreter= javaTestInterpreter;
	}

	// TODO: rename method to element?
	
	@Override
	public void run(RunNotifier notifier) {
		if (fTestMethods.isEmpty())
			testAborted(notifier, getDescription(), new Exception("No runnable methods"));
		for (JavaMethod method : fTestMethods)
			invokeTestMethod(method, notifier);
	}

	private void testAborted(RunNotifier notifier, Description description, Throwable cause) {
		// TODO: duped!
		// TODO: envious
		notifier.fireTestStarted(description);
		notifier.fireTestFailure(new Failure(description, cause));
		notifier.fireTestFinished(description);
	}

	@Override
	public Description getDescription() {
		Description spec= Description.createSuiteDescription(getName());
		for (JavaMethod method : fTestMethods)
				spec.addChild(methodDescription(method));
		return spec;
	}

	protected Description methodDescription(Method method) {
		return methodDescription(new JavaMethod(method));
	}

	protected String getName() {
		return getTestClass().getName();
	}
	
	protected Object createTest() throws Exception {
		return getTestClass().newTestObject();
	}

	private void invokeTestMethod(JavaMethod method, RunNotifier notifier) {
		Object test;
		try {
			test= createTest();
		} catch (InvocationTargetException e) {
			testAborted(notifier, methodDescription(method), e.getCause());
			return;			
		} catch (Exception e) {
			testAborted(notifier, methodDescription(method), e);
			return;
		}
		createMethodRunner(test, method, notifier).run();
	}

	private TestMethodRunner createMethodRunner(Object test, JavaMethod method, RunNotifier notifier) {
		return new TestMethodRunner(test, method, notifier, methodDescription(method), fInterpreter);
	}

	protected String testName(JavaModelElement method) {
		return method.getName();
	}

	private Description methodDescription(JavaMethod method) {
		return describe(method, getTestClass());
	}

	private Description describe(JavaMethod method, JavaClass testClass) {
		return Description.createTestDescription(testClass.getTestClass(), testName(method));
	}

	public void filter(Filter filter) throws NoTestsRemainException {
		for (Iterator<JavaMethod> iter= fTestMethods.iterator(); iter.hasNext();) {
			JavaMethod method= iter.next();
			if (!filter.shouldRun(methodDescription(method)))
				iter.remove();
		}
		if (fTestMethods.isEmpty())
			throw new NoTestsRemainException();
	}

	public void sort(final Sorter sorter) {
		Collections.sort(fTestMethods, new Comparator<JavaMethod>() {
			public int compare(JavaMethod o1, JavaMethod o2) {
				return sorter.compare(methodDescription(o1), methodDescription(o2));
			}
		});
	}

	protected JavaClass getTestClass() {
		return fTestClass;
	}
}