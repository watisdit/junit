/**
 * 
 */
package org.junit.internal.runners;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class JavaClass {
	// TODO: push out
	private final Class<?> fClass;

	JavaClass(Class<?> type) {
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

	List<Method> getTestMethods(MethodAnnotation methodAnnotation) {
		List<Method> results= new ArrayList<Method>();
		for (JavaClass eachClass : getSuperClasses()) {
			List<JavaMethod> methods= eachClass.getDeclaredMethods();
			for (JavaMethod eachMethod : methods) {
				Annotation annotation= eachMethod.getAnnotation(methodAnnotation);
				if (annotation != null && ! eachMethod.isShadowedBy(results)) 
					results.add(eachMethod.getMethod());
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

	List<Method> getTestMethods(Class<? extends Annotation> type) {
		return getTestMethods(new MethodAnnotation(type));
	}
}