package junit.framework;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.internal.runner.TestIntrospector;

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

	//TODO Could we just create a strategy on our test class and invoke it?
	//We would have to add an error for every error sent to the test notifier
	public void run(TestResult result) {
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
	}

	public List<TestCase> getTests() {
		return fTests;
	}

	private void oneTimeSetUp() throws Exception {
		List<Method> beforeMethods= getTestIntrospector().getTestMethods(BeforeClass.class);
		for (Method method : beforeMethods)
			method.invoke(null, new Object[0]);
	}

	private void runTests(TestResult result) throws Exception {
		for (TestCase test : fTests) {
			result.run(test);
		}
	}

	private void oneTimeTearDown() throws Exception {
		List<Method> beforeMethods= getTestIntrospector().getTestMethods(AfterClass.class);
		for (Method method : beforeMethods)
			method.invoke(null, new Object[0]);
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