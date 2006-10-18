/**
 * 
 */
package org.junit.internal.runners;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TestEnvironment {
	private final JavaTestInterpreter fInterpreter;

	private final PerTestNotifier fNotifier;

	private final Object fTest;

	public TestEnvironment(JavaTestInterpreter interpreter,
			PerTestNotifier notifier, Object test) {
		fInterpreter= interpreter;
		fNotifier= notifier;
		fTest= test;
	}

	void runAfters(Class<? extends Annotation> afterAnnotation,
			JavaClass javaClass, Object test) {
		List<JavaMethod> afters= javaClass.getMethods(afterAnnotation,
				getInterpreter());
		for (JavaMethod after : afters) {
			try {
				after.invoke(test);
			} catch (Throwable e) {
				addFailure(e);
			}
		}
	}

	public void runWithBeforeAndAfter(Runnable protectThis,
			JavaModelElement element) {
		// TODO: is this envious now? Yes
		try {
			runBefores(element, fTest);
			protectThis.run();
		} catch (FailedBefore e) {
		} finally {
			runAfters(element.getAfterAnnotation(), element.getJavaClass(), fTest);
		}
	}

	private void runBefores(JavaModelElement element, Object test) throws FailedBefore {
		// TODO: envious of environment?
		try {
			List<JavaMethod> befores= element.getJavaClass().getMethods(
					element.getBeforeAnnotation(), getInterpreter());

			// TODO: no auto-correct if wrong type on left side of new-style
			// for?
			for (JavaMethod before : befores)
				before.invoke(test);
		} catch (Throwable e) {
			addFailure(e);
			throw new FailedBefore();
		}
	}

	void runWithoutTimeout(final JavaMethod javaMethod) {
		runWithBeforeAndAfter(new Runnable() {
			public void run() {
				javaMethod.runWithoutBeforeAndAfter(TestEnvironment.this, fTest);
			}
		}, javaMethod);
	}

	void runWithTimeout(long timeout, final JavaMethod method) {
		ExecutorService service= Executors.newSingleThreadExecutor();
		Callable<Object> callable= new Callable<Object>() {
			public Object call() throws Exception {
				runWithoutTimeout(method);
				return null;
			}
		};
		Future<Object> result= service.submit(callable);
		service.shutdown();
		try {
			boolean terminated= service.awaitTermination(timeout,
					TimeUnit.MILLISECONDS);
			if (!terminated)
				service.shutdownNow();
			result.get(timeout, TimeUnit.MILLISECONDS); // throws the exception
			// if one occurred
			// during the invocation
		} catch (TimeoutException e) {
			addFailure(new Exception(String.format(
					"test timed out after %d milliseconds", timeout)));
		} catch (Exception e) {
			addFailure(e);
		}
	}

	void addFailure(Throwable e) {
		fNotifier.addFailure(e);
	}

	void run(JavaMethod javaMethod) {
		if (javaMethod.isIgnored()) {
			fNotifier.fireTestIgnored();
			return;
		}
		fNotifier.fireTestStarted();
		try {
			long timeout= javaMethod.getTimeout();
			if (timeout > 0)
				runWithTimeout(timeout, javaMethod);
			else
				runWithoutTimeout(javaMethod);
		} finally {
			fNotifier.fireTestFinished();
		}
	}

	JavaTestInterpreter getInterpreter() {
		return fInterpreter;
	}
}