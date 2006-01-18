package org.junit.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.notify.Failure;
import org.junit.notify.RunListener;
import org.junit.plan.LeafPlan;
import org.junit.plan.Plan;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class RunnerTest {

	private boolean wasRun;

	public class MyListener implements RunListener {

		int testCount;

		public void testRunStarted(Plan plan) {
			this.testCount= plan.testCount();
		}

		public void testRunFinished(Result result) {
		}

		public void testStarted(LeafPlan plan) {
		}

		public void testFailure(Failure failure) {
		}

		public void testIgnored(LeafPlan plan) {
		}

		public void testFinished(LeafPlan plan) {
		}
		
	}
	
	public static class Example {
		@Test public void empty() {
		}
	}
	
	@Test public void newTestCount() {
		JUnitCore runner= new JUnitCore();
		MyListener listener= new MyListener();
		runner.addListener(listener);
		runner.run(Example.class);
		assertEquals(1, listener.testCount);
	}
	
	public static class ExampleTest extends TestCase {
		public void testEmpty() {
		}
	}
	
	@Test public void oldTestCount() {
		JUnitCore runner= new JUnitCore();
		MyListener listener= new MyListener();
		runner.addListener(listener);
		runner.run(ExampleTest.class);
		assertEquals(1, listener.testCount);
	}
	
	public static class NewExample {
		@Test public void empty() {
		}
	}
	
	@Test public void testFinished() {
		JUnitCore runner= new JUnitCore();
		wasRun= false;
		RunListener listener= new MyListener() {
			@Override
			public void testFinished(LeafPlan plan) {
				wasRun= true;
			}
		};
		runner.addListener(listener);
		runner.run(NewExample.class);
		assertTrue(wasRun);
	}
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(RunnerTest.class);
	}
}
