package org.junit.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.util.concurrent.TimeoutException;
import junit.framework.JUnit4TestAdapter;
import junit.framework.TestResult;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class TimeoutTest {
	
	static public class FailureWithTimeoutTest {
		@Test(timeout= 1000) public void failure() {
			fail();
		}
	}
	
	@Test public void failureWithTimeout() throws Exception {
		JUnitCore core= new JUnitCore();
		Result runner= core.run(FailureWithTimeoutTest.class);
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
		JUnitCore core= new JUnitCore();
		Result runner= core.run(FailureWithTimeoutRunTimeExceptionTest.class);
		assertEquals(1, runner.getRunCount());
		assertEquals(1, runner.getFailureCount());
		assertEquals(NullPointerException.class, runner.getFailures().get(0).getException().getClass());
	}

	static public class SuccessWithTimeoutTest {
		@Test(timeout= 1000) public void success() {			
		}
	}
		
	@Test public void successWithTimeout() throws Exception {
		JUnitCore core= new JUnitCore();
		Result runner= core.run(SuccessWithTimeoutTest.class);
		assertEquals(1, runner.getRunCount());
		assertEquals(0, runner.getFailureCount());
	}

	static public class TimeoutFailureTest {
		@Test(timeout= 100) public void success() throws InterruptedException {			
			Thread.sleep(200);
		}
	}
	
	@Test public void timeoutFailure() throws Exception {
		JUnitCore core= new JUnitCore();
		Result runner= core.run(TimeoutFailureTest.class);
		assertEquals(1, runner.getRunCount());
		assertEquals(1, runner.getFailureCount());
		assertEquals(InterruptedException.class, runner.getFailures().get(0).getException().getClass());
	}
	
	static public class InfiniteLoopTest {
		@Test(timeout= 100) public void failure() {
			for(;;);
		}
	}
	
	@Test public void infiniteLoop() throws Exception {
		JUnitCore core= new JUnitCore();
		Result runner= core.run(InfiniteLoopTest.class);
		assertEquals(1, runner.getRunCount());
		assertEquals(1, runner.getFailureCount());
		assertEquals(TimeoutException.class, runner.getFailures().get(0).getException().getClass());
	}

	@Test public void compatibility() {
		TestResult result= new TestResult();
		new JUnit4TestAdapter(InfiniteLoopTest.class).run(result);
		assertEquals(1, result.errorCount());
	}
	
	static public junit.framework.Test suite() {
		return new JUnit4TestAdapter(TimeoutTest.class);
	}
}
