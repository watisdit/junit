package org.junit.tests;


import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.description.Description;
import org.junit.runner.description.TestDescription;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class PreJUnit4TestCaseRunnerTest {

	static int count;
	
	static public class OneTest extends TestCase {
		public void testOne() {
		}
	}
	
	@Test public void testListener() throws Exception {
		JUnitCore runner= new JUnitCore();
		RunListener listener= new RunListener() {

			public void testStarted(TestDescription description) {
				assertEquals("testOne", description.getName());
				count++;
			}

			public void testRunFinished(Result r) {
			}

			public void testFailure(Failure failure) {
			}

			public void testRunStarted(Description description) {
			}

			public void testIgnored(TestDescription description) {
			}

			public void testFinished(TestDescription description) {
			}
		};
		
		runner.addListener(listener);
		count= 0;
		Result result= runner.run(OneTest.class);
		assertEquals(1, count);
		assertEquals(1, result.getRunCount());
	}
	
	public static junit.framework.Test suite() {
		return (new JUnit4TestAdapter(PreJUnit4TestCaseRunnerTest.class));
	}
}
