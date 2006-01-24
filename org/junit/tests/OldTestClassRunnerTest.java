package org.junit.tests;

import static org.junit.Assert.assertEquals;

import junit.extensions.TestDecorator;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Test;
import org.junit.runner.internal.OldTestClassRunner;

public class OldTestClassRunnerTest {
	public static class MyTest extends TestCase {
		public void testA() {
			
		}
	}
	
	@Test public void plansDecoratorCorrectly() {
		OldTestClassRunner runner = new OldTestClassRunner(new TestDecorator(new TestSuite(MyTest.class)));
		assertEquals(1, runner.testCount());
	}
	
}
