package org.junit.runner;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestResult;


/**
 * A <code>TestFailure</code> collects a failed test together with
 * the caught exception.
 * @see TestResult
 */
public class Failure extends Object {
	protected Object fFailedTest;
	protected String fMethodName;
	protected Throwable fThrownException;

	/**
	 * Constructs a TestFailure with the given test, method name, and exception.
	 */
	public Failure(Object failedTest, String methodName, Throwable thrownException) {
		fFailedTest= failedTest;
		fMethodName= methodName;
		fThrownException= thrownException;
	}
	
	/**
	 * Constructs a TestFailure with only the given exception.
	 */
	public Failure(Throwable thrownException) {
		fThrownException= thrownException;
	}
	/**
	 * Gets the thrown exception.
	 */
	public Throwable getException() {
	    return fThrownException;
	}
	/**
	 * Returns a short description of the failure.
	 */
	@Override
	public String toString() {
	    StringBuffer buffer= new StringBuffer();
	    buffer.append(getTestHeader() + ": "+fThrownException.getMessage());
	    return buffer.toString();
	}
	
	public String getTrace() {
		StringWriter stringWriter= new StringWriter();
		PrintWriter writer= new PrintWriter(stringWriter);
		getException().printStackTrace(writer);
		StringBuffer buffer= stringWriter.getBuffer();
		return buffer.toString();
	}
	
	public String getMessage() {
		return getException().getMessage();
	}
	
	public String getTestHeader() {
		return (isTestFailure())
			? fFailedTest.getClass().getName() + "." + fMethodName + "()"
			: "Test mechanism failure";
	}

	private boolean isTestFailure() {
		return fFailedTest != null;
	}
}
