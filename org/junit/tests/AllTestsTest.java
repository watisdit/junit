package org.junit.tests;

import static org.junit.Assert.assertTrue;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Factory;
import org.junit.extensions.AllTests;
import org.junit.runner.Runner;

public class AllTestsTest {
	
	private static boolean run;
	
	public static class OneTest extends TestCase {
		public void testSomething() {
			run= true;
		}
	}
	
	@Factory(AllTests.class)
	public static class All {
		static public junit.framework.Test suite() {
			TestSuite suite= new TestSuite();
			suite.addTestSuite(OneTest.class);
			return suite;
		}
	}
	
	@org.junit.Test public void ensureTestIsRun() {
		Runner runner= new Runner();
		runner.run(All.class);
		assertTrue(run);
	}
}
