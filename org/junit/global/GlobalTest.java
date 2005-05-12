package org.junit.global;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Runner;
import org.junit.runner.TestListener;

public class GlobalTest {
	private Recorder recorder;
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(GlobalTest.class);
	}
	
	@Before public void start() {
		recorder= new Recorder();
		recorder.start();
	}
	
	@After public void stop() {
		recorder.stop();
	}
	
	public static class Example {
		@Test public void test() {
		}
	}
	 
	@Test public void transaction() {
		String output= runTests("kent@threeriversinstitute.org");
		assertEquals("JUnit User #0, kent@threeriversinstitute.org\r\nThis run: 1 test run, 0 failed\r\nToday: 1 test run, 0 failed\r\nTotal: 1 test run, 0 failed\r\n", output);
	}
	
	@Test public void twoTransactions() {
		runTests("kent@threeriversinstitute.org");
		String output= runTests("kent@threeriversinstitute.org");
		assertEquals("JUnit User #0, kent@threeriversinstitute.org\r\nThis run: 1 test run, 0 failed\r\nToday: 2 test run, 0 failed\r\nTotal: 2 test run, 0 failed\r\n", output);
	}

	@Test public void twoUsers() {
		runTests("kent@threeriversinstitute.org");
		String output= runTests("cindee@threeriversinstitute.org");
		assertEquals("JUnit User #1, cindee@threeriversinstitute.org\r\nThis run: 1 test run, 0 failed\r\nToday: 2 test run, 0 failed\r\nTotal: 2 test run, 0 failed\r\n", output);
	}
	
	private String runTests(String email) {
		ByteArrayOutputStream results= new ByteArrayOutputStream();
		PrintStream writer= new PrintStream(results);
		TestListener listener= new RecordingListener(email, writer);
		Runner first= new Runner();
		first.addListener(listener);
		first.run(Example.class);
		return results.toString();
	}
	
}

//TODO: HTTP instead of plain sockets
//TODO: Report page
//TODO: Registering email addresses
//TODO: Report page for registered users
//TODO: Eclipse plug-in
//TODO: Add JUnit version number to records
//TODO: Logging
//TODO: Recover log on startup
//TODO: Switch-over when starting a new version of the server
