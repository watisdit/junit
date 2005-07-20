package org.junit.internal.runner;

import java.lang.reflect.Method;

import org.junit.runner.Failure;

public interface TestNotifier {

	public void fireTestStarted(Object test, String name);

	public void fireTestFinished(Object test, String name);
	
	public void fireTestIgnored(Method method);

	public void fireTestFailure(Failure failure);

}