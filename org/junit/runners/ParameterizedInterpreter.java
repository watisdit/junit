/**
 * 
 */
package org.junit.runners;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

import org.junit.internal.runners.CompositeRunner;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JavaClass;
import org.junit.internal.runners.JavaMethod;
import org.junit.internal.runners.JavaTestInterpreter;
import org.junit.internal.runners.MethodValidator;
import org.junit.internal.runners.TestClassMethodsRunner;
import org.junit.runner.Runner;
import org.junit.runners.Parameterized.Parameters;

class ParameterizedInterpreter extends JavaTestInterpreter {
	private static class ParameterizedJavaClass extends JavaClass {
		private final Object[] fParameters;

		private final int fNumber;

		private ParameterizedJavaClass(Class<?> type, Object[] parameters,
				int number) {
			super(type);
			fParameters= parameters;
			fNumber= number;
		}

		// TODO: too many exceptions
		@Override
		protected Object newInstance() throws InstantiationException,
				IllegalAccessException, InvocationTargetException,
				NoSuchMethodException {
			return getOnlyConstructor().newInstance(fParameters);
		}

		@Override
		public String getName() {
			return String.format("[%s]", fNumber);
		}

		private Constructor getOnlyConstructor() {
			Constructor[] constructors= getTestClass().getConstructors();
			assertEquals(1, constructors.length);
			return constructors[0];
		}
	}

	@Override
	protected JavaMethod interpretJavaMethod(final JavaClass klass,
			Method method) {
		return new JavaMethod(klass, method) {
			@Override
			public String getName() {
				return String.format("%s%s", super.getName(), klass.getName());
			}
		};
	}

	// TODO: I think this now eagerly reads parameters, which was never the
	// point.

	// TODO: pull interpreter back in?
	@Override
	public Runner runnerFor(Class klass) throws InitializationError {
		CompositeRunner runner= new CompositeRunner(klass.getName());
		int i= 0;
		for (final Object each : getParametersList(new JavaClass(klass))) {
			if (each instanceof Object[]) {
				runner
						.add(new TestClassMethodsRunner(
								new ParameterizedJavaClass(klass,
										(Object[]) each, i++), this));
			} else
				throw new InitializationError(String.format(
						"%s.%s() must return a Collection of arrays.", klass
								.getName(), getParametersMethod(
								new JavaClass(klass)).getName()));
		}
		return runner;
	}

	@Override
	protected void validate(MethodValidator methodValidator) {
		methodValidator.validateStaticMethods();
		methodValidator.validateInstanceMethods();
	}

	private Collection getParametersList(JavaClass javaClass)
			throws InitializationError {
		Method parametersMethod= getParametersMethod(javaClass);
		try {
			return (Collection) parametersMethod.invoke(null);
		} catch (Exception e) {
			throw new InitializationError(e);
		}
	}

	private Method getParametersMethod(JavaClass javaClass)
			throws InitializationError {
		// TODO: is this DUP?
		List<Method> methods= javaClass.getMethods(Parameters.class);

		for (Method each : methods) {
			int mods= each.getModifiers();
			if (Modifier.isStatic(mods) && Modifier.isPublic(mods)) {
				return each;
			}
		}
		throw new InitializationError(
				"No public static parameters method on class "
						+ javaClass.getName());
	}
}