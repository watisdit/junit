package org.junit.tries;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Expected;
import org.junit.Test;
import org.junit.TestListener;

public class Runner {

	private int fCount= 0;
	private List<Throwable> fFailures= new ArrayList<Throwable>();
	private List<TestListener> fListeners= new ArrayList<TestListener>();

	public void run(Class testClass) throws Exception { //TODO: This shouldn't throw, just report any problems
		long startTime= System.currentTimeMillis();
		if (junit.framework.TestCase.class.isAssignableFrom(testClass)) {
			runCompatibleTest(testClass);
			return;
		}
		List<Method> beforeMethods= getAnnotatedMethods(testClass, BeforeClass.class);
		for (Method method : beforeMethods) {
			if (validateOneTimeMethod(method)) {
				addFailure(new Exception("@beforeClass methods have to be public static")); 
				return;
			}
			method.invoke(null, new Object[0]);
		}
		List<Method> methods= getAnnotatedMethods(testClass, Test.class);
		for (Method method : methods) {
			Constructor constructor= testClass.getConstructor(new Class[0]);
			Object test= constructor.newInstance(new Object[0]);
			invokeMethod(test, method);
		}
		List<Method> afterMethods= getAnnotatedMethods(testClass, AfterClass.class);
		for (Method method : afterMethods) {
			method.invoke(null, new Object[0]);
		}
		long endTime= System.currentTimeMillis();
		for (TestListener each : fListeners)
			each.testRunFinished(this, endTime - startTime);
	}

	private void addFailure(Throwable exception) {
		fFailures.add(exception); //TODO Add a TestFailure that includes the test that failed
		for (TestListener each : fListeners)
			each.failure(exception);
	}

	//TODO: Have this throw an exception if the method is invalid
	public static boolean validateOneTimeMethod(Method method) {
		return !Modifier.isStatic(method.getModifiers());
	}
	
	//TODO: Think about how to move the compatibility stuff to another class--it's gunking up this one
	//TODO: This implementation will also not inform test fListeners correctly
	private void runCompatibleTest(Class testClass) {
		List<junit.framework.Test> compatibleTests= getCompatibleTests(testClass);
		TestResult result= new TestResult();
		for (junit.framework.Test test : compatibleTests) {
			test.run(result);
		}
		fCount+= result.runCount();
		//TODO: About time to introduce TestFailure... maybe...
		for (TestFailure failure : result.failures()) {
			addFailure(failure.thrownException());
		}
		for (TestFailure failure : result.errors()) {
			addFailure(failure.thrownException());
		}
	}

	private List<junit.framework.Test> getCompatibleTests(Class testClass) {
		List<junit.framework.Test> tests= new ArrayList<junit.framework.Test>();
		try {
			getTestConstructor(testClass); // Avoid generating multiple error messages
		} catch (NoSuchMethodException e) {
			tests.add(warning("Class "+testClass.getName()+" has no public constructor TestCase(String name) or TestCase()"));
			return new ArrayList<junit.framework.Test>();
		}
		if (!Modifier.isPublic(testClass.getModifiers())) {
			tests.add(warning("Class "+testClass.getName()+" is not public"));
			return new ArrayList<junit.framework.Test>();
		}
		Class superClass= testClass;
		List<String> names= new ArrayList<String>();
		while (junit.framework.Test.class.isAssignableFrom(superClass)) {
			for (Method each : superClass.getDeclaredMethods())
				addTestMethod(tests, each, names, testClass);
			superClass= superClass.getSuperclass();
		}
		if (tests.size() == 0)
			tests.add(warning("No tests found in "+testClass.getName()));
		return tests;
	}

	public Constructor getTestConstructor(Class theClass) throws NoSuchMethodException {
		Class[] args= { String.class };
		try {
			return theClass.getConstructor(args);	
		} catch (NoSuchMethodException e) {
			// fall through
		}
		return theClass.getConstructor(new Class[0]);
	}

	public static junit.framework.Test warning(final String message) {
		return new TestCase("warning") {
			protected void runTest() {
				fail(message);
			}
		};
	}

	private void addTestMethod(List<junit.framework.Test> tests, Method m, List<String> names, Class theClass) {
		String name= m.getName();
		if (names.contains(name))
			return;
		if (! isPublicTestMethod(m)) {
			if (isTestMethod(m))
				tests.add(warning("Test method isn't public: "+m.getName()));
			return;
		}
		names.add(name);
		tests.add(createTest(theClass, name));
	}

	public junit.framework.Test createTest(Class theClass, String name) {
		Constructor constructor;
		try {
			constructor= getTestConstructor(theClass);
		} catch (NoSuchMethodException e) {
			return warning("Class "+theClass.getName()+" has no public constructor TestCase(String name) or TestCase()");
		}
		Object test;
		try {
			if (constructor.getParameterTypes().length == 0) {
				test= constructor.newInstance(new Object[0]);
				if (test instanceof TestCase)
					((TestCase) test).setName(name);
			} else {
				test= constructor.newInstance(new Object[]{name});
			}
		} catch (InstantiationException e) {
			return(warning("Cannot instantiate test case: "+name+" ("+exceptionToString(e)+")"));
		} catch (InvocationTargetException e) {
			return(warning("Exception in constructor: "+name+" ("+exceptionToString(e.getTargetException())+")"));
		} catch (IllegalAccessException e) {
			return(warning("Cannot access test case: "+name+" ("+exceptionToString(e)+")"));
		}
		return (junit.framework.Test) test;
	}
	
	private static String exceptionToString(Throwable t) {
		StringWriter stringWriter= new StringWriter();
		PrintWriter writer= new PrintWriter(stringWriter);
		t.printStackTrace(writer);
		return stringWriter.toString();
	}

	private boolean isPublicTestMethod(Method m) {
		return isTestMethod(m) && Modifier.isPublic(m.getModifiers());
	 }
	 
	private boolean isTestMethod(Method m) {
		String name= m.getName();
		Class[] parameters= m.getParameterTypes();
		Class returnType= m.getReturnType();
		return parameters.length == 0 && name.startsWith("test") && returnType.equals(Void.TYPE);
	 }

	//TODO: public void run(TestSuite suite) ...
	
	private void invokeMethod(Object test, Method method) {
		fCount++;
		for (TestListener each : fListeners)
			each.testStarted(test, method.getName());
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
				addFailure(new AssertionFailedError()); //TODO: Error string specifying exception that should have been thrown
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
				if (! failed)
					addFailure(e.getTargetException());
			} catch (Throwable e) {
				addFailure(e); // TODO: Write test for this
			}
		}
	}

	private boolean expectedException(Method method) {
		return method.getAnnotation(Expected.class) != null;
	}

	private boolean isUnexpected(Throwable exception, Method method) {
		Expected annotation= method.getAnnotation(Expected.class);
		if (annotation == null) 
			return true;
		return ! annotation.value().equals(exception.getClass());
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

	public int getRunCount() {
		return fCount;
	}

	public int getFailureCount() {
		return fFailures.size();
	}

	public List<Throwable> getFailures() {
		return fFailures;
	}

	public void addListener(TestListener listener) {
		fListeners.add(listener);
	}

	public boolean wasSuccessful() {
		return getFailureCount() == 0;
	}

}
