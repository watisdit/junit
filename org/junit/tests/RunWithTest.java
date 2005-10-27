package org.junit.tests;

import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;
import org.junit.Test;
import org.junit.runner.ClassRunner;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunNotifier;
import org.junit.runner.RunWith;

public class RunWithTest {

	private static String log;

	public static class ExampleRunner extends ClassRunner {
		public ExampleRunner(RunNotifier notifier, Class<? extends Object> klass) {
			super(notifier, klass);
			log+= "initialize";
		}

		@Override
		public void run() {
			log+= "run";
		}

		@Override
		public int testCount() {
			log+= "count";
			return 0;
		}
		
	}
	
	@RunWith(ExampleRunner.class)
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
