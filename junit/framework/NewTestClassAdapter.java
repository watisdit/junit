package junit.framework; 

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Expected;
import org.junit.tries.JUnit4TestRunner;

public class NewTestClassAdapter implements Test {

	private List<Method> fMethods;
	private final Class<? extends Object> fNewTestClass;

	public NewTestClassAdapter(Class<? extends Object> newTestClass) {
		fNewTestClass= newTestClass;
		fMethods= getTestMethods(newTestClass);
	}

	private List<Method> getTestMethods(Class newTestClass) {
		List<Method> results= new ArrayList<Method>();
		Method[] methods= newTestClass.getMethods();
		for (Method each : methods) {
			Annotation annotation= each.getAnnotation(org.junit.Test.class);
			if (annotation != null)
				results.add(each);
		}
		return results;
	}

	public int countTestCases() {
		return fMethods.size();
	}

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

	private void oneTimeSetUp() throws Exception {
		List<Method> beforeMethods= JUnit4TestRunner.getAnnotatedMethods(fNewTestClass, BeforeClass.class);
		for (Method method : beforeMethods) {
			method.invoke(null, new Object[0]);
		}
	}
	
	private void runTests(TestResult result) throws Exception {
		for (Method method : fMethods) {
			Object test= fNewTestClass.newInstance();
			Expected annotation= method.getAnnotation(Expected.class); // Can safely be null
			TestCase wrapper= new NewTestCaseAdapter(test, method, annotation);
			result.run(wrapper);
		}
	}
	
	private void oneTimeTearDown() throws Exception {
		List<Method> beforeMethods= JUnit4TestRunner.getAnnotatedMethods(fNewTestClass, AfterClass.class);
		for (Method method : beforeMethods) {
			method.invoke(null, new Object[0]);
		}
	}

	@Override
	public String toString() {
		return "Wrapped " + fNewTestClass.toString();
	}
}