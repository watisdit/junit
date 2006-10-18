/**
 * 
 */
package org.junit.runners;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.internal.runners.JavaClass;
import org.junit.internal.runners.JavaMethod;
import org.junit.internal.runners.JavaTestInterpreter;

class ParameterizedInterpreter extends JavaTestInterpreter {
	private final Object fEach;

	private final int fNumber;

	ParameterizedInterpreter(Object each, int number) {
		fEach= each;
		fNumber= number;
	}

	@Override
	public JavaClass interpretJavaClass(Class<?> superclass) {
		return new JavaClass(superclass) {
			// TODO: too many exceptions
			@Override
			protected Object newInstance()
					throws InstantiationException,
					IllegalAccessException, InvocationTargetException,
					NoSuchMethodException {
				return getOnlyConstructor().newInstance(
						(Object[]) fEach);
			}

			@Override
			public String getName() {
				return String.format("[%s]", fNumber);
			}

			@Override
			protected JavaMethod makeJavaMethod(Method method) {
				return new JavaMethod(this, method) {
					@Override
					public String getName() {
						return String.format("%s[%s]", super.getName(),
								fNumber);
					}
				};
			}

			private Constructor getOnlyConstructor() {
				Constructor[] constructors= getTestClass()
						.getConstructors();
				assertEquals(1, constructors.length);
				return constructors[0];
			}
		};
	}
}