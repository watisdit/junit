package tries;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Runner {

	private int fCount= 0;
	private List<Throwable> fFailures= new ArrayList<Throwable>();

	public void run(Class testClass) throws Exception {
		//TODO: if (TestCase.class.isAssignableFrom(testClass));
		List<Method> methods= getAnnotatedMethods(testClass, Test.class);
		for (Method method : methods) {
			Constructor constructor= testClass.getConstructor(new Class[0]);
			Object test= constructor.newInstance(new Object[0]);
			invokeMethod(test, method);
		}
	}

	//TODO: public void run(TestSuite suite) ...
	
	private void invokeMethod(Object test, Method method) {
		fCount++;
		try {
			setUp(test);
		} catch (InvocationTargetException e) {
			fFailures.add(e.getTargetException());
			return;
		} catch (Throwable e) {
			fFailures.add(e); // TODO: Write test for this
		}
		boolean failed= false;
		try {
			method.invoke(test, new Object[0]);
		} catch (InvocationTargetException e) {
			fFailures.add(e.getTargetException());
			failed= true;
		} catch (Throwable e) {
			// TODO: Make sure this can't happen
			fFailures.add(e);
		} finally {
			try {
				tearDown(test);
			} catch (InvocationTargetException e) {
				if (! failed)
					fFailures.add(e.getTargetException());
			} catch (Throwable e) {
				fFailures.add(e); // TODO: Write test for this
			}
		}
	}

	private void tearDown(Object test) throws Exception {
		List<Method> afters= getAnnotatedMethods(test.getClass(), After.class);
		for (Method after : afters)
			after.invoke(test, new Object[0]);
	}

	private void setUp(Object test) throws Exception {
		List<Method> befores= getAnnotatedMethods(test.getClass(), Before.class);
		for (Method before : befores)
			before.invoke(test, new Object[0]);
	}

	public List<Method> getAnnotatedMethods(Class klass, Class annotationClass) {
		List<Method> results= new ArrayList<Method>();
		Method[] methods= klass.getMethods();
		for (Method each : methods) {
			Annotation annotation= each.getAnnotation(annotationClass);
			if (annotation != null)
				results.add(each);
		}
		return results;
	}

	public int getTestsRun() {
		return fCount;
	}

	public int getFailureCount() {
		return fFailures.size();
	}

	public List<Throwable> getFailures() {
		return fFailures;
	}

}
