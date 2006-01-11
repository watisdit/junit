package junit.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.runner.Failure;
import org.junit.runner.InitializationErrorListener;
import org.junit.runner.Result;
import org.junit.runner.RunListener;
import org.junit.runner.RunNotifier;
import org.junit.runner.Runner;
import org.junit.runner.StoppedByUserException;
import org.junit.runner.internal.TestFailure;
import org.junit.runner.plan.CompositePlan;
import org.junit.runner.plan.LeafPlan;
import org.junit.runner.plan.Plan;
import org.junit.runner.plan.Plan.Visitor;
import org.junit.runner.request.Request;

public class JUnit4TestAdapter implements Test {
	// cache because runners (like Eclipse) expect to see the same Test object
	// meaning the same test every time.
	public static class PlanCache extends HashMap<LeafPlan, Test> {
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

		// TODO: plan passes ignored methods, but nothing happens with them

		private Test createTest(Plan plan) {
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

		RunNotifier getNotifier(final TestResult result,
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

		List<Test> asTestList(Plan plan) {
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

	public static class InitializationError extends TestCase {
		private final Throwable fThrowable;

		public InitializationError(Throwable t) {
			super("Initialization Error");
			fThrowable = t;
		}

		public Throwable getThrowable() {
			return fThrowable;
		}

		void addError(TestResult result) {
			result.addError(this, getThrowable());
		}
	}
	
	private final Class<? extends Object> fNewTestClass;

	private Runner fRunner;

	private PlanCache fCache;

	private List<InitializationError> fInitializationErrors = new ArrayList<InitializationError>();

	public JUnit4TestAdapter(Class<? extends Object> newTestClass) {
		this(newTestClass, new PlanCache());
	}

	public JUnit4TestAdapter(final Class<? extends Object> newTestClass,
			PlanCache cache) {
		fCache = cache;
		fNewTestClass = newTestClass;
		fRunner = Request.aClass(newTestClass).getRunner(failureListener());
		if (fRunner == null)
			fRunner = new Runner() {
				@Override
				public void run(RunNotifier notifier) {
					// do nothing
				}

				@Override
				public Plan getPlan() {
					return new CompositePlan("Could not initialize "
							+ newTestClass.getName());
				}
			};
	}

	public int countTestCases() {
		return fRunner.testCount();
	}

	public void run(TestResult result) {
		for (InitializationError e : fInitializationErrors) {
			e.addError(result);
		}
		try {
			fRunner.run(fCache.getNotifier(result, this));
		} catch (StoppedByUserException e1) {
			result.stop();
		}
	}

	private InitializationErrorListener failureListener() {
		return new InitializationErrorListener() {
			public void initializationError(Throwable e) {
				fInitializationErrors.add(new InitializationError(e));
			}
		};
	}

	// reflective interface for Eclipse
	public List<Test> getTests() {
		List<Test> asTestList = fCache.asTestList(fRunner.getPlan());
		asTestList.addAll(0, fInitializationErrors);
		return asTestList;
	}

	// reflective interface for Eclipse
	public Class getTestClass() {
		return fNewTestClass;
	}

	@Override
	public String toString() {
		return fNewTestClass.getName();
	}
}