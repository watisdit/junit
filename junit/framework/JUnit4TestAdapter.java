package junit.framework;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.Failure;
import org.junit.runner.Result;
import org.junit.runner.RunListener;
import org.junit.runner.RunNotifier;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.internal.RunnerFactory;
import org.junit.runner.internal.TestIntrospector;

public class JUnit4TestAdapter implements Test {

	private final List<TestCase> fTests;
	private final Class< ? extends Object> fNewTestClass;

	public JUnit4TestAdapter(Class< ? extends Object> newTestClass) {
		fNewTestClass= newTestClass;
		fTests= createTests();
	}

	public int countTestCases() {
		return fTests.size();
	}

	public void run(TestResult result) {
		RunWith annotation= fNewTestClass.getAnnotation(RunWith.class);
		if (annotation == null) {
		List<Exception> errors= getTestIntrospector().validateTestMethods();
		if (!errors.isEmpty()) {
			for (Throwable each : errors)
				result.addError(null, each);
			return;
		}
		try {
			oneTimeSetUp();
			try {
				runTests(result);
			} finally {
				oneTimeTearDown();
			}
		} catch (Exception e) {
			result.addError(this, e);
		}
		} else {
			
			List<Runner> runners= new RunnerFactory().getRunners(getNotifier(result), fNewTestClass);
			for (Runner runner : runners) {
				runner.run();
			}
		}
	}

	private RunNotifier getNotifier(final TestResult result) {
		RunNotifier notifier= new RunNotifier();
		notifier.addListener(new RunListener() {
			public void testIgnored(Method method) throws Exception {
			}
		
			public void testFailure(Failure failure) throws Exception {
			}
		
			public void testFinished(Object test, String name) throws Exception {
			}
		
			public void testStarted(Object test, String name) throws Exception {
				// TODO: flesh these out correctly
				result.startTest(new TestCase("I'm cheating!") {});
			}
		
			public void testRunFinished(Result result) throws Exception {
			}
		
			public void testRunStarted(int testCount) throws Exception {
			}
		});
		return notifier;
	}

	public List<TestCase> getTests() {
		return fTests;
	}

	private void oneTimeSetUp() throws Exception {
		List<Method> beforeMethods= getTestIntrospector().getTestMethods(BeforeClass.class);
		for (Method method : beforeMethods)
			method.invoke(null);
	}

	private void runTests(TestResult result) throws Exception {
		for (TestCase test : fTests) {
			result.run(test);
		}
	}

	private void oneTimeTearDown() throws Exception {
		List<Method> beforeMethods= getTestIntrospector().getTestMethods(AfterClass.class);
		for (Method method : beforeMethods)
			method.invoke(null);
	}

	public Class getTestClass() {
		return fNewTestClass;
	}

	@Override
	public String toString() {
		return "Wrapped " + fNewTestClass.toString();
	}

	private List<TestCase> createTests() {
		List<TestCase> result= new ArrayList<TestCase>();
		List<Method> methods= getTestIntrospector().getTestMethods(org.junit.Test.class);

		for (Method method : methods) {
			try {
				if (getTestIntrospector().isIgnored(method))
					continue;
				Object test= fNewTestClass.newInstance();
				TestCase wrapper= new JUnit4TestCaseAdapter(test, method);
				result.add(wrapper);
			} catch (Exception e) {
				// skip it
			}
		}
		return result;
	}

	private TestIntrospector getTestIntrospector() {
		return new TestIntrospector(fNewTestClass);
	}
}