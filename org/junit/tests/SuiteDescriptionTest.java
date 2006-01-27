package org.junit.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.junit.runner.description.SuiteDescription;
import org.junit.runner.description.TestDescription;

public class SuiteDescriptionTest {
	SuiteDescription childless = new SuiteDescription("a");
	SuiteDescription anotherChildless = new SuiteDescription("a");
	SuiteDescription namedB = new SuiteDescription("b");
	
	SuiteDescription twoKids = descriptionWithTwoKids("foo", "bar");
	SuiteDescription anotherTwoKids = descriptionWithTwoKids("foo", "baz");

	@Test public void equalsIsCorrect() {	
		assertEquals(childless, anotherChildless);
		assertFalse(childless.equals(namedB));
		assertFalse(childless.equals(twoKids));
		assertFalse(twoKids.equals(anotherTwoKids));
		assertFalse(twoKids.equals(new Integer(5)));
	}

	@Test public void hashCodeIsReasonable() {
		assertEquals(childless.hashCode(), anotherChildless.hashCode());
		assertFalse(childless.hashCode() == namedB.hashCode());
	}
	
	private SuiteDescription descriptionWithTwoKids(String first, String second) {
		SuiteDescription twoKids = new SuiteDescription("a");
		twoKids.addChild(new TestDescription(getClass(), first));
		twoKids.addChild(new TestDescription(getClass(), second));
		return twoKids;
	}
}
