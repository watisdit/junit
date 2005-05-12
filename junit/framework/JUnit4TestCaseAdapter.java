package junit.framework; 

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.internal.runner.TestIntrospector;

public class JUnit4TestCaseAdapter extends TestCase {

	private final Object fTest;
	private final Method fMethod;
	private final TestIntrospector fTestIntrospector;

	public JUnit4TestCaseAdapter(Object test, Method method) {
		fTest= test;
		fMethod= method;
		fTestIntrospector= new TestIntrospector(fTest.getClass());
	}

	protected void runTest() throws Throwable {
		try {
			fMethod.invoke(fTest, new Object[0]);
		} catch (InvocationTargetException e) {
			if (fTestIntrospector.isUnexpected(e.getCause(), fMethod))
				throw e.getCause();
			return;
		}
		if (fTestIntrospector.expectsException(fMethod))
			throw new Exception("Expected exception: " + fTestIntrospector.expectedException(fMethod));
	}

	protected void setUp() throws Exception {
		List<Method> befores= fTestIntrospector.getTestMethods(Before.class);
		for (Method before : befores)
			before.invoke(fTest);
	}

	protected void tearDown() throws Exception {
		List<Method> afters= fTestIntrospector.getTestMethods(After.class);
		for (Method after : afters)
			after.invoke(fTest);
	}

	@Override
	public String getName() {
		return fTest.getClass().getName() + "(" + fMethod.getName() + ")";
	}
}
