package org.junit.internal.runners;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

/**
 * A Test Failure holds the failed Test, method name, and the 
 * thrown exception
 */
public class TestFailure extends Failure {
	private final Description fDescription;

	/**
	 * Constructs a TestFailure with the given plan and exception.
	 */
	public TestFailure(Description description, Throwable thrownException) {
		super(thrownException);
		fDescription= description;
	}

	@Override
	public String getTestHeader() {
		return fDescription.getDisplayName();
	}
	
	@Override
	public boolean isTestFailure() {
		return true;
	}

	public Description getDescription() {
		return fDescription;
	}
}
