package junit.framework;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.tries.Runner;

public class NewTestCaseAdapter extends TestCase {

	private final Object fTest;
	private final Method fMethod;

	public NewTestCaseAdapter(Object test, Method method) {
		fTest= test;
		fMethod= method;
	}

	protected void runTest() throws Throwable {
		try {
			fMethod.invoke(fTest, new Object[0]);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}

	protected void setUp() throws Exception {
		Runner.setUp(fTest);
	}

	protected void tearDown() throws Exception {
		Runner.tearDown(fTest);
	}

}
