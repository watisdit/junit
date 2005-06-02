package org.junit.tests;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.internal.runner.TextListener;
import org.junit.runner.Runner;

public class TextListenerTest extends TestCase {
	
	private Runner runner;
	private OutputStream results;
	private TextListener listener;

	@Override
	public void setUp() {
		runner= new Runner();
		results= new ByteArrayOutputStream();
		PrintStream writer= new PrintStream(results);
		listener= new TextListener(writer);
		runner.addListener(listener);
	}
	
	public static class OneTest {
		@Test public void one() {}
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
		assertTrue(results.toString().indexOf(convert("\nThere was 1 failure:\n1) org.junit.tests.TextListenerTest$ErrorTest.error()\njava.lang.Exception")) != -1);
	}
	
	private String convert(String string) {
		OutputStream resultsStream= new ByteArrayOutputStream();
		PrintStream writer= new PrintStream(resultsStream);
		writer.println();
		return string.replace("\n", resultsStream.toString());
	}
	
}
