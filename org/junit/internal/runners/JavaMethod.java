/**
 * 
 */
package org.junit.internal.runners;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

class JavaMethod {
	// TODO: push out
	private final Method fMethod;

	JavaMethod(Method current) {
		fMethod= current;
	}

	public boolean isShadowedBy(Method previous) {
		if (!previous.getName().equals(fMethod.getName()))
			return false;
		if (previous.getParameterTypes().length != fMethod
				.getParameterTypes().length)
			return false;
		for (int i= 0; i < previous.getParameterTypes().length; i++) {
			if (!previous.getParameterTypes()[i].equals(fMethod
					.getParameterTypes()[i]))
				return false;
		}
		return true;
	}

	boolean isShadowedBy(List<Method> results) {
		for (Method each : results) {
			if (isShadowedBy(each))
				return true;
		}
		return false;
	}

	public Annotation getAnnotation(MethodAnnotation methodAnnotation) {
		return fMethod.getAnnotation(methodAnnotation.getAnnotationClass());
	}

	public Method getMethod() {
		return fMethod;
	}
}