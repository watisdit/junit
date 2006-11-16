/**
 * 
 */
package org.junit.internal.javamodel;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.internal.runners.JavaTestInterpreter;
import org.junit.internal.runners.MethodAnnotation;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

public class JavaClass extends WrappedJavaModelElement {
	// TODO: push out
	private final Class<?> fClass;

	public JavaClass(Class<?> type, JavaTestInterpreter interpreter) {
		super(interpreter);
		fClass= type;
	}

	public List<JavaClass> getSuperClasses() {
		ArrayList<JavaClass> results= new ArrayList<JavaClass>();
		results.add(this);

		// TODO: this will not add parameterized superclasses (need to use
		// interpreter here?)
		if (fClass.getSuperclass() != null)
			results.addAll(getInterpreter().buildClass(fClass.getSuperclass())
					.getSuperClasses());
		return results;
	}

	public List<Method> getMethods(MethodAnnotation methodAnnotation) {
		List<Method> results= new ArrayList<Method>();
		for (JavaClass eachClass : getSuperClasses()) {
			for (Method eachMethod : eachClass.getDeclaredMethods()) {
				Annotation annotation= eachMethod
						.getAnnotation(methodAnnotation.getAnnotationClass());
				if (annotation != null && !isShadowedBy(eachMethod, results))
					results.add(eachMethod);
			}
		}
		if (methodAnnotation.runsTopToBottom())
			Collections.reverse(results);
		return results;
	}

	private boolean isShadowedBy(Method target, Method previous) {
		if (!previous.getName().equals(target.getName()))
			return false;
		if (previous.getParameterTypes().length != target.getParameterTypes().length)
			return false;
		for (int i= 0; i < previous.getParameterTypes().length; i++) {
			if (!previous.getParameterTypes()[i].equals(target
					.getParameterTypes()[i]))
				return false;
		}
		return true;
	}

	boolean isShadowedBy(Method target, List<Method> results) {
		for (Method each : results) {
			if (isShadowedBy(target, each))
				return true;
		}
		return false;
	}

	private Method[] getDeclaredMethods() {
		return fClass.getDeclaredMethods();
	}

	public List<Method> getMethods(Class<? extends Annotation> type) {
		return getMethods(new MethodAnnotation(type));
	}

	public Class getTestClass() {
		return fClass;
	}

	public void validateNoArgConstructor(List<Throwable> errors) {
		try {
			getTestClass().getConstructor();
		} catch (Exception e) {
			errors.add(new Exception(
					"Test class should have public zero-argument constructor",
					e));
		}
	}

	@Override
	public String getName() {
		return fClass.getName();
	}

	protected Object newInstance() throws InstantiationException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		return getTestClass().getConstructor().newInstance();
	}

	@Override
	public Class<? extends Annotation> getAfterAnnotation() {
		return AfterClass.class;
	}

	@Override
	public Class<? extends Annotation> getBeforeAnnotation() {
		return BeforeClass.class;
	}

	@Override
	public JavaClass getJavaClass() {
		return this;
	}

	public JavaMethodList getTestMethods(JavaTestInterpreter javaTestInterpreter) {
		List<Method> methods= getMethods(Test.class);
		JavaMethodList list= new JavaMethodList(this);
		for (Method method : methods) {
			list.add(javaTestInterpreter.interpretJavaMethod(this, method));
		}
		return list;
	}

	@Override
	public Description getDescription() {
		return Description.createSuiteDescription(fClass);
	}

	@Override
	public void run(RunNotifier notifier) {
		// TODO Auto-generated method stub
		
	}
}