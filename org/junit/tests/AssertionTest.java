package org.junit.tests;

import static org.junit.Assert.assertEquals;
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
	
	@Test(expected= AssertionError.class) public void arraysNotEqual() {
		assertEquals(new Object[] {new Object()}, new Object[] {new Object()});
	}
	
	@Test(expected= AssertionError.class) public void arraysNotEqualWithMessage() {
		assertEquals("not equal", new Object[] {new Object()}, new Object[] {new Object()});
	}
	
	@Test public void arraysNotEqualErrorMessage() {
		try {
			assertEquals("not equal", new Object[] {new Object()}, new Object[] {new Object()});
		} catch (AssertionError exception) {
			assertEquals("not equal: arrays first differed at element 0", exception.getMessage());
		}
	}
	
	@Test public void arraysExpectedNullMessage() {
		try {
			assertEquals("not equal", null, new Object[] {new Object()});
		} catch (AssertionError exception) {
			assertEquals("not equal: expected array was null", exception.getMessage());
		}
	}
	
	@Test public void arraysActualNullMessage() {
		try {
			assertEquals("not equal", new Object[] {new Object()}, null);
		} catch (AssertionError exception) {
			assertEquals("not equal: actual array was null", exception.getMessage());
		}
	}
	
	@Test public void arraysDifferentLengthMessage() {
		try {
			assertEquals("not equal", new Object[0] , new Object[1]);
		} catch (AssertionError exception) {
			assertEquals("not equal: array lengths differed, expected.length=0 actual.length=1", exception.getMessage());
		}
	}
	
	@Test public void arraysEqual() {
		Object element= new Object();
		Object[] objects1= new Object[] {element};
		Object[] objects2= new Object[] {element};
		assertEquals(objects1, objects2);
	}
	
	@Test public void arraysEqualWithMessage() {
		Object element= new Object();
		Object[] objects1= new Object[] {element};
		Object[] objects2= new Object[] {element};
		assertEquals("equal", objects1, objects2);
	}
	
	static public junit.framework.Test suite() {
		return new JUnit4TestAdapter(AssertionTest.class);
	}
}
