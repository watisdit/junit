package org.junit.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.util.concurrent.TimeoutException;
import junit.framework.JUnit4TestAdapter;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Runner;

public class TimeoutTest {
	
	static public class FailureWithTimeoutTest {
		@Test(timeout= 1000) public void failure() {
			fail();
		}
	}
	
	@Test public void failureWithTimeout() throws Exception {
		Runner runner= new Runner();
		runner.run(FailureWithTimeoutTest.class);
		assertEquals(1, runner.getRunCount());
		assertEquals(1, runner.getFailureCount());
		assertEquals(AssertionError.class, runner.getFailures().get(0).getException().getClass());
	}

	static public class FailureWithTimeoutRunTimeExceptionTest {
		@Test(timeout= 1000) public void failure() {
			throw new NullPointerException();
		}
	}
	
	@Test public void failureWithTimeoutRunTimeException() throws Exception {
		Runner runner= new Runner();
		runner.run(FailureWithTimeoutRunTimeExceptionTest.class);
		assertEquals(1, runner.getRunCount());
		assertEquals(1, runner.getFailureCount());
		assertEquals(NullPointerException.class, runner.getFailures().get(0).getException().getClass());
	}

	static public class SuccessWithTimeoutTest {
		@Test(timeout= 1000) public void success() {			
		}
	}
	
	@Test public void successWithTimeout() throws Exception {
		Runner runner= new Runner();
		runner.run(SuccessWithTimeoutTest.class);
		assertEquals(1, runner.getRunCount());
		assertEquals(0, runner.getFailureCount());
	}

	static public class TimeoutFailureTest {
		@Test(timeout= 1000) public void success() throws InterruptedException {			
			Thread.sleep(2000);
		}
	}
	
	@Test public void timeoutFailure() throws Exception {
		Runner runner= new Runner();
		runner.run(TimeoutFailureTest.class);
		assertEquals(1, runner.getRunCount());
		assertEquals(1, runner.getFailureCount());
		assertEquals(InterruptedException.class, runner.getFailures().get(0).getException().getClass());
	}
	
	static public class InfiniteLoopTest {
		@Test(timeout= 1000) public void failure() {
			for(;;);
		}
	}
	
	@Ignore // TODO Ignore isn't honored by the JUnit4TestCaseAdapter
	@Test public void infiniteLoop() throws Exception {
		Runner runner= new Runner();
		runner.run(InfiniteLoopTest.class);
		assertEquals(1, runner.getRunCount());
		assertEquals(1, runner.getFailureCount());
		assertEquals(TimeoutException.class, runner.getFailures().get(0).getException().getClass());
	}


	static public junit.framework.Test suite() {
		return new JUnit4TestAdapter(TimeoutTest.class);
	}
}
