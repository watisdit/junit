package org.junit.tests;

import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Runner;

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
		Runner runner= new Runner();
		runner.run(All.class);
		assertEquals(2, runner.getRunCount());
		assertEquals(1, runner.getFailureCount());
	}
}
