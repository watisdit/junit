package org.junit.runner.internal;

import org.junit.runner.Failure;
import org.junit.runner.plan.LeafPlan;

/**
 * A Test Failure holds the failed Test, method name, and the 
 * thrown exception
 */
public class TestFailure extends Failure {
	private final LeafPlan fPlan;

	/**
	 * Constructs a TestFailure with the given plan and exception.
	 */
	public TestFailure(LeafPlan plan, Throwable thrownException) {
		super(thrownException);
		fPlan = plan;
	}

	@Override
	public String getTestHeader() {
		return fPlan.getTestClass().getName() + "." + fPlan.getName() + "()";
	}
	
	@Override
	public boolean isTestFailure() {
		return true;
	}

	public LeafPlan getPlan() {
		return fPlan;
	}
}
