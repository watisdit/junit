package org.junit.tests;

import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunNotifier;
import org.junit.runner.RunWith;
import org.junit.runner.extensions.Suite;
import org.junit.runner.extensions.SuiteClasses;

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
	
	@RunWith(Suite.class)
	@SuiteClasses({TestA.class, TestB.class})
	public static class All {
	}
	
	@Test public void ensureTestIsRun() {
		JUnitCore core= new JUnitCore();
		Result result= core.run(All.class);
		assertEquals(2, result.getRunCount());
		assertEquals(1, result.getFailureCount());
	}
	
	@Test public void suiteTestCountIsCorrect() {
		Suite runner= new Suite(new RunNotifier(), All.class);
		assertEquals(2, runner.testCount());
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(SuiteTest.class);
	}
}
