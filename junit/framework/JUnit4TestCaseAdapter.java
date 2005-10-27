package junit.framework; 

import java.lang.reflect.Method;

import org.junit.runner.Failure;
import org.junit.runner.RunNotifier;
import org.junit.runner.internal.TestMethodRunner;

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
			CompatibilityNotifier notifier= new CompatibilityNotifier();
			new TestMethodRunner(fTest, fMethod, notifier).run();
			if (fException != null)
				throw fException;
	}

	private final class CompatibilityNotifier extends RunNotifier {
		@Override
		public void fireTestFailure(Failure failure) {
			fException= failure.getException();
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
