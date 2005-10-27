package org.junit.runner;


public abstract class Runner {

	private final RunNotifier fNotifier;
	
	public Runner(RunNotifier notifier) {
		fNotifier= notifier;
	}

	public abstract void run();

	public abstract int testCount();

	protected RunNotifier getNotifier() {
		return fNotifier;
	}

}