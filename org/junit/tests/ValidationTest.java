package org.junit.tests;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.notify.RunNotifier;
import org.junit.plan.Plan;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runner.internal.InitializationError;
import org.junit.runner.internal.TestClassRunner;

import static org.junit.Assert.assertEquals;

public class ValidationTest {
	public static class WrongBeforeClass {
		@BeforeClass
		protected int a() {
			return 0;
		}
	}

	@Test(expected=InitializationError.class)
	public void testClassRunnerHandlesBeforeClassAndAfterClassValidation() throws InitializationError {
		new TestClassRunner(WrongBeforeClass.class, new Runner() {
			@Override
			public Plan getPlan() {
				return null;
			}

			@Override
			public void run(RunNotifier notifier) {
				// do nothing
			}
		});
	}
	
	@Test
	public void initializationErrorIsOnCorrectClass() {
		assertEquals(WrongBeforeClass.class.getName(), Request.aClass(WrongBeforeClass.class).getRunner().getPlan().getName());
	}
}
