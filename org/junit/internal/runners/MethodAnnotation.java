/**
 * 
 */
package org.junit.internal.runners;

import java.lang.annotation.Annotation;

import org.junit.RunSuperclassMethodsFirst;

final class MethodAnnotation {
	// TODO: push out
	// TODO: package too big?
	private final Class<? extends Annotation> fAnnotation;

	MethodAnnotation(Class<? extends Annotation> annotation) {
		fAnnotation= annotation;
	}

	public boolean runsTopToBottom() {
		return fAnnotation.getAnnotation(RunSuperclassMethodsFirst.class) != null;
	}

	public Class<? extends Annotation> getAnnotationClass() {
		return fAnnotation;
	}
}