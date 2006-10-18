package org.junit.internal.runners;

import java.lang.reflect.InvocationTargetException;

public class JavaTestInterpreter {
	
	public JavaTestInterpreter() {
		
	}

	// TODO: push out
	// TODO: be suspicious of everywhere this is constructed
	
	/* (non-Javadoc)
	 * @see org.junit.internal.runners.IJavaTestInterpreter#executeMethodBody(java.lang.Object, org.junit.internal.runners.JavaMethod)
	 */
	public void executeMethodBody(Object test, JavaMethod javaMethod)
			throws IllegalAccessException, InvocationTargetException {
		javaMethod.invoke(test);
	}

	public JavaClass interpretJavaClass(Class<?> superclass) {
		return new JavaClass(superclass);
	}

}
