package org.junit.tests;

import java.lang.reflect.Method;

import org.junit.Test;
import org.junit.runner.Failure;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunListener;

import static org.junit.Assert.*;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;

public class PreJUnit4TestCaseRunnerTest {

	static int count;
	
	static public class OneTest extends TestCase {
		public void testOne() {
		}
	}
	
	@Test public void testListener() throws Exception {
		JUnitCore runner= new JUnitCore();
		RunListener listener= new RunListener() {

			public void testStarted(Object test, String name) {
				assertEquals("testOne", name);
				count++;
			}

			public void testRunFinished(Result r) {
			}

			public void testFailure(Failure failure) {
			}

			public void testRunStarted(int testCount) {
			}

			public void testIgnored(Method method) {
			}

			public void testFinished(Object test, String name) {
			}
		};
		
		runner.addListener(listener);
		count= 0;
		Result result= runner.run(OneTest.class);
		assertEquals(1, count);
		assertEquals(1, result.getRunCount());
	}
	
	public static junit.framework.Test suite() {
		return (new JUnit4TestAdapter(PreJUnit4TestCaseRunnerTest.class));
	}
}
