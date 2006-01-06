package org.junit.tests;
import junit.framework.Test;
import org.junit.runner.RunWith;
import org.junit.runner.extensions.AllTests;

@RunWith(AllTests.class)
public class OldTests {
	static public Test suite() {
		return junit.tests.AllTests.suite();
	}
}
