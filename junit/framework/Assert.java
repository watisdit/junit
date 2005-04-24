package junit.framework;

/**
 * A set of assert methods. Messages are only displayed when an assert fails.
 */

public class Assert extends org.junit.Assert {
	/**
	 * Protect constructor since it is a static only class
	 */
	protected Assert() {
	}
	
	/**
	 * Fails a test with the given message.
	 */
	static public void fail(String message) {
		throw new AssertionFailedError(message);
	}

	/**
	 * Asserts that two Strings are equal.
	 */
	static public void assertEquals(String message, String expected, String actual) {
		if (expected == null && actual == null)
			return;
		if (expected != null && expected.equals(actual))
			return;
		throw new ComparisonFailure(message, expected, actual);
	}

}
