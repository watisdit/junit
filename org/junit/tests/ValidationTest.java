package org.junit.tests;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JavaTestInterpreter;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.Request;

public class ValidationTest {
	public static class WrongBeforeClass {
		@BeforeClass
		protected int a() {
			return 0;
		}
	}

	@Test(expected=InitializationError.class)
	public void testClassRunnerHandlesBeforeClassAndAfterClassValidation() throws InitializationError {
		new TestClassRunner(WrongBeforeClass.class, new JavaTestInterpreter());
	}
	
	@Test
	public void initializationErrorIsOnCorrectClass() {
		assertEquals(WrongBeforeClass.class.getName(), 
				Request.aClass(WrongBeforeClass.class).getRunner().getDescription().getDisplayName());
	}
}
