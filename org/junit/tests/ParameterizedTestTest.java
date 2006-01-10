package org.junit.tests;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import junit.framework.JUnit4TestAdapter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunNotifier;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.extensions.Parameterized;
import org.junit.runner.extensions.Parameters;
import org.junit.runner.plan.CompositePlan;
import org.junit.runner.request.Request;

public class ParameterizedTestTest {
	@RunWith(Parameterized.class)
	static public class FibonacciTest {
		@Parameters
		public static Collection<Object[]> data() {
			return Arrays.asList(new Object[][] { { 0, 0 }, { 1, 1 }, { 2, 1 },
					{ 3, 2 }, { 4, 3 }, { 5, 5 }, { 6, 8 } });
		}

		private int fInput;

		private int fExpected;

		public FibonacciTest(int input, int expected) {
			fInput = input;
			fExpected = expected;
		}

		@Test
		public void test() {
			assertEquals(fExpected, fib(fInput));
		}

		private int fib(int x) {
			return 0;
		}
	}

	@Test
	public void count() {
		Result result = JUnitCore.runClasses(FibonacciTest.class);
		assertEquals(7, result.getRunCount());
		assertEquals(6, result.getFailureCount());
	}

	@Test
	public void failuresNamedCorrectly() {
		Result result = JUnitCore.runClasses(FibonacciTest.class);
		assertEquals(
				String.format("%s.test[1]()", FibonacciTest.class.getName()),
				result.getFailures().get(0).getTestHeader());
	}

	@Test
	public void countBeforeRun() throws Exception {
		Runner runner = Request.aClass(FibonacciTest.class).getRunner();
		assertEquals(7, runner.testCount());
	}

	@Test
	public void plansNamedCorrectly() throws Exception {
		Runner runner = Request.aClass(FibonacciTest.class).getRunner();
		CompositePlan plan = (CompositePlan) runner.getPlan();
		assertEquals("[0]", plan.getChildren().get(0).getName());
	}

	private static String fLog;

	@RunWith(Parameterized.class)
	static public class BeforeAndAfter {
		@BeforeClass
		public static void before() {
			fLog += "before ";
		}
		@AfterClass
		public static void after() {
			fLog += "after ";
		}

		@Parameters
		public static Collection<Object[]> data() {
			return Collections.emptyList();
		}
	}

	@Test
	public void beforeAndAfterClassAreRun() {
		fLog = "";
		JUnitCore.runClasses(BeforeAndAfter.class);
		assertEquals("before after ", fLog);
	}

	@RunWith(Parameterized.class)
	static public class EmptyTest {
		@BeforeClass
		public static void before() {
			fLog += "before ";
		}

		@AfterClass
		public static void after() {
			fLog += "after ";
		}
	}

	@Test
	public void validateClassCatchesNoParameters() {
		Result result = JUnitCore.runClasses(EmptyTest.class);
		assertEquals(1, result.getFailureCount());
	}

	@RunWith(Parameterized.class)
	static public class IncorrectTest {
		@Test
		public int test() {
			return 0;
		}

		@Parameters
		public static Collection<Object[]> data() {
			return Parameterized.eachOne(1, 2);
		}
	}

	@Test
	public void failuresAddedForBadTestMethod() throws Exception {
		Runner runner = Request.aClass(IncorrectTest.class).getRunner(new RunNotifier());
		assertEquals(null, runner);
	}

	@RunWith(Parameterized.class)
	static public class ProtectedParametersTest {
		@Parameters
		protected static Collection<Object[]> data() {
			return Collections.emptyList();
		}
	}

	@Test
	public void meaningfulFailureWhenParametersNotPublic() throws Exception {
		Result result = JUnitCore.runClasses(ProtectedParametersTest.class);
		assertEquals(1, result.getFailureCount());
		String expected = String.format(
				"No public static parameters method on class %s",
				ProtectedParametersTest.class.getName());
		assertEquals(expected, result.getFailures().get(0).getMessage());
	}

	static public junit.framework.Test suite() {
		return new JUnit4TestAdapter(ParameterizedTestTest.class);
	}
}
