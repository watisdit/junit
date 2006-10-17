package org.junit.internal.runners;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public abstract class BeforeAndAfterRunner {
	private static class FailedBefore extends Exception {
		private static final long serialVersionUID= 1L;
	}

	private final Class<? extends Annotation> fBeforeAnnotation;

	private final Class<? extends Annotation> fAfterAnnotation;

	private Object fTest;

	private final JavaClass fJavaClass;

	protected PerTestNotifier fPerTestNotifier;

	public BeforeAndAfterRunner(Class<?> testClass,
			Class<? extends Annotation> beforeAnnotation,
			Class<? extends Annotation> afterAnnotation, Object test,
			PerTestNotifier perTestNotifier) {
		fBeforeAnnotation= beforeAnnotation;
		fAfterAnnotation= afterAnnotation;
		fTest= test;
		fPerTestNotifier= perTestNotifier;
		fJavaClass= new JavaClass(testClass);
	}

	public void runProtected() {
		try {
			runBefores();
			runUnprotected();
		} catch (FailedBefore e) {
		} finally {
			runAfters();
		}
	}

	protected abstract void runUnprotected();

	// Stop after first failed @Before
	private void runBefores() throws FailedBefore {
		try {
			List<JavaMethod> befores= fJavaClass
					.getTestMethods(fBeforeAnnotation);

			// TODO: no auto-correct if wrong type on left side of new-style
			// for?
			for (JavaMethod before : befores)
				before.invoke(fTest);
		} catch (InvocationTargetException e) {
			fPerTestNotifier.addFailure(e.getTargetException());
			throw new FailedBefore();
		} catch (Throwable e) {
			fPerTestNotifier.addFailure(e);
			throw new FailedBefore();
		}
	}

	// Try to run all @Afters regardless
	private void runAfters() {
		List<JavaMethod> afters= fJavaClass.getTestMethods(fAfterAnnotation);
		for (JavaMethod after : afters)
			try {
				after.invoke(fTest);
			} catch (InvocationTargetException e) {
				fPerTestNotifier.addFailure(e.getTargetException());
			} catch (Throwable e) {
				fPerTestNotifier.addFailure(e); // Untested, but seems
												// impossible
			}
	}
}
