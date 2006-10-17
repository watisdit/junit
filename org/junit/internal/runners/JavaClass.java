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

public class JavaClass extends JavaModelElement {
	// TODO: push out
	private final Class<?> fClass;

	public JavaClass(Class<?> type) {
		fClass= type;
	}

	public List<JavaClass> getSuperClasses() {
		ArrayList<JavaClass> results= new ArrayList<JavaClass>();
		Class<?> current= fClass;
		while (current != null) {
			results.add(new JavaClass(current));
			current= current.getSuperclass();
		}
		return results;
	}

	List<JavaMethod> getTestMethods(MethodAnnotation methodAnnotation) {
		List<JavaMethod> results= new ArrayList<JavaMethod>();
		for (JavaClass eachClass : getSuperClasses()) {
			for (JavaMethod eachMethod : eachClass.getDeclaredMethods()) {
				Annotation annotation= eachMethod.getAnnotation(methodAnnotation);
				if (annotation != null && ! eachMethod.isShadowedBy(results)) 
					results.add(eachMethod);
			}
		}
		if (methodAnnotation.runsTopToBottom())
			Collections.reverse(results);
		return results;
	}

	private List<JavaMethod> getDeclaredMethods() {
		Method[] declaredMethods= fClass.getDeclaredMethods();
		ArrayList<JavaMethod> javaMethods= new ArrayList<JavaMethod>();
		for (Method method : declaredMethods) {
			javaMethods.add(new JavaMethod(method));
		}
		return javaMethods;
	}

	List<JavaMethod> getTestMethods(Class<? extends Annotation> type) {
		return getTestMethods(new MethodAnnotation(type));
	}

	public Class getTestClass() {
		return fClass;
	}

	void validateNoArgConstructor(List<Throwable> errors) {
		try {
			getTestClass().getConstructor();
		} catch (Exception e) {
			errors.add(new Exception("Test class should have public zero-argument constructor", e));
		}
	}

	@Override
	public String getName() {
		return fClass.getName();
	}

	Object newTestObject()
			throws InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		return getTestClass().getConstructor().newInstance();
	}
}