/**
 * 
 */
package junit.framework;

import org.junit.plan.LeafPlan;

public class JUnit4TestCaseFacade implements Test {
	private final LeafPlan fPlan;

	JUnit4TestCaseFacade(LeafPlan plan) {
		fPlan = plan;
	}

	@Override
	public String toString() {
		return getPlan().toString();
	}

	public int countTestCases() {
		return 1;
	}

	public void run(TestResult result) {
		throw new RuntimeException(
				"This test stub created only for informational purposes.");
	}

	public LeafPlan getPlan() {
		return fPlan;
	}
}