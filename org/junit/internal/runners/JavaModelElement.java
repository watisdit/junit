package org.junit.internal.runners;

import java.lang.annotation.Annotation;

public abstract class JavaModelElement {
	public abstract String getName();

	public abstract Class<? extends Annotation> getBeforeAnnotation();

	public abstract Class<? extends Annotation> getAfterAnnotation();

	public abstract JavaClass getJavaClass();
}
