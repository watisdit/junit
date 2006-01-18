package junit.framework;

import java.util.List;

import org.junit.runner.Request;
import org.junit.runner.Runner;

public class JUnit4TestAdapter implements Test {
	private final Class<? extends Object> fNewTestClass;

	private Runner fRunner;

	private JUnit4TestAdapterCache fCache;

	public JUnit4TestAdapter(Class<? extends Object> newTestClass) {
		this(newTestClass, new JUnit4TestAdapterCache());
	}

	public JUnit4TestAdapter(final Class<? extends Object> newTestClass,
			JUnit4TestAdapterCache cache) {
		fCache = cache;
		fNewTestClass = newTestClass;
		fRunner = Request.aClass(newTestClass).getRunner();
//		if (fRunner == null)
//			fRunner = new Runner() {
//				@Override
//				public void run(RunNotifier notifier) {
//					// do nothing
//				}
//
//				@Override
//				public Plan getPlan() {
//					return new CompositePlan("Could not initialize "
//							+ newTestClass.getName());
//				}
//			};
	}

	public int countTestCases() {
		return fRunner.testCount();
	}

	public void run(TestResult result) {
		fRunner.run(fCache.getNotifier(result, this));
	}

	// reflective interface for Eclipse
	public List<Test> getTests() {
		return fCache.asTestList(fRunner.getPlan());
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