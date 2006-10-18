package org.junit.runners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JavaTestInterpreter;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.Request;
import org.junit.runner.Runner;

/**
 * Using <code>Suite</code> as a runner allows you to manually build a suite
 * containing tests from many classes. It is the JUnit 4 equivalent of the JUnit
 * 3.8.x static {@link junit.framework.Test} <code>suite()</code> method. To
 * use it, annotate a class with <code>@RunWith(Suite.class)</code> and
 *                       <code>SuiteClasses(TestClass1.class, ...)</code>.
 *                       When you run this class, it will run all the tests in
 *                       all the suite classes.
 */
public class Suite extends TestClassRunner {
	/**
	 * The <code>SuiteClasses</code> annotation specifies the classes to be
	 * run when a class annotated with <code>@RunWith(Suite.class)</code> is run.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface SuiteClasses {
		public Class[] value();
	}

	/**
	 * Internal use only.
	 */
	public Suite(Class<?> klass) throws InitializationError {
		this(klass, getAnnotatedClasses(klass));
	}

	/**
	 * Internal use only.
	 * @throws InitializationError 
	 */
	public Suite(Class<?> klass, final Class[] annotatedClasses) throws InitializationError {
		super(klass, new JavaTestInterpreter() {
			@Override
			public Runner runnerFor(Class<?> klass) throws InitializationError {
				return Request.classes(klass.getName(), annotatedClasses)
						.getRunner();
			}
		});
	}

	private static Class[] getAnnotatedClasses(Class<?> klass)
			throws InitializationError {
		SuiteClasses annotation= klass.getAnnotation(SuiteClasses.class);
		if (annotation == null)
			throw new InitializationError(String.format(
					"class '%s' must have a SuiteClasses annotation", klass
							.getName()));
		return annotation.value();
	}
}
