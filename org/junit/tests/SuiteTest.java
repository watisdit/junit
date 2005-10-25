package org.junit.tests;

import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class SuiteTest {
	
	public static class TestA {
		@Test public void pass() {
			// fail();
		}
	}
	
	public static class TestB {
		@Test public void fail() {
			fail();
		}
	}	
// TODO	
//	@Factory(Suite.class)
//	@SuiteClasses({TestA.class, TestB.class})
	// @SuitePackage("org.junit.tests")
	public static class All {
	}
	
	@Ignore @Test public void ensureTestIsRun() {
		JUnitCore core= new JUnitCore();
		Result result= core.run(All.class);
		assertEquals(2, result.getRunCount());
		assertEquals(1, result.getFailureCount());
	}
}
