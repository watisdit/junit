package org.junit.tests;

import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.internal.TestNotifier;

public class RunWithTest {

	private static String log;

	public static class ExampleFactory implements Runner {
		public void initialize(TestNotifier notifier, Class klass) {
			log+= "initialize";
		}

		public void run() {
			log+= "run";
		}

		public int testCount() {
			log+= "count";
			return 0;
		}
		
	}
	
	@RunWith(ExampleFactory.class)
	public static class ExampleTest {
	}
	
	@Test public void run() {
		log= "";

		JUnitCore.runClasses(ExampleTest.class);
		assertTrue(log.contains("count"));
		assertTrue(log.contains("initialize"));
		assertTrue(log.contains("run"));
	}
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(RunWithTest.class);
	}
}
