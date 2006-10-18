/**
 * 
 */
package org.junit.internal.runners;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public class JavaClass extends JavaModelElement {
	// TODO: push out
	private final Class<?> fClass;

	public JavaClass(Class<?> type) {
		fClass= type;
	}

	public List<JavaClass> getSuperClasses() {
		ArrayList<JavaClass> results= new ArrayList<JavaClass>();
		results.add(this);

		// TODO: this will not add parameterized superclasses (need to use
		// interpreter here?)
		if (fClass.getSuperclass() != null)
			results.addAll(new JavaClass(fClass.getSuperclass())
					.getSuperClasses());
		return results;
	}

	public JavaMethodList getMethods(MethodAnnotation methodAnnotation,
			JavaTestInterpreter interpreter) {
		JavaMethodList results= new JavaMethodList();
		for (JavaClass eachClass : getSuperClasses()) {
			for (JavaMethod eachMethod : eachClass
					.getDeclaredMethods(interpreter)) {
				Annotation annotation= eachMethod
						.getAnnotation(methodAnnotation);
				if (annotation != null && !eachMethod.isShadowedBy(results))
					results.add(eachMethod);
			}
		}
		if (methodAnnotation.runsTopToBottom())
			Collections.reverse(results);
		return results;
	}

	private List<JavaMethod> getDeclaredMethods(JavaTestInterpreter interpreter) {
		Method[] declaredMethods= fClass.getDeclaredMethods();
		ArrayList<JavaMethod> javaMethods= new ArrayList<JavaMethod>();
		for (Method method : declaredMethods) {
			javaMethods.add(interpreter.interpretJavaMethod(this, method));
		}
		return javaMethods;
	}

	public JavaMethodList getMethods(Class<? extends Annotation> type,
			JavaTestInterpreter interpreter) {
		return getMethods(new MethodAnnotation(type), interpreter);
	}

	public Class getTestClass() {
		return fClass;
	}

	void validateNoArgConstructor(List<Throwable> errors) {
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
}