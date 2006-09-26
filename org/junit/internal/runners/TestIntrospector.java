package org.junit.internal.runners;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.Test.None;


public class TestIntrospector {
	private final Class< ?> fTestClass;
	
	public TestIntrospector(Class<?> testClass) {
		fTestClass= testClass;
	}

	public List<Method> getTestMethods(Class<? extends Annotation> annotationClass) {
		return getJavaClass().getTestMethods(new MethodAnnotation(annotationClass));
	}

	JavaClass getJavaClass() {
		return new JavaClass(getTestClass());
	}

	public Class<?> getTestClass() {
		return fTestClass;
	}

	public boolean isIgnored(Method eachMethod) {
		return eachMethod.getAnnotation(Ignore.class) != null;
	}

	long getTimeout(Method method) {
		Test annotation= method.getAnnotation(Test.class);
		long timeout= annotation.timeout();
		return timeout;
	}

	Class<? extends Throwable> expectedException(Method method) {
		Test annotation= method.getAnnotation(Test.class);
		if (annotation.expected() == None.class)
			return null;
		else
			return annotation.expected();
	}

}

