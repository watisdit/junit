package org.junit.tests;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.JUnit4TestAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.global.Recorder;
import org.junit.runner.JUnitCore;

public class CommandLineTest {
	private ByteArrayOutputStream results;
	private PrintStream oldOut;

	@Before public void before() {
		oldOut= System.out;
		results= new ByteArrayOutputStream();
		System.setOut(new PrintStream(results));
	}

	@After public void after() {
		System.setOut(oldOut);
	}

	static public class Example {
		@Test public void test() {}
	}

	@Test public void runATest() {
		JUnitCore.main(new String[]{"org.junit.tests.CommandLineTest$Example"});
	}

	String fEmail= "";
	@Test public void record() {
		Recorder recorder= new Recorder() {
			@Override
			protected void addUser(String email) {
				fEmail= email;
			}
		};
		recorder.start();
		JUnitCore.main(new String[]{"-email", "somebody@somewhere.com", "-host", "localhost", "org.junit.tests.CommandLineTest$Example"});
		recorder.stop();
		assertEquals("somebody@somewhere.com", fEmail);
	}

	static public junit.framework.Test suite() {
		return new JUnit4TestAdapter(CommandLineTest.class);
	}

}
