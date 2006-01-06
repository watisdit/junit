package org.junit.tests;

import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.RunNotifier;
import org.junit.runner.Runner;
import org.junit.runner.internal.TestClassRunner;
import org.junit.runner.plan.Plan;

public class ValidationTest {
	public static class WrongBeforeClass {
		@BeforeClass
		protected int a() {
			return 0;
		}
	}

	@Test
	public void testClassRunnerHandlesBeforeClassAndAfterClassValidation() {
		TestClassRunner runner = new TestClassRunner(WrongBeforeClass.class, new Runner() {
			@Override
			public Plan getPlan() {
				return null;
			}

			@Override
			public void run(RunNotifier notifier) {
				// do nothing
			}
		});
		RunNotifier notifier = new RunNotifier();
		Result result = new Result();
		result.addListenerTo(notifier);
		runner.canRun(notifier);
		assertTrue(result.getFailureCount() > 0);
	}
}
