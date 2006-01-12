package junit.framework.compatibility;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;

import org.junit.runner.InitializationErrorListener;
import org.junit.runner.RunNotifier;
import org.junit.runner.Runner;
import org.junit.runner.plan.CompositePlan;
import org.junit.runner.plan.Plan;
import org.junit.runner.request.Request;

public class JUnit4TestAdapter implements Test {
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

	private TestCache fCache;

	private List<InitializationError> fInitializationErrors = new ArrayList<InitializationError>();

	public JUnit4TestAdapter(Class<? extends Object> newTestClass) {
		this(newTestClass, new TestCache());
	}

	public JUnit4TestAdapter(final Class<? extends Object> newTestClass,
			TestCache cache) {
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
		fRunner.run(fCache.getNotifier(result, this));
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