package junit.framework; 

import java.lang.reflect.Method;

import org.junit.internal.runner.JUnit4RunnerStrategy;
import org.junit.internal.runner.TestNotifier;
import org.junit.runner.Failure;

public class JUnit4TestCaseAdapter extends TestCase {

	private final Object fTest;
	private final Method fMethod;
	private Throwable fException;

	public JUnit4TestCaseAdapter(Object test, Method method) {
		fTest= test;
		fMethod= method;
	}

	@Override
	protected void runTest() throws Throwable {
			JUnit4RunnerStrategy strategy= new JUnit4RunnerStrategy(new CompatibilityNotifier(), fTest.getClass());
			strategy.invokeTestMethod(fTest, fMethod);
			if (fException != null)
				throw fException;
	}

	private final class CompatibilityNotifier implements TestNotifier {
		public void fireTestFailure(Failure failure) {
			fException= failure.getException();
		}
		
		public void fireTestIgnored(Method method) {
		}
		
		public void fireTestStarted(Object test, String name) {
		}

		public void fireTestFinished(Object test, String name) {
		}
	}
	
	@Override
	public String getName() {
		return fTest.getClass().getName() + "(" + fMethod.getName() + ")";
	}
	
	public Method getTestMethod() {
		return fMethod;
	}
	
	public Object getTest() {
		return fTest;
	}
}
