package org.junit.tests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.extensions.Suite;
import org.junit.runner.extensions.SuiteClasses;
import org.junit.runner.internal.RunnerFactory;

public class SuiteTest {
	
	public static class TestA {
		@Test public void pass() {
		}
	}
	
	public static class TestB {
		@Test public void fail() {
			Assert.fail();
		}
	}	
	
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
	
	@Test public void suiteTestCountIsCorrect() throws Exception {
		Runner runner= new RunnerFactory().getRunner(null, All.class);
		assertEquals(2, runner.testCount());
	}
	
	@Test public void ensureSuitesWorkWithForwardCompatibility() {
		junit.framework.Test test= new JUnit4TestAdapter(All.class);
		TestResult result= new TestResult();
		test.run(result);
		assertEquals(2, result.runCount());
	}

	@Test public void forwardCompatibilityWorksWithGetTests() {
		JUnit4TestAdapter adapter = new JUnit4TestAdapter(All.class);
		List<? extends junit.framework.Test> tests = adapter.getTests();
		assertEquals(2, tests.size());
	}
	
	@Test public void forwardCompatibilityWorksWithTestCount() {
		JUnit4TestAdapter adapter = new JUnit4TestAdapter(All.class);
		assertEquals(2, adapter.countTestCases());
	}
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(SuiteTest.class);
	}
}
