package org.junit.runner.extensions;

import junit.framework.TestSuite;
import org.junit.runner.ClassRunner;
import org.junit.runner.RunNotifier;
import org.junit.runner.internal.OldTestClassRunner;

public class AllTests extends ClassRunner {
	private OldTestClassRunner fRunner;

	@SuppressWarnings("unchecked")
	public AllTests(RunNotifier notifier, Class< ? extends Object> klass) {
		super(notifier, klass);
		fRunner = new OldTestClassRunner(notifier, getSuite(klass));
	}

	@SuppressWarnings("unchecked")
	private TestSuite getSuite(Class< ? extends Object> klass) {
		return new TestSuite((Class) klass); // TODO call suite() reflectively
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
