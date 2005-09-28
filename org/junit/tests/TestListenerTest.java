package org.junit.tests;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import junit.framework.JUnit4TestAdapter;
import org.junit.Test;
import org.junit.runner.Failure;
import org.junit.runner.Runner;
import org.junit.runner.TestListener;

public class TestListenerTest {
	
	int count;
	
	class ErrorListener implements TestListener {
		public void testRunStarted(int testCount) throws Exception {
			throw new Error();
		}

		public void testRunFinished(Runner runner) {
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
		Runner runner= new Runner();
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
		Runner runner= new Runner();
		runner.addListener(new ExceptionListener());
		
		count= 0;
		runner.run(OneTest.class);
		assertEquals(1, count);
		assertEquals(1, runner.getFailureCount());

		count= 0;
		runner.run(OneTest.class);
		assertEquals(0, count); // Doesn't change because listener was removed		
	}
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestListenerTest.class);
	}

}
