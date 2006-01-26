package org.junit.runner.notification;

import org.junit.runner.Result;
import org.junit.runner.description.Description;
import org.junit.runner.description.TestDescription;



public interface RunListener {

	// TODO Event-based interface instead?
	
	void testRunStarted(Description description) throws Exception;
	
	void testRunFinished(Result result) throws Exception;
	
	void testStarted(TestDescription description) throws Exception;

	void testFinished(TestDescription description) throws Exception;

	void testFailure(Failure failure) throws Exception;

	void testIgnored(TestDescription description) throws Exception;

}
