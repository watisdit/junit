package org.junit.tests;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Expected;
import org.junit.Test;
import org.junit.runner.Runner;

public class AnnotationTest extends TestCase {
	static boolean run;

	public void setUp() {
		run= false;
	}
	
	static public class SimpleTest {
		@Test
		public void success() {
			run= true;
		}
	}

	public void testAnnotatedMethod() throws Exception {
		Runner runner= new Runner();
		runner.run(SimpleTest.class);
		assertTrue(run);
	}
	
	static public class SetupTest {
		@Before
		public void before() {
			run= true;
		}
		@Test
		public void success() {
		}
	}
	
	public void testSetup() throws Exception {
		Runner runner= new Runner();
		runner.run(SetupTest.class);
		assertTrue(run);
	}
	
	static public class TeardownTest {
		@After
		public void after() {
			run= true;
		}
		@Test
		public void success() {
		}
	}

	public void testTeardown() throws Exception {
		Runner runner= new Runner();
		runner.run(TeardownTest.class);
		assertTrue(run);		
	}
	
	static public class FailureTest {
		@Test
		public void error() throws Exception {
			org.junit.Assert.fail();
		}
	}
	
	public void testRunFailure() throws Exception {
		Runner runner= new Runner();
		runner.run(FailureTest.class);
		assertEquals(1, runner.getRunCount());
		assertEquals(1, runner.getFailureCount());
		assertEquals(AssertionError.class, runner.getFailures().get(0).getException().getClass());
	}
	
	static public class SetupFailureTest {
		@Before
		public void before() {
			throw new Error();
		}
		@Test
		public void test() {
			run= true;
		}
		@After
		public void after() {
			run= true;
		}
	}
	
	public void testSetupFailure() throws Exception {
		Runner runner= new Runner();
		runner.run(SetupFailureTest.class);
		assertEquals(1, runner.getRunCount());
		assertEquals(1, runner.getFailureCount());
		assertEquals(Error.class, runner.getFailures().get(0).getException().getClass());
		assertFalse(run);
	}

	static public class TeardownFailureTest {
		@After
		public void after() {
			throw new Error();
		}
		@Test
		public void test() {
		}
	}
	
	public void testTeardownFailure() throws Exception {
		Runner runner= new Runner();
		runner.run(TeardownFailureTest.class);
		assertEquals(1, runner.getRunCount());
		assertEquals(1, runner.getFailureCount());
		assertEquals(Error.class, runner.getFailures().get(0).getException().getClass());
	}
	
	static public class TestAndTeardownFailureTest {
		@After
		public void after() {
			throw new Error();
		}
		@Test
		public void test() throws Exception {
			throw new Exception();
		}
	}
	
	public void testTestAndTeardownFailure() throws Exception {
		Runner runner= new Runner();
		runner.run(TestAndTeardownFailureTest.class);
		assertEquals(1, runner.getRunCount());
		assertEquals(2, runner.getFailureCount());
		assertEquals(Exception.class, runner.getFailures().get(0).getException().getClass());
		assertEquals(Error.class, runner.getFailures().get(1).getException().getClass());
	}
	
	static public class TeardownAfterFailureTest {
		@After
		public void after() {
			run= true;
		}
		@Test
		public void test() throws Exception {
			throw new Exception();
		}
	}
	
	public void testTeardownAfterFailure() throws Exception {
		Runner runner= new Runner();
		runner.run(TeardownAfterFailureTest.class);
		assertTrue(run);
	}
	
	static int count= 0;
	static Set<Object> tests= new HashSet<Object>();
	static public class TwoTests {
		@Test
		public void one() {
			count++;
			tests.add(this);
		}
		@Test
		public void two() {
			count++;
			tests.add(this);
		}
	}
	
	public void testTwoTests() throws Exception {
		Runner runner= new Runner();
		runner.run(TwoTests.class);
		assertEquals(2, count);
		assertEquals(2, tests.size());
	}
	
	static public class OldTest extends TestCase {
		public void test() {
			run= true;
		}
	}
	public void testOldTest() throws Exception {
		Runner runner= new Runner();
		runner.run(OldTest.class);
		assertTrue(run);
	}
	
	static public class OldSuiteTest extends TestCase {
		public void testOne() {
			run= true;
		}
	}
	
	public void testOldSuiteTest() throws Exception {
		TestSuite suite= new TestSuite(OldSuiteTest.class);
		Runner runner= new Runner();
		runner.run(suite);
		assertTrue(run);
	}
	
	static public class ExceptionTest {
		@Test
		@Expected( Error.class)
		public void expectedException() {
			throw new Error();
		}
	}
	
	public void testException() throws Exception {
		Runner runner= new Runner();
		runner.run(ExceptionTest.class);
		assertEquals(0, runner.getFailureCount());
	}
	
	static public class NoExceptionTest {
		@Test
		@Expected( Error.class)
		public void expectedException() {
		}
	}

	public void testExceptionNotThrown() throws Exception {
		Runner runner= new Runner();
		runner.run(NoExceptionTest.class);
		assertEquals(1, runner.getFailureCount());
		assertEquals("Expected exception: java.lang.Error", runner.getFailures().get(0).getMessage());
	}
	
	static public class OneTimeSetup {
		@BeforeClass public static void once() {
			count++;
		}
		@Test public void one() {};
		@Test public void two() {};
	}
	
	public void testOneTimeSetup() throws Exception {
		count= 0;
		Runner runner= new Runner();
		runner.run(OneTimeSetup.class);
		assertEquals(1, count);
	}
	
	static public class OneTimeTeardown {
		@AfterClass public static void once() {
			count++;
		}
		@Test public void one() {};
		@Test public void two() {};
	}
	
	public void testOneTimeTeardown() throws Exception {
		count= 0;
		Runner runner= new Runner();
		runner.run(OneTimeTeardown.class);
		assertEquals(1, count);
	}
	
	static String log;
	
	public static class OrderTest {
		@BeforeClass public static void onceBefore() { log+= "beforeClass "; }
		@Before public void before() { log += "before "; }
		@Test public void test() { log += "test "; }
		@After public void after() { log += "after "; }
		@AfterClass public static void onceAfter() { log += "afterClass "; }
	}
	
	public void testOrder() throws Exception {
		log= "";
		Runner runner= new Runner();
		runner.run(OrderTest.class);
		assertEquals("beforeClass before test after afterClass ", log);
	}
	
	static public class NonStaticOneTimeSetup {
		@BeforeClass public void once() {
		}
	}
	
	public void testNonStaticOneTimeSetup() throws Exception {
		Runner runner= new Runner();
		runner.run(NonStaticOneTimeSetup.class);
		assertEquals(1, runner.getFailureCount());
	}
	
	static public class ErrorInBeforeClass {
		@BeforeClass public static void before() throws Exception {
			throw new Exception();
		}
		@Test public void test() {
			run= true;
		}
		@AfterClass public static void after() {
			run= true;
		}
	}
	
	public void testErrorInBeforeClass() throws Exception {
		run= false;
		Runner runner= new Runner();
		runner.run(ErrorInBeforeClass.class);
		assertFalse(run);
		assertEquals(1, runner.getFailureCount());
	}

	static public class ErrorInAfterClass {
		@Test public void test() {
			run= true;
		}
		@AfterClass public static void after() throws Exception {
			throw new Exception();
		}
	}
	
	public void testErrorInAfterClass() throws Exception {
		run= false;
		Runner runner= new Runner();
		runner.run(ErrorInAfterClass.class);
		assertTrue(run);
		assertEquals(1, runner.getFailureCount());
	}

	//TODO: Inherited test methods, before class, before, test, after, after class
}
