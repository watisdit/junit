package tries;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Runner {

	public void run(Class testClass) throws Exception {
		List<Method> methods= getAnnotatedMethods(testClass, Test.class);
		for (Method method : methods) {
			Constructor constructor= testClass.getConstructor(new Class[0]);
			Object test= constructor.newInstance(new Object[0]);
			invokeMethod(test, method);
		}
	}

	private void invokeMethod(Object test, Method method) throws Exception {
		setUp(test);
		method.invoke(test, new Object[0]);
		tearDown(test);
	}

	private void tearDown(Object test) throws Exception {
		List<Method> afters= getAnnotatedMethods(test.getClass(), After.class);
		for (Method after : afters)
			try {
				after.invoke(test, new Object[0]);
			} catch 
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

}
