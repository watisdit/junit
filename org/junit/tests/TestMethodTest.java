package org.junit.tests;

import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnit4TestRunner;

import static org.junit.Assert.*;

public class TestMethodTest {
	public static class EverythingWrong {
		private EverythingWrong() {}
		@BeforeClass public void notStaticBC() {}
		@BeforeClass static void notPublicBC() {}
		@BeforeClass public static int nonVoidBC() { return 0; }
		@BeforeClass public static void argumentsBC(int i) {}
		@BeforeClass public static void fineBC() {}
		@AfterClass public void notStaticAC() {}
		@AfterClass static void notPublicAC() {}
		@AfterClass public static int nonVoidAC() { return 0; }
		@AfterClass public static void argumentsAC(int i) {}
		@AfterClass public static void fineAC() {}
		@After public static void staticA() {}
		@After void notPublicA() {}
		@After public int nonVoidA() { return 0; }
		@After public void argumentsA(int i) {}
		@After public void fineA() {}
		@Before public static void staticB() {}
		@Before void notPublicB() {}
		@Before public int nonVoidB() { return 0; }
		@Before public void argumentsB(int i) {}
		@Before public void fineB() {}
		@Test public static void staticT() {}
		@Test void notPublicT() {}
		@Test public int nonVoidT() { return 0; }
		@Test public void argumentsT(int i) {}
		@Test public void fineT() {}
	}
	@Test public void testFailures() throws Exception {
		List<Exception> problems= JUnit4TestRunner.validateTestMethods(EverythingWrong.class);
		int errorCount= 1 + 4 * 5; // missing constructor plus four invalid methods for each annotation */
		assertEquals(errorCount, problems.size());
	}
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestMethodTest.class);
	}
}
