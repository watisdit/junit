package org.junit.runner;

import org.junit.runner.plan.LeafPlan;
import org.junit.runner.plan.Plan;



public interface RunListener {

	// TODO Event-based interface instead?
	
	void testRunStarted(Plan plan) throws Exception;
	
	void testRunFinished(Result result) throws Exception;
	
	void testStarted(LeafPlan plan) throws Exception;

	void testFinished(LeafPlan plan) throws Exception;

	void testFailure(Failure failure) throws Exception;

	void testIgnored(LeafPlan plan) throws Exception;

}
