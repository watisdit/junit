package org.junit.runner.internal;

import org.junit.runner.Failure;

/**
 * A Test Failure holds the failed Test, method name, and the 
 * thrown exception
 */
public class TestFailure extends Failure {

	protected Object fFailedTest;
	protected String fMethodName;

	/**
	 * Constructs a TestFailure with the given test, method name, and exception.
	 */
	public TestFailure(Object failedTest, String methodName, Throwable thrownException) {
		super(thrownException);
		fFailedTest= failedTest;
		fMethodName= methodName;
	}

	@Override
	public String getTestHeader() {
		return fFailedTest.getClass().getName() + "." + fMethodName + "()";
	}
	
	public Object getFailedTest() {
		return fFailedTest;
	}
	
	public String getMethodName() {
		return fMethodName;
	}
	
	@Override
	public boolean isTestFailure() {
		return true;
	}
}
