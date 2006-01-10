/**
 * 
 */
package org.junit.runner.request;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.runner.InitializationErrorListener;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.internal.OldTestClassRunner;
import org.junit.runner.internal.TestClassRunner;

public class ClassRequest extends Request {
	private final Class<? extends Object> fEach;

	public ClassRequest(Class<? extends Object> each) {
		fEach = each;
	}

	@Override
	public Runner getRunner(InitializationErrorListener notifier) {
		try {
			Class klass = getRunnerClass(fEach);
			Constructor constructor = klass.getConstructor(Class.class); // TODO good error message if no such constructor
			Runner runner = (Runner) constructor
					.newInstance(new Object[] { fEach });
			if (runner.canRun(notifier))
				return runner;
		} catch (InvocationTargetException e) {
			notifier.initializationError(e.getCause());
		} catch (Exception e) {
			notifier.initializationError(e);
		}
		return null;
	}
	
	Class getRunnerClass(Class< ? extends Object> testClass) {
		Class klass;
		RunWith annotation= testClass.getAnnotation(RunWith.class);
		if (annotation != null) {
			klass= annotation.value();
		} else if (isPre4Test(testClass)) {
			klass= OldTestClassRunner.class; 
		} else {
			klass= TestClassRunner.class;
		}
		return klass;
	}
	
	boolean isPre4Test(Class< ? extends Object> testClass) {
		return junit.framework.TestCase.class.isAssignableFrom(testClass);
	}
}