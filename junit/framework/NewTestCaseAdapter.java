package junit.framework;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Expected;
import org.junit.tries.JUnit4TestRunner;

public class NewTestCaseAdapter extends TestCase {

	private final Object fTest;
	private final Method fMethod;
	private final Expected fExpected;

	public NewTestCaseAdapter(Object test, Method method, Expected annotation) {
		fTest= test;
		fMethod= method;
		fExpected= annotation;
	}

	protected void runTest() throws Throwable {
		try {
			fMethod.invoke(fTest, new Object[0]);
		} catch (InvocationTargetException e) {
			if (fExpected == null || JUnit4TestRunner.isUnexpected(e.getCause(), fMethod))
				throw e.getCause();
		}
	}

	protected void setUp() throws Exception {
		JUnit4TestRunner.setUp(fTest);
	}

	protected void tearDown() throws Exception {
		JUnit4TestRunner.tearDown(fTest);
	}

}
