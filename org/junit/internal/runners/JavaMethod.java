/**
 * 
 */
package org.junit.internal.runners;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Test.None;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

// TODO: check names of various "run" methods

public class JavaMethod extends JavaModelElement {
	// TODO: push out
	private final Method fMethod;

	private final JavaClass fJavaClass;

	public JavaMethod(JavaClass javaClass, Method current) {
		fJavaClass= javaClass;
		fMethod= current;
	}

	private boolean isShadowedBy(JavaMethod previousJavaMethod) {
		Method previous= previousJavaMethod.fMethod;
		if (!previous.getName().equals(fMethod.getName()))
			return false;
		if (previous.getParameterTypes().length != fMethod.getParameterTypes().length)
			return false;
		for (int i= 0; i < previous.getParameterTypes().length; i++) {
			if (!previous.getParameterTypes()[i].equals(fMethod
					.getParameterTypes()[i]))
				return false;
		}
		return true;
	}

	boolean isShadowedBy(List<JavaMethod> results) {
		for (JavaMethod each : results) {
			if (isShadowedBy(each))
				return true;
		}
		return false;
	}

	public Annotation getAnnotation(MethodAnnotation methodAnnotation) {
		return fMethod.getAnnotation(methodAnnotation.getAnnotationClass());
	}

	boolean isIgnored() {
		return fMethod.getAnnotation(Ignore.class) != null;
	}

	long getTimeout() {
		return getTestAnnotation().timeout();
	}

	private Test getTestAnnotation() {
		return fMethod.getAnnotation(Test.class);
	}

	Class<? extends Throwable> expectedException() {
		Test annotation= getTestAnnotation();
		if (annotation.expected() == None.class)
			return null;
		else
			return annotation.expected();
	}

	boolean isUnexpected(Throwable exception) {
		return !expectedException().isAssignableFrom(exception.getClass());
	}

	boolean expectsException() {
		return expectedException() != null;
	}

	public Object invoke(Object object) throws IllegalAccessException,
			InvocationTargetException {
		return fMethod.invoke(object);
	}

	@Override
	public String getName() {
		return fMethod.getName();
	}

	void validateAsTestMethod(boolean isStatic, List<Throwable> errors) {
		Method each= fMethod;
		if (Modifier.isStatic(each.getModifiers()) != isStatic) {
			String state= isStatic ? "should" : "should not";
			errors.add(new Exception("Method " + each.getName() + "() " + state
					+ " be static"));
		}
		if (!Modifier.isPublic(each.getDeclaringClass().getModifiers()))
			errors
					.add(new Exception("Class "
							+ each.getDeclaringClass().getName()
							+ " should be public"));
		if (!Modifier.isPublic(each.getModifiers()))
			errors.add(new Exception("Method " + each.getName()
					+ " should be public"));
		if (each.getReturnType() != Void.TYPE)
			errors.add(new Exception("Method " + each.getName()
					+ " should be void"));
		if (each.getParameterTypes().length != 0)
			errors.add(new Exception("Method " + each.getName()
					+ " should have no parameters"));
	}

	// TODO: push out
	public Description description() {
		return Description.createTestDescription(fJavaClass.getTestClass(),
				getName());
	}

	public boolean isStatic() {
		return Modifier.isStatic(getModifiers());
	}

	// TODO: sort methods
	private int getModifiers() {
		return fMethod.getModifiers();
	}

	public boolean isPublic() {
		return Modifier.isPublic(getModifiers());
	}

	// TODO: push out
	@Override
	public JavaClass getJavaClass() {
		return fJavaClass;
	}

	@Override
	public Class<? extends Annotation> getAfterAnnotation() {
		return After.class;
	}

	@Override
	public Class<? extends Annotation> getBeforeAnnotation() {
		return Before.class;
	}

	public void runWithoutBeforeAndAfter(TestEnvironment environment,
			Object test) {
		try {
			environment.getInterpreter().executeMethodBody(test, this);
			if (expectsException())
				environment
						.addFailure(new AssertionError("Expected exception: "
								+ expectedException().getName()));
		} catch (InvocationTargetException e) {
			Throwable actual= e.getTargetException();
			if (!expectsException())
				environment.addFailure(actual);
			else if (isUnexpected(actual)) {
				String message= "Unexpected exception, expected<"
						+ expectedException().getName() + "> but was<"
						+ actual.getClass().getName() + ">";
				environment.addFailure(new Exception(message, actual));
			}
		} catch (Throwable e) {
			environment.addFailure(e);
		}
	}

	void invokeTestMethod(RunNotifier notifier, JavaTestInterpreter interpreter) {
		Object test;
		try {
			test= getJavaClass().newInstance();
		} catch (InvocationTargetException e) {
			notifier.testAborted(description(), e.getCause());
			return;
		} catch (Exception e) {
			notifier.testAborted(description(), e);
			return;
		}
		TestEnvironment testEnvironment= new TestEnvironment(interpreter,
				new PerTestNotifier(notifier, description()), test);
		testEnvironment.run(this);
	}
}