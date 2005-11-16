package org.junit.tests;

import static org.junit.Assert.assertEquals;
import junit.framework.ComparisonFailure;
import junit.framework.JUnit4TestAdapter;
import org.junit.Assert;
import org.junit.Test;

public class AssertionTest {
// If you want to use 1.4 assertions, they will be reported correctly.
// However, you need to add the -ea VM argument when running.
	
//	@Test @Expected(AssertionError.class) public void error() {
//		assert false;
//	}
	
	@Test(expected= AssertionError.class) public void fails() {
		Assert.fail();
	}
	
	@Test(expected= AssertionError.class) public void arraysNotEqual() {
		assertEquals(new Object[] {new Object()}, new Object[] {new Object()});
	}
	
	@Test(expected= AssertionError.class) public void arraysNotEqualWithMessage() {
		assertEquals("not equal", new Object[] {new Object()}, new Object[] {new Object()});
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
	
	@Test public void arraysElementsDiffer() {
		try {
			assertEquals("not equal", new Object[] {"one"} , new Object[] {"two"});
		} catch (AssertionError exception) {
			assertEquals("not equal: arrays first differed at element 0 expected:<one> but was:<two>", exception.getMessage());
		}
	}
	
	@Test public void stringsDifferWithUserMessage() {
		try {
			assertEquals("not equal", "one", "two");
		} catch (Throwable exception) {
			assertEquals("not equal expected:<[one]> but was:<[two]>", exception.getMessage());
		}
	}
	
	//TODO: Should we compact the comparison of Objects and not just Strings? In any case, we should compact the comparison of elements which are Strings
	
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
	
	@Test public void equals() {
		Object o= new Object();
		assertEquals(o, o);
		assertEquals("abc", "abc");
		assertEquals(true, true);
		assertEquals((byte) 1, (byte) 1);
		assertEquals('a', 'a');
		assertEquals((short) 1, (short) 1);
		assertEquals(1, 1); // int by default, cast is unnecessary
		assertEquals(1l, 1l);
		assertEquals(1.0, 1.0, 0.0);
		assertEquals(1.0d, 1.0d, 0.0d);
	}
	
	@Test(expected= AssertionError.class) public void objectsNotEquals() {
		assertEquals(new Object(), new Object());
	}
	
	//TODO should expected pass if you get a subclass of the expected exception?
	//TODO does ComparisonFailure need to be API and/or migrated to org.junit?
	@Test(expected= ComparisonFailure.class) public void stringsNotEqual() {
		assertEquals("abc", "def");
	}
	
	@Test(expected= AssertionError.class) public void booleansNotEqual() {
		assertEquals(true, false);
	}
	
	@Test(expected= AssertionError.class) public void bytesNotEqual() {
		assertEquals((byte) 1, (byte) 2);
	}
	
	@Test(expected= AssertionError.class) public void charsNotEqual() {
		assertEquals('a', 'b');
	}
	
	@Test(expected= AssertionError.class) public void shortsNotEqual() {
		assertEquals((short) 1, (short) 2);
	}
	
	@Test(expected= AssertionError.class) public void intsNotEqual() {
		assertEquals(1, 2);
	}
	
	@Test(expected= AssertionError.class) public void longsNotEqual() {
		assertEquals(1l, 2l);
	}
	
	@Test(expected= AssertionError.class) public void floatsNotEqual() {
		assertEquals(1.0, 2.0, 0.9);
	}
	
	@Test(expected= AssertionError.class) public void doublesNotEqual() {
		assertEquals(1.0d, 2.0d, 0.9d);
	}
	
	@Test public void naNsAreEqual() {
		assertEquals(Float.NaN, Float.NaN, Float.POSITIVE_INFINITY);
		assertEquals(Double.NaN, Double.NaN, Double.POSITIVE_INFINITY);
	}
	
	//TODO tests for the other flavors of assertions
	
	static public junit.framework.Test suite() {
		return new JUnit4TestAdapter(AssertionTest.class);
	}
}
