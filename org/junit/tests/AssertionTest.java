package org.junit.tests;

import static org.junit.Assert.fail;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

public class AssertionTest {
// If you want to use 1.4 assertions, they will be reported correctly.
// However, you need to add the -ea VM argument when running.
	
//	@Test @Expected(AssertionError.class) public void error() {
//		assert false;
//	}
	
	@Test(expected= AssertionError.class) public void fails() {
		fail();
	}
	
	static public junit.framework.Test suite() {
		return new JUnit4TestAdapter(AssertionTest.class);
	}
}
