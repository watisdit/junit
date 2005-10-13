package org.junit.tests;

import junit.framework.JUnit4TestAdapter;
import org.junit.Factory;
import org.junit.Test;
import org.junit.internal.runner.TestNotifier;
import org.junit.runner.Runner;
import org.junit.runner.RunnerStrategy;
import static org.junit.Assert.*;

public class FactoryTest {

	private static String log;

	public static class ExampleFactory implements RunnerStrategy {
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
	
	@Factory(ExampleFactory.class)
	public static class ExampleTest {
	}
	
	@Test public void run() {
		log= "";
		Runner runner= new Runner();
		runner.run(ExampleTest.class);
		assertTrue(log.contains("count"));
		assertTrue(log.contains("initialize"));
		assertTrue(log.contains("run"));
	}
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(FactoryTest.class);
	}
}
