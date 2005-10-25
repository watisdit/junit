package org.junit.runner;

import java.lang.reflect.Method;


public interface RunListener {

	// TODO Event-based interface instead?
	
	void testRunStarted(int testCount) throws Exception;
	
	void testRunFinished(Result result) throws Exception;
	
	void testStarted(Object test, String name) throws Exception;

	void testFinished(Object test, String name) throws Exception;

	void testFailure(Failure failure) throws Exception;

	void testIgnored(Method method) throws Exception;

}
