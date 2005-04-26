package org.junit.tries;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;

public class PreJUnit4TestCaseRunner implements TestListener {
	
	private Runner fRunner;
	private final Class fTestClass;

	PreJUnit4TestCaseRunner(Runner runner, Class testClass) {
		fRunner= runner;
		fTestClass= testClass;
	}
	
	void runCompatibleTest() {
		List<junit.framework.Test> compatibleTests= getCompatibleTests();
		TestResult result= new TestResult();
		result.addListener(this);
		for (junit.framework.Test test : compatibleTests) {
			test.run(result);
		}
	}

	private List<junit.framework.Test> getCompatibleTests() {
		List<junit.framework.Test> tests= new ArrayList<junit.framework.Test>();
		try {
			getTestConstructor(fTestClass); // Avoid generating multiple error messages
		} catch (NoSuchMethodException e) {
			tests.add(warning("Class "+fTestClass.getName()+" has no public constructor TestCase(String name) or TestCase()"));
			return new ArrayList<junit.framework.Test>();
		}
		if (!Modifier.isPublic(fTestClass.getModifiers())) {
			tests.add(warning("Class "+fTestClass.getName()+" is not public"));
			return new ArrayList<junit.framework.Test>();
		}
		Class superClass= fTestClass;
		List<String> names= new ArrayList<String>();
		while (junit.framework.Test.class.isAssignableFrom(superClass)) {
			for (Method each : superClass.getDeclaredMethods())
				addTestMethod(tests, each, names, fTestClass);
			superClass= superClass.getSuperclass();
		}
		if (tests.size() == 0)
			tests.add(warning("No tests found in "+fTestClass.getName()));
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

	// Implement junit.framework.TestListener
	public void addError(Test test, Throwable t) {
		fRunner.addFailure(t);
	}

	public void addFailure(Test test, AssertionFailedError t) {
		fRunner.addFailure(t);
	}

	public void endTest(Test test) {
	}

	public void startTest(Test test) {
		fRunner.startTestCase(test, ((TestCase) test).getName());
	}

}
