package org.junit.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.JUnit4TestAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.global.RecordingServer;
import org.junit.runner.JUnitCore;

public class CommandLineTest {
	private ByteArrayOutputStream results;
	private PrintStream oldOut;
	private static boolean testWasRun;

	@Before public void before() {
		oldOut= System.out;
		results= new ByteArrayOutputStream();
		System.setOut(new PrintStream(results));
	}

	@After public void after() {
		System.setOut(oldOut);
	}

	static public class Example {
		@Test public void test() { 
			testWasRun= true; 
		}
	}

	@Test public void runATest() {
		testWasRun= false;
		JUnitCore.main(new String[]{"org.junit.tests.CommandLineTest$Example"});
		assertTrue(testWasRun);
	}

	String fEmail= "";
	@Test public void record() {
		RecordingServer recorder= new RecordingServer() {
			@Override
			protected void addUser(String email) {
				fEmail= email;
			}
		};
		recorder.start();
		try {
			JUnitCore.main(new String[]{"-host", "localhost", "-email", "somebody@somewhere.com", "org.junit.tests.CommandLineTest$Example"});
		} finally {
			recorder.stop();
		}
		assertEquals("somebody@somewhere.com", fEmail);
	}

	static public junit.framework.Test suite() {
		return new JUnit4TestAdapter(CommandLineTest.class);
	}

}
