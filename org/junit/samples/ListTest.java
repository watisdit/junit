package org.junit.samples;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Expected;
import org.junit.Test;

/**
 * A sample test case, testing <code>java.util.Vector</code>.
 *
 */
public class ListTest {
	protected List<Integer> fEmpty;
	protected List<Integer> fFull;
	protected static List<Integer> fgHeavy;

	public static void main (String... args) {
		junit.textui.TestRunner.run (suite());
	}
	
	@BeforeClass public static void setUpOnce() {
		fgHeavy= new ArrayList<Integer>();
		for(int i= 0; i < 1000; i++)
			fgHeavy.add(new Integer(i));
	}
	
	@Before public void setUp() {
		fEmpty= new ArrayList<Integer>();
		fFull= new ArrayList<Integer>();
		fFull.add(new Integer(1));
		fFull.add(new Integer(2));
		fFull.add(new Integer(3));
	}
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(ListTest.class);
	}
	@Test public void capacity() {
		int size= fFull.size(); 
		for (int i= 0; i < 100; i++)
			fFull.add(new Integer(i));
		assertTrue(fFull.size() == 100+size);
	}
//TODO: fix this	public void testClone() {
//		List<Integer> clone= (List<Integer>)fFull.clone(); 
//		assertTrue(clone.size() == fFull.size());
//		assertTrue(clone.contains(new Integer(1)));
//	}
	@Test public void contains() {
		assertTrue(fFull.contains(new Integer(1)));  
		assertTrue(!fEmpty.contains(new Integer(1)));
	}
	@Test @Expected(IndexOutOfBoundsException.class) public void elementAt() {
		Integer i= fFull.get(0);
		assertTrue(i.intValue() == 1);
		fFull.get(fFull.size());
	}
	
	@Test public void removeAll() {
		fFull.removeAll(fFull);
		fEmpty.removeAll(fEmpty);
		assertTrue(fFull.isEmpty());
		assertTrue(fEmpty.isEmpty()); 
	}
	@Test public void removeElement() {
		fFull.remove(new Integer(3));
		assertTrue(!fFull.contains(new Integer(3)) ); 
	}
}