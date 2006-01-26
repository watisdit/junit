/**
 * 
 */
package junit.framework;

import org.junit.runner.description.TestDescription;

public class JUnit4TestCaseFacade implements Test {
	private final TestDescription fDescription;

	JUnit4TestCaseFacade(TestDescription description) {
		fDescription = description;
	}

	@Override
	public String toString() {
		return getDescription().toString();
	}

	public int countTestCases() {
		return 1;
	}

	public void run(TestResult result) {
		throw new RuntimeException(
				"This test stub created only for informational purposes.");
	}

	public TestDescription getDescription() {
		return fDescription;
	}
}