package org.junit.tests;

import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;
import org.junit.Factory;
import org.junit.Parameters;
import org.junit.Test;
import org.junit.runner.Parameterized;
import org.junit.runner.Runner;

public class ParameterizedTestTest {
	
	@Factory(Parameterized.class) //TODO This shouldn't be called Factory, because it doesn't create anything. "Runner", perhaps?
	static public class FibonacciTest {
		@Parameters public static Object[] data() {
			return new int[][] {{0, 0}, {1, 1}, {2, 1}, {3, 2}, {4, 3}, {5, 5}, {6, 8}};
		}
		
		private int fInput; 
		private int fExpected;
		
		public FibonacciTest(int input, int expected) {
			fInput= input;
			fExpected= expected;
		}
		
		@Test public void test() {
			assertEquals(fExpected, fib(fInput));
		}

		private int fib(int x) {
			return 0;
		}
	}

	@Test public void count() {
		Runner runner= new Runner();
		runner.run(FibonacciTest.class);
		assertEquals(7, runner.getRunCount());
		assertEquals(6, runner.getFailureCount());
	}
	
	static public junit.framework.Test suite() {
		return new JUnit4TestAdapter(ParameterizedTestTest.class);
	}
}
