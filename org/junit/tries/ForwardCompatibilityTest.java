package org.junit.tries;

import junit.framework.NewTestClassAdapter;
import junit.framework.TestCase;
import junit.framework.TestResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ForwardCompatibilityTest extends TestCase {
	static String fLog;
	
	static public class NewTest {
		@Before public void before() { 
			fLog+= "before "; 
		}
		@After public void after() {
			fLog+= "after ";
		}
		@Test public void test() {
			fLog+= "test ";
		}
	}
	
	public void testCompatibility() {
		fLog= "";
		TestResult result= new TestResult();
		junit.framework.Test adapter= new NewTestClassAdapter(NewTest.class);
		adapter.run(result);
		assertEquals("before test after ", fLog);
	}
	
	static Exception exception= new Exception();
	
	public static class ErrorTest {
		@Test public void error() throws Exception {
			throw exception;
		}
	}
	public void testException() {
		TestResult result= new TestResult();
		junit.framework.Test adapter= new NewTestClassAdapter(ErrorTest.class);
		adapter.run(result);
		assertEquals(exception, result.errors().get(0).thrownException());
	}
}
