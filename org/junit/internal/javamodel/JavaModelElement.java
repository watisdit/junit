package org.junit.internal.javamodel;

import org.junit.internal.runners.JavaTestInterpreter;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public abstract class JavaModelElement extends Runner {
	// TODO: only construct through interpreter
	
	private final JavaTestInterpreter fInterpreter;

	public JavaModelElement(JavaTestInterpreter interpreter) {
		fInterpreter = interpreter;
	}
	
	public abstract String getName();

	public void addFailure(RunNotifier runNotifier, Throwable targetException) {
		runNotifier
				.fireTestFailure(new Failure(getDescription(), targetException));
	}
	
	protected JavaTestInterpreter getInterpreter() {
		return fInterpreter;
	}
}
