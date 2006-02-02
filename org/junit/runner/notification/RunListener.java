package org.junit.runner.notification;

import org.junit.runner.Description;
import org.junit.runner.Result;



public interface RunListener {

	// TODO Event-based interface instead?
	
	void testRunStarted(Description description) throws Exception;
	
	void testRunFinished(Result result) throws Exception;
	
	void testStarted(Description description) throws Exception;

	void testFinished(Description description) throws Exception;

	void testFailure(Failure failure) throws Exception;

	void testIgnored(Description description) throws Exception;

}
