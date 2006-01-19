package org.junit.runner.extensions;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.runner.Request;
import org.junit.runner.internal.InitializationError;
import org.junit.runner.internal.TestClassRunner;

public class Suite extends TestClassRunner implements Filterable, Sortable {
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface SuiteClasses {
		public Class[] value();
	}

	public Suite(Class< ? extends Object> klass) throws InitializationError {
		this(klass, getAnnotatedClasses(klass));
	}

	public Suite(Class<? extends Object> klass, Class[] annotatedClasses) throws InitializationError {
		super(klass, Request.classes(klass.getName(), annotatedClasses).getRunner());
	}

	private static Class[] getAnnotatedClasses(Class<? extends Object> klass) throws InitializationError {
		SuiteClasses annotation= klass.getAnnotation(SuiteClasses.class);
		if (annotation == null)
			throw new InitializationError(String.format("class '%s' must have a SuiteClasses annotation", klass.getName()));
		return annotation.value();
	}
}
