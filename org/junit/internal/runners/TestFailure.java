package org.junit.internal.runners;

import org.junit.runner.description.TestDescription;
import org.junit.runner.notification.Failure;

/**
 * A Test Failure holds the failed Test, method name, and the 
 * thrown exception
 */
public class TestFailure extends Failure {
	private final TestDescription fDescription;

	/**
	 * Constructs a TestFailure with the given plan and exception.
	 */
	public TestFailure(TestDescription description, Throwable thrownException) {
		super(thrownException);
		fDescription= description;
	}

	@Override
	public String getTestHeader() {
		return String.format("%s.%s()", fDescription.getTestClass().getName(), fDescription.getName());
	}
	
	@Override
	public boolean isTestFailure() {
		return true;
	}

	public TestDescription getDescription() {
		return fDescription;
	}
}
