/**
 * 
 */
package org.junit.internal.runners;


import org.junit.runner.notification.RunNotifier;

public class TestEnvironment {
	private final JavaTestInterpreter fInterpreter;

	final RunNotifier fNotifier;

	public TestEnvironment(JavaTestInterpreter interpreter,
			RunNotifier notifier) {
		fInterpreter= interpreter;
		fNotifier= notifier;
	}

	public JavaTestInterpreter getInterpreter() {
		return fInterpreter;
	}

	public RunNotifier getRunNotifier() {
		return fNotifier;
	}
}