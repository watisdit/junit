package org.junit.runner.extensions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import junit.framework.Test;
import org.junit.runner.ClassRunner;
import org.junit.runner.Failure;
import org.junit.runner.RunNotifier;
import org.junit.runner.internal.OldTestClassRunner;

public class AllTests extends ClassRunner {
	private OldTestClassRunner fRunner;

	@SuppressWarnings("unchecked")
	public AllTests(RunNotifier notifier, Class< ? extends Object> klass) {
		super(notifier, klass);
		Method suiteMethod= null;
		Test suite= null;
		try {
			suiteMethod= klass.getMethod("suite");
			if (! Modifier.isStatic(suiteMethod.getModifiers())) {
				getNotifier().fireTestFailure(new Failure(new Exception(klass.getName() + ".suite() must be static")));
				return;
			}
			suite= (Test) suiteMethod.invoke(null); // static method
		} catch (InvocationTargetException e) { // TODO need coverage
			getNotifier().fireTestFailure(new Failure(e.getCause()));
			return;
		} catch (Exception e) {
			getNotifier().fireTestFailure(new Failure(e));
			return;
		}
		fRunner = new OldTestClassRunner(notifier, suite);
	}

	@Override
	public void run() {
		fRunner.run();
	}

	@Override
	public int testCount() {
		return fRunner.testCount();
	}
}
