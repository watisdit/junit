package tries;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

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
			throw new Exception();
		}
	}
	
	public void testRunFailure() throws Exception {
		Runner runner= new Runner();
		runner.run(FailureTest.class);
		assertEquals(1, runner.getTestsRun());
		assertEquals(1, runner.getFailureCount());
		assertEquals(Exception.class, runner.getFailures().get(0).getClass());
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
		assertEquals(1, runner.getTestsRun());
		assertEquals(1, runner.getFailureCount());
		assertEquals(Error.class, runner.getFailures().get(0).getClass());
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
		assertEquals(1, runner.getTestsRun());
		assertEquals(1, runner.getFailureCount());
		assertEquals(Error.class, runner.getFailures().get(0).getClass());
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
		assertEquals(1, runner.getTestsRun());
		assertEquals(1, runner.getFailureCount());
		assertEquals(Exception.class, runner.getFailures().get(0).getClass());
		//TODO: Could also specify that (one way or the other) the run exception wraps the teardown exception
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
	
//	static public class OldTest extends TestCase {
//		public void test() {
//			run= true;
//		}
//	}
//	public void testOldTest() throws Exception {
//		Runner runner= new Runner();
//		runner.run(OldTest.class);
//		assertTrue(run);
//	}
	
	//TODO: Non-public void
	//TODO: Inherited test methods
	//TODO: Inherited before methods (make sure overriding works correctly)
	//TODO: Run more than one test class (figure out the design for this)
}
