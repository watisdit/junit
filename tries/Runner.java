package tries;

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

public class Runner {

	private int fCount= 0;
	private List<Throwable> fFailures= new ArrayList<Throwable>();

	public void run(Class testClass) throws Exception {
		if (junit.framework.TestCase.class.isAssignableFrom(testClass)) {
			runCompatibleTest(testClass);
			return;
		}
		List<Method> methods= getAnnotatedMethods(testClass, Test.class);
		for (Method method : methods) {
			Constructor constructor= testClass.getConstructor(new Class[0]);
			Object test= constructor.newInstance(new Object[0]);
			invokeMethod(test, method);
		}
	}

	private void runCompatibleTest(Class testClass) {
		List<junit.framework.Test> compatibleTests= getCompatibleTests(testClass);
		TestResult result= new TestResult();
		for (junit.framework.Test test : compatibleTests) {
			test.run(result);
		}
		fCount+= result.runCount();
		//TODO: About time to introduce TestFailure...
		for (TestFailure failure : result.failures()) {
			fFailures.add(failure.thrownException());
		}
		for (TestFailure failure : result.errors()) {
			fFailures.add(failure.thrownException());
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
		try {
			setUp(test);
		} catch (InvocationTargetException e) {
			fFailures.add(e.getTargetException());
			return;
		} catch (Throwable e) {
			fFailures.add(e); // TODO: Write test for this
		}
		boolean failed= false;
		try {
			method.invoke(test, new Object[0]);
			if (expectedException(method)) {
				fFailures.add(new AssertionFailedError()); //TODO: Special exception for expected exceptions not thrown?
				failed= true;
			}
		} catch (InvocationTargetException e) {
			if (isUnexpected(e.getTargetException(), method)) {
				fFailures.add(e.getTargetException());
				failed= true;
			}
		} catch (Throwable e) {
			// TODO: Make sure this can't happen
			fFailures.add(e);
		} finally {
			try {
				tearDown(test);
			} catch (InvocationTargetException e) {
				if (! failed)
					fFailures.add(e.getTargetException());
			} catch (Throwable e) {
				fFailures.add(e); // TODO: Write test for this
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

	private void tearDown(Object test) throws Exception {
		List<Method> afters= getAnnotatedMethods(test.getClass(), After.class);
		for (Method after : afters)
			after.invoke(test, new Object[0]);
	}

	private void setUp(Object test) throws Exception {
		List<Method> befores= getAnnotatedMethods(test.getClass(), Before.class);
		for (Method before : befores)
			before.invoke(test, new Object[0]);
	}

	public List<Method> getAnnotatedMethods(Class klass, Class annotationClass) {
		List<Method> results= new ArrayList<Method>();
		Method[] methods= klass.getMethods();
		for (Method each : methods) {
			Annotation annotation= each.getAnnotation(annotationClass);
			if (annotation != null)
				results.add(each);
		}
		return results;
	}

	public int getTestsRun() {
		return fCount;
	}

	public int getFailureCount() {
		return fFailures.size();
	}

	public List<Throwable> getFailures() {
		return fFailures;
	}

}
