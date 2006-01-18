package org.junit.tests;

import junit.framework.JUnit4TestAdapter;
import org.junit.runner.RunWith;
import org.junit.runner.extensions.Suite;
import org.junit.runner.extensions.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
		AllTestsTest.class,
		AnnotationTest.class,
		AssertionTest.class,
		CommandLineTest.class,
		ExpectedTest.class,
		ForwardCompatibilityTest.class,
		OldTests.class,
		ParameterizedTestTest.class,
		PreJUnit4TestCaseRunnerTest.class,
		RunWithTest.class,
		RunnerTest.class,
		SuiteTest.class,
		TestListenerTest.class,
		TestMethodTest.class,
		TextListenerTest.class,
		TimeoutTest.class,
		EnclosedTest.class,
		ParameterizedTestMethodTest.class,
		InitializationErrorForwardCompatibilityTest.class,
		SingleMethodTest.class,
		ValidationTest.class,
		UserStopTest.class
})
public class AllTests {	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(AllTests.class);
	}
}
