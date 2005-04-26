package org.junit.tries;

import org.junit.Test;
import org.junit.TestListener;
import static org.junit.Assert.*;

import junit.framework.NewTestClassAdapter;
import junit.framework.TestCase;

public class PreJUnit4TestCaseRunnerTest {

	static int count;
	
	static public class OneTest extends TestCase {
		public void testOne() {
		}
	}
	
	@Test public void testListener() throws Exception {
		Runner runner= new Runner();
		TestListener listener= new TestListener() {

			public void testStarted(Object test, String name) {
				assertEquals("testOne", name);
				count++;
			}

			public void testRunFinished(Runner runner, long runTime) {
			}

			public void failure(Throwable exception) {
			}
		};
		
		runner.addListener(listener);
		count= 0;
		runner.run(OneTest.class);
		assertEquals(1, count);
		assertEquals(2, runner.getRunCount());
	}
	
	public static junit.framework.Test suite() {
		return (new NewTestClassAdapter(PreJUnit4TestCaseRunnerTest.class));
	}
}
