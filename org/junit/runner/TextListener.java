package org.junit.runner;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.NumberFormat;

import org.junit.Test;

public class TextListener implements TestListener {

	private final PrintStream fWriter;

	static public void run(Class testClass) {
		TextListener listener= new TextListener();
		Runner runner= new Runner();
		runner.addListener(listener);
		try {
			runner.run(testClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TextListener() {
		this(System.out);
	}

	public TextListener(PrintStream writer) {
		this.fWriter= writer;
	}

	// TestListener implementation
	public void testRunStarted() {
	}

	public void testRunFinished(Runner result, long runTime) {
		printHeader(runTime);
		printFailures(result);
		printFooter(result);
	}

	public void testStarted(Object test, String name) {
		fWriter.append('.');
	}

	public void testFailure(Failure failure) {
		fWriter.append('E');
	}
	
	/*
	 * Internal methods
	 */

	private PrintStream getWriter() {
		return fWriter;
	}

	protected void printHeader(long runTime) {
		getWriter().println();
		getWriter().println("Time: " + elapsedTimeAsString(runTime));
	}

	protected void printFailures(Runner result) {
		if (result.getFailureCount() == 0)
			return;
		if (result.getFailureCount() == 1)
			getWriter().println("There was " + result.getFailureCount() + " failure:");
		else
			getWriter().println("There were " + result.getFailureCount() + " failures:");
		int i= 1;
		for (Failure each : result.getFailures())
			printFailure(each, i++);
	}

	protected void printFailure(Failure failure, int count) {
		printFailureHeader(failure, count);
		printFailureTrace(failure);
	}

	protected void printFailureHeader(Failure failure, int count) {
		getWriter().println(count + ") " + failure.getTestHeader());
	}

	protected void printFailureTrace(Failure failure) {
		getWriter().print(failure.getTrace());
	}

	protected void printFooter(Runner result) {
		if (result.wasSuccessful()) {
			getWriter().println();
			getWriter().print("OK");
			getWriter().println(" (" + result.getRunCount() + " test" + (result.getRunCount() == 1 ? "" : "s") + ")");

		} else {
			getWriter().println();
			getWriter().println("FAILURES!!!");
			getWriter().println("Tests run: " + result.getRunCount() + ",  Failures: " + result.getFailureCount());
		}
		getWriter().println();
	}

	/**
	 * Returns the formatted string of the elapsed time. Duplicated from
	 * BaseTestRunner. Fix it.
	 */
	protected String elapsedTimeAsString(long runTime) {
		return NumberFormat.getInstance().format((double) runTime / 1000);
	}

	private String trace(Throwable exception) { //QUESTION test runner never shows the stacktrace?
		StringWriter stringWriter= new StringWriter();
		PrintWriter writer= new PrintWriter(stringWriter);
		exception.printStackTrace(writer);
		StringBuffer buffer= stringWriter.getBuffer();
		return buffer.toString();
	}

	// Temporary usage example
	static public class TwoTests {
		@Test public void succeed() {}
		@Test public void not() throws Exception {
			throw new Exception("If at first you don't...");
		}
	}
	
	public static void main(String[] args) throws Exception {
		run(TwoTests.class);
	}

}
