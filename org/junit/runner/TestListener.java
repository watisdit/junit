package org.junit.runner;

import java.lang.reflect.Method;


public interface TestListener {

	void testRunStarted(int testCount) throws Exception;
	
	void testRunFinished(Runner runner) throws Exception; //TODO: Something is wrong here... Should take an event anyway
	
	void testStarted(Object test, String name) throws Exception;

	void testFinished(Object test, String name) throws Exception;

	void testFailure(Failure failure) throws Exception;

	void testIgnored(Method method) throws Exception;

}
