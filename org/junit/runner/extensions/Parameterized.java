package org.junit.runner.extensions;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.runner.InitializationErrorListener;
import org.junit.runner.Runner;
import org.junit.runner.internal.CompositeRunner;
import org.junit.runner.internal.TestClassMethodsRunner;
import org.junit.runner.internal.TestClassRunner;

public class Parameterized extends TestClassRunner implements Filterable {
	public static Collection<Object[]> eachOne(Object... params) {
		ArrayList<Object[]> returnThis = new ArrayList<Object[]>();
		for (Object param : params) {
			returnThis.add(new Object[] { param });
		}
		return returnThis;
	}
	
	private static class TestClassRunnerForParameters extends TestClassMethodsRunner {
		private final Object[] fParameters;

		private final int fParameterSetNumber;

		private final Constructor fConstructor;

		private TestClassRunnerForParameters(Class<? extends Object> klass, Object[] parameters, int i) {
			super(klass);
			fParameters = parameters;
			fParameterSetNumber = i;
			fConstructor = getOnlyConstructor();
		}

		@Override
		protected Object createTest() throws Exception {
			return fConstructor.newInstance(fParameters);
		}
		
		@Override
		protected String getName() {
			return String.format("[%s]", fParameterSetNumber);
		}
		
		@Override
		protected String testName(final Method method) {
			return String.format("%s[%s]", method.getName(), fParameterSetNumber);
		}

		private Constructor getOnlyConstructor() {
			Constructor[] constructors = getTestClass().getConstructors();
			assertEquals(1, constructors.length);
			return constructors[0];
		}
	}
	
	public static class RunAllParameterMethods extends CompositeRunner {
		private final Class<? extends Object> fKlass;

		public RunAllParameterMethods(Class<? extends Object> klass) throws Exception {
			super(klass.getName());
			fKlass = klass;
			int i = 0;
			for (final Object[] parameters : getParametersList()) {
				super.add(new TestClassRunnerForParameters(klass, parameters, i++));
			}
		}
		
		@Override
		public boolean canRun(InitializationErrorListener notifier) {
			for (Runner runner : getRunners()) {
				if (!runner.canRun(notifier))
					return false;
			}
			return true;
		}

		@SuppressWarnings("unchecked")
		private Collection<Object[]> getParametersList() throws IllegalAccessException, InvocationTargetException, Exception {
			return (Collection) getParametersMethod().invoke(null);
		}
		
		private Method getParametersMethod() throws Exception {
			for (Method each : fKlass.getMethods()) {
				if (Modifier.isStatic(each.getModifiers())) {
					Annotation[] annotations = each.getAnnotations();
					for (Annotation annotation : annotations) {
						if (annotation.annotationType() == Parameters.class)
							return each;
					}
				}
			}
			throw new Exception("No public static parameters method on class "
					+ getName());
		}
	}
	
	public Parameterized(final Class<? extends Object> klass) throws Exception {
		super(klass, new RunAllParameterMethods(klass));
	}
}
