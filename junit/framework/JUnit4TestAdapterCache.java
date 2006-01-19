/**
 * 
 */
package junit.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.notify.Failure;
import org.junit.notify.RunListener;
import org.junit.notify.RunNotifier;
import org.junit.plan.CompositePlan;
import org.junit.plan.LeafPlan;
import org.junit.plan.Plan;
import org.junit.plan.Plan.Visitor;
import org.junit.runner.Result;
import org.junit.runner.internal.TestFailure;

public class JUnit4TestAdapterCache extends HashMap<LeafPlan, Test> {
	private static final long serialVersionUID = 1L;

	public Test asTest(Plan plan) {
		return plan.accept(new Visitor<Test>() {
			public Test visitComposite(CompositePlan plan) {
				return createTest(plan);
			}

			public Test visitLeaf(LeafPlan plan) {
				if (!containsKey(plan))
					put(plan, createTest(plan));
				return get(plan);
			}
		});
	}

	Test createTest(Plan plan) {
		return plan.accept(new Visitor<Test>() {
			public Test visitComposite(CompositePlan plan) {
				TestSuite suite = new TestSuite(plan.getName());
				for (Plan child : plan.getChildren()) {
					suite.addTest(asTest(child));
				}
				return suite;
			}

			public Test visitLeaf(final LeafPlan plan) {
				return new Test() {
					@Override
					public String toString() {
						return String.format("%s(%s)", plan.getName(), plan
								.getTestClass().getName());
					}

					public int countTestCases() {
						return 1;
					}

					public void run(TestResult result) {
						throw new RuntimeException(
								"This test stub created only for informational purposes.");
					}
				};
			}
		});
	}

	public RunNotifier getNotifier(final TestResult result,
			final JUnit4TestAdapter adapter) {
		RunNotifier notifier = new RunNotifier();
		notifier.addListener(new RunListener() {
			public void testIgnored(LeafPlan plan) throws Exception {
			}

			public void testFailure(Failure failure) throws Exception {
				result.addError(getTest(failure), failure.getException());
			}

			private Test getTest(Failure failure) {
				if (failure instanceof TestFailure) {
					TestFailure tf = (TestFailure) failure;
					return asTest(tf.getPlan());
				} else {
					return null;
				}
			}

			public void testFinished(LeafPlan plan)
					throws Exception {
				result.endTest(asTest(plan));
			}

			public void testStarted(LeafPlan plan)
					throws Exception {
				result.startTest(asTest(plan));
			}

			public void testRunFinished(Result result) throws Exception {
			}

			public void testRunStarted(Plan plan) throws Exception {
			}
		});
		return notifier;
	}

	public List<Test> asTestList(Plan plan) {
		return plan.accept(new Visitor<List<Test>>() {
			public List<Test> visitComposite(CompositePlan plan) {
				List<Test> returnThis = new ArrayList<Test>();
				for (Plan child : plan.getChildren()) {
					returnThis.add(asTest(child));
				}
				return returnThis;
			}

			public List<Test> visitLeaf(LeafPlan plan) {
				return Arrays.asList(asTest(plan));
			}
		});
	}

}