package junit.samples;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A sample test case, testing <code>java.util.Vector</code>.
 *
 */
public class ListTest extends TestCase {
	protected List<Integer> fEmpty;
	protected List<Integer> fFull;

	public static void main (String[] args) {
		junit.textui.TestRunner.run (suite());
	}
	protected void setUp() {
		fEmpty= new ArrayList<Integer>();
		fFull= new ArrayList<Integer>();
		fFull.add(new Integer(1));
		fFull.add(new Integer(2));
		fFull.add(new Integer(3));
	}
	public static Test suite() {
		return new TestSuite(ListTest.class);
	}
	public void testCapacity() {
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
	public void testContains() {
		assertTrue(fFull.contains(new Integer(1)));  
		assertTrue(!fEmpty.contains(new Integer(1)));
	}
	public void testElementAt() {
		Integer i= (Integer)fFull.get(0);
		assertTrue(i.intValue() == 1);

		try { 
			fFull.get(fFull.size());
		} catch (ArrayIndexOutOfBoundsException e) {
			return;
		}
		fail("Should raise an ArrayIndexOutOfBoundsException");
	}
	public void testRemoveAll() {
		fFull.removeAll(fFull);
		fEmpty.removeAll(fEmpty);
		assertTrue(fFull.isEmpty());
		assertTrue(fEmpty.isEmpty()); 
	}
	public void testRemoveElement() {
		fFull.remove(new Integer(3));
		assertTrue(!fFull.contains(new Integer(3)) ); 
	}
}