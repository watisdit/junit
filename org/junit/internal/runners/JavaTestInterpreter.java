package org.junit.internal.runners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.runner.Runner;

public class JavaTestInterpreter {

	public JavaTestInterpreter() {

	}

	// TODO: push out
	// TODO: be suspicious of everywhere this is constructed

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.junit.internal.runners.IJavaTestInterpreter#executeMethodBody(java.lang.Object,
	 *      org.junit.internal.runners.JavaMethod)
	 */
	public void executeMethodBody(Object test, JavaMethod javaMethod)
			throws IllegalAccessException, InvocationTargetException {
		javaMethod.invoke(test);
	}

	public Runner runnerFor(Class<?> klass) throws InitializationError {
		MethodValidator methodValidator= new MethodValidator(klass);
		validate(methodValidator);
		methodValidator.assertValid();
		return new TestClassMethodsRunner(new JavaClass(klass), this);
	}

	protected void validate(MethodValidator methodValidator) {
		methodValidator.validateAllMethods();
	}
	
	protected JavaMethod interpretJavaMethod(final JavaClass klass, Method method) {
		return new JavaMethod(klass, method);
	}
}
