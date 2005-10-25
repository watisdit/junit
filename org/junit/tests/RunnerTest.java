package org.junit.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.Failure;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunListener;

public class RunnerTest {

	private boolean wasRun;

	public class MyListener implements RunListener {

		int testCount;

		public void testRunStarted(int testCount) {
			this.testCount= testCount;
		}

		public void testRunFinished(Result result) {
		}

		public void testStarted(Object test, String name) {
		}

		public void testFailure(Failure failure) {
		}

		public void testIgnored(Method method) {
		}

		public void testFinished(Object test, String name) {
		}
		
	}
	
	public static class Example {
		@Test public void empty() {
		}
	}
	
	@Test public void newTestCount() {
		JUnitCore runner= new JUnitCore();
		MyListener listener= new MyListener();
		runner.addListener(listener);
		runner.run(Example.class);
		assertEquals(1, listener.testCount);
	}
	
	public static class ExampleTest extends TestCase {
		public void testEmpty() {
		}
	}
	
	@Test public void oldTestCount() {
		JUnitCore runner= new JUnitCore();
		MyListener listener= new MyListener();
		runner.addListener(listener);
		runner.run(ExampleTest.class);
		assertEquals(1, listener.testCount);
	}
	
	public static class NewExample {
		@Test public void empty() {
		}
	}
	
	@Test public void testFinished() {
		JUnitCore runner= new JUnitCore();
		wasRun= false;
		RunListener listener= new MyListener() {
			@Override
			public void testFinished(Object test, String name) {
				wasRun= true;
			}
		};
		runner.addListener(listener);
		runner.run(NewExample.class);
		assertTrue(wasRun);
	}
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(RunnerTest.class);
	}
}
