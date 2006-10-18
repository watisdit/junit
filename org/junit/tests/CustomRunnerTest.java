package org.junit.tests;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.javamodel.JavaMethod;
import org.junit.internal.runners.JavaTestInterpreter;
import org.junit.runner.JUnitCore;

// TODO: better factoring here
public class CustomRunnerTest {
	public static class CustomInterpreter extends JavaTestInterpreter {
		@Override
		public void executeMethodBody(Object test, JavaMethod javaMethod)
				throws IllegalAccessException, InvocationTargetException {
			super.executeMethodBody(test, javaMethod);
			assertGlobalStateIsValid();
		}
	}

	private static void assertGlobalStateIsValid() {
		Assert.fail();
	}

	@InterpretWith(CustomInterpreter.class)
	public static class UsesGlobalState {
		@Test
		public void foo() {
		}
	}

	@Test
	public void failsWithGlobalState() {
		assertEquals(1, JUnitCore.runClasses(UsesGlobalState.class)
				.getFailureCount());
	}

}
