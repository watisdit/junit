package org.junit.notify;

import org.junit.plan.LeafPlan;
import org.junit.plan.Plan;
import org.junit.runner.Result;



public interface RunListener {

	// TODO Event-based interface instead?
	
	void testRunStarted(Plan plan) throws Exception;
	
	void testRunFinished(Result result) throws Exception;
	
	void testStarted(LeafPlan plan) throws Exception;

	void testFinished(LeafPlan plan) throws Exception;

	void testFailure(Failure failure) throws Exception;

	void testIgnored(LeafPlan plan) throws Exception;

}
