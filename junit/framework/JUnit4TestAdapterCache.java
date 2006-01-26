/**
 * 
 */
package junit.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.internal.runners.TestFailure;
import org.junit.runner.Result;
import org.junit.runner.description.Description;
import org.junit.runner.description.SuiteDescription;
import org.junit.runner.description.TestDescription;
import org.junit.runner.description.Description.Visitor;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

public class JUnit4TestAdapterCache extends HashMap<TestDescription, Test> {
	private static final long serialVersionUID = 1L;
	private static final JUnit4TestAdapterCache fInstance = new JUnit4TestAdapterCache();

	public static JUnit4TestAdapterCache getDefault() {
		return fInstance;
	}
	
	public Test asTest(Description description) {
		return description.accept(new Visitor<Test>() {
			public Test visitSuite(SuiteDescription description) {
				return createTest(description);
			}

			public Test visitTest(TestDescription description) {
				if (!containsKey(description))
					put(description, createTest(description));
				return get(description);
			}
		});
	}

	Test createTest(Description description) {
		return description.accept(new Visitor<Test>() {
			public Test visitSuite(SuiteDescription description) {
				TestSuite suite = new TestSuite(description.getName());
				for (Description child : description.getChildren()) {
					suite.addTest(asTest(child));
				}
				return suite;
			}

			public Test visitTest(final TestDescription description) {
				return new JUnit4TestCaseFacade(description);
			}
		});
	}

	public RunNotifier getNotifier(final TestResult result,
			final JUnit4TestAdapter adapter) {
		RunNotifier notifier = new RunNotifier();
		notifier.addListener(new RunListener() {
			public void testIgnored(TestDescription description) throws Exception {
			}

			public void testFailure(Failure failure) throws Exception {
				result.addError(getTest(failure), failure.getException());
			}

			private Test getTest(Failure failure) {
				if (failure instanceof TestFailure) {
					TestFailure tf = (TestFailure) failure;
					return asTest(tf.getDescription());
				} else {
					return null;
				}
			}

			public void testFinished(TestDescription description)
					throws Exception {
				result.endTest(asTest(description));
			}

			public void testStarted(TestDescription description)
					throws Exception {
				result.startTest(asTest(description));
			}

			public void testRunFinished(Result result) throws Exception {
			}

			public void testRunStarted(Description description) throws Exception {
			}
		});
		return notifier;
	}

	public List<Test> asTestList(Description description) {
		return description.accept(new Visitor<List<Test>>() {
			public List<Test> visitSuite(SuiteDescription description) {
				List<Test> returnThis = new ArrayList<Test>();
				for (Description child : description.getChildren()) {
					returnThis.add(asTest(child));
				}
				return returnThis;
			}

			public List<Test> visitTest(TestDescription description) {
				return Arrays.asList(asTest(description));
			}
		});
	}

}