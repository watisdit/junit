package org.junit.tests;

import junit.framework.JUnit4TestAdapter;
import org.junit.Parameter;
import org.junit.Parameters;
import org.junit.Test;
import org.junit.runner.Runner;
import static org.junit.Assert.*;

public class ParameterizedTestTest {
	static public class FibonacciTest {
		@Parameters public static Object[] data() {
			return new int[][] {{0, 0}, {1, 1}, {2, 1}, {3, 2}, {4, 3}, {5, 5}, {6, 8}};
		}
		
		@Parameter(0) public int expected;
		@Parameter(1) public int input; 
		
		@Test public void test() {
			assertEquals(expected, fib(input));
		}

		private int fib(int input2) {
			return 0;
		}
	}

	@Test public void count() {
		Runner runner= new Runner();
		runner.run(FibonacciTest.class);
		assertEquals(7, runner.getRunCount());
		assertEquals(6, runner.getFailureCount());
	}
	
//	static public class ParameterizedTest {
//		@Parameters public static Object[] data() {
//			if (data == null)
//				data= computeData();
//			return data;
//		}
//		@Parameters public static Object[] data= computeData();
//	}

	static public junit.framework.Test suite() {
		return new JUnit4TestAdapter(ParameterizedTestTest.class);
	}
}
