package org.junit.tests;

import org.junit.Test;
import org.junit.runner.description.TestDescription;
import static org.junit.Assert.assertFalse;

public class TestDescriptionTest {
	@Test public void equalsIsFalseForNonTestDescription() {
		assertFalse(new TestDescription(getClass(), "a").equals(new Integer(5)));
	}
}
