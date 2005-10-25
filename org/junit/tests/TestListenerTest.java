package org.junit.tests;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import junit.framework.JUnit4TestAdapter;
import org.junit.Test;
import org.junit.runner.Failure;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunListener;

public class TestListenerTest {
	
	int count;
	
	class ErrorListener implements RunListener {
		public void testRunStarted(int testCount) throws Exception {
			throw new Error();
		}

		public void testRunFinished(Result result) {
		}

		public void testStarted(Object test, String name) {
		}

		public void testFinished(Object test, String name) {
		}

		public void testFailure(Failure failure) {
		}

		public void testIgnored(Method method) {
		}
	}
	
	public static class OneTest {
		@Test public void nothing() {}
	}
	
	@Test(expected=Error.class) public void failingListener() {
		JUnitCore runner= new JUnitCore();
		runner.addListener(new ErrorListener());
		runner.run(OneTest.class);
	}
	
	class ExceptionListener extends ErrorListener {
		@Override
		public void testRunStarted(int testCount) throws Exception {
			count++;
			throw new Exception();
		}
	}
	
	@Test public void removeFailingListeners() {
		JUnitCore core= new JUnitCore();
		core.addListener(new ExceptionListener());
		
		// TODO: a fresh result each time?
		count= 0;
		Result result= core.run(OneTest.class);
		assertEquals(1, count);
		assertEquals(1, result.getFailureCount());

		count= 0;
		core.run(OneTest.class);
		assertEquals(0, count); // Doesn't change because listener was removed		
	}
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestListenerTest.class);
	}

}
