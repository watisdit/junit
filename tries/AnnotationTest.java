package tries;

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
	
	static public class SetupFailureTest {
		@Before
		public void before() throws Exception {
			throw new Exception();
		}
		@Test
		public void success() {
			run= true;
		}
		@After
		public void after() {
			fail("Shouldn't have run after");
		}
	}
	public void testSetupFailure() throws Exception {
		Runner runner= new Runner();
		runner.run(SetupFailureTest.class);
		assertFalse("Shouldn't have run test method", run);		
	}


}
