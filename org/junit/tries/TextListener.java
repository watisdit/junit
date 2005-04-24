package org.junit.tries;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.NumberFormat;

import junit.runner.BaseTestRunner;

import org.junit.Test;
import org.junit.TestListener;

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
	public void testStarted(Object test, String name) {
		fWriter.append('.');
	}

	public void testRunFinished(Runner result, long runTime) {
		printHeader(runTime);
		printFailures(result);
		printFooter(result);
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
		for (Throwable each : result.getFailures())
			printDefect(each, i++);
	}

	protected void printDefect(Throwable booBoo, int count) {
		printDefectHeader(booBoo, count);
		printDefectTrace(booBoo);
	}

	protected void printDefectHeader(Throwable booBoo, int count) {
		// I feel like making this a println, then adding a line giving the
		// throwable a chance to print something
		// before we get to the stack trace.
		getWriter().print(count + ") " + "???"); // TODO: We need real failures
	}

	protected void printDefectTrace(Throwable booBoo) {
		getWriter().print(BaseTestRunner.getFilteredTrace(trace(booBoo)));
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

	private String trace(Throwable exception) {
		StringWriter stringWriter= new StringWriter();
		PrintWriter writer= new PrintWriter(stringWriter);
		exception.printStackTrace(writer);
		StringBuffer buffer= stringWriter.getBuffer();
		return buffer.toString();
	}

	public void failure(Throwable exception) {
		fWriter.append('E');
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
