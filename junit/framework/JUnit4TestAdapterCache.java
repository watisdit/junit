/**
 * 
 */
package junit.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.internal.runners.TestFailure;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

public class JUnit4TestAdapterCache extends HashMap<Description, Test> {
	private static final long serialVersionUID = 1L;
	private static final JUnit4TestAdapterCache fInstance = new JUnit4TestAdapterCache();

	public static JUnit4TestAdapterCache getDefault() {
		return fInstance;
	}
	
	public Test asTest(Description description) {
		if (description.hasChildren())
			return createTest(description);
		else {
			if (!containsKey(description))
				put(description, createTest(description));
			return get(description);
		}
	}

	Test createTest(Description description) {
		if (!description.hasChildren())
			return new JUnit4TestCaseFacade(description);
		else {
			TestSuite suite = new TestSuite(description.getDisplayName());
			for (Description child : description.getChildren())
				suite.addTest(asTest(child));
			return suite;
		}
	}

	public RunNotifier getNotifier(final TestResult result,
			final JUnit4TestAdapter adapter) {
		RunNotifier notifier = new RunNotifier();
		notifier.addListener(new RunListener() {
			public void testIgnored(Description description) throws Exception {
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

			public void testFinished(Description description)
					throws Exception {
				result.endTest(asTest(description));
			}

			public void testStarted(Description description)
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
		if (!description.hasChildren())
			return Arrays.asList(asTest(description));
		else {
			List<Test> returnThis = new ArrayList<Test>();
			for (Description child : description.getChildren()) {
				returnThis.add(asTest(child));
			}
			return returnThis;
		}
	}

}