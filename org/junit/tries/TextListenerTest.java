package org.junit.tries;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

import org.junit.Test;

public class TextListenerTest extends TestCase {
	
	private Runner runner;
	private OutputStream results;
	private TextListener listener;

	public void setUp() {
		runner= new Runner();
		results= new ByteArrayOutputStream();
		PrintStream writer= new PrintStream(results);
		listener= new TextListener(writer);
		runner.addListener(listener);
	}
	
	public static class OneTest {
		@Test public void one() {};
	}
	
	public void testSuccess() throws Exception {
		runner.run(OneTest.class);
		assertTrue(results.toString().startsWith(convert(".\nTime: ")));
		assertTrue(results.toString().endsWith(convert("\n\nOK (1 test)\n\n")));
	}
	
	public static class ErrorTest {
		@Test public void error() throws Exception {throw new Exception();}
	}
	
	public void testError() throws Exception {
		runner.run(ErrorTest.class);
		assertTrue(results.toString().startsWith(convert(".E\nTime: ")));
		assertTrue(results.toString().indexOf(convert("\nThere was 1 failure:\n1)")) != -1); //TODO: Append the test case name to force implementation of TestFailure
	}
	
	private String convert(String string) {
		OutputStream results= new ByteArrayOutputStream();
		PrintStream writer= new PrintStream(results);
		writer.println();
		return string.replace("\n", results.toString());
	}
	
}
