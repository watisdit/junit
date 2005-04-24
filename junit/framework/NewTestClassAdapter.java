package junit.framework;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class NewTestClassAdapter implements Test {

	private List<Method> methods;
	private final Class fNewTestClass;

	public NewTestClassAdapter(Class newTestClass) {
		fNewTestClass= newTestClass;
		methods= getTestMethods(newTestClass);
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
		return methods.size();
	}

	public void run(TestResult result) {
		for (Method method : methods) {
			Object test= null;
			try {
				test= fNewTestClass.newInstance();
			} catch (Exception e) {
				result.addError(this, e);
			}
			TestCase wrapper= new NewTestCaseAdapter(test, method);
			result.run(wrapper);
		}
	}

	@Override
	public String toString() {
		return "Wrapped " + fNewTestClass.toString();
	}
}