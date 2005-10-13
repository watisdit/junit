package org.junit.tests;

import static org.junit.Assert.assertTrue;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runner.extensions.AllTests;

public class AllTestsTest {
	
	private static boolean run;
	
	public static class OneTest extends TestCase {
		public void testSomething() {
			run= true;
		}
	}
	
	@RunWith(AllTests.class)
	public static class All {
		static public junit.framework.Test suite() {
			TestSuite suite= new TestSuite();
			suite.addTestSuite(OneTest.class);
			return suite;
		}
	}
	
	@org.junit.Test public void ensureTestIsRun() {
		JUnitCore runner= new JUnitCore();
		runner.run(All.class);
		assertTrue(run);
	}
}
