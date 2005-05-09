package org.junit.runner;

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
}
