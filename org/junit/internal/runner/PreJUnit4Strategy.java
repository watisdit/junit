package org.junit.internal.runner;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class PreJUnit4Strategy implements junit.framework.TestListener,  RunnerStrategy {
	
	private Test fTest;
	private TestNotifier fNotifier;

	public PreJUnit4Strategy(TestNotifier notifier, Class<? extends TestCase> testClass) {
		this(notifier);
		fTest= new TestSuite(testClass);
	}
	
	public PreJUnit4Strategy(TestNotifier notifier, Test test) {
		this(notifier);
		fTest= test;
	}
	
	private PreJUnit4Strategy(TestNotifier notifier) {
		fNotifier= notifier;		
	}
	
	public void run() {
		TestResult result= new TestResult();
		result.addListener(this);
		fTest.run(result);
	}

	// Implement junit.framework.TestListener
	public void addError(Test test, Throwable t) {
		String name;
		if (test instanceof TestCase)
			name= ((TestCase) test).getName();
		else
			name= test.toString();
		TestFailure failure= new TestFailure(test, name, t);
		fNotifier.fireTestFailure(failure);
	}

	public void addFailure(Test test, AssertionFailedError t) {
		addError(test, t);
	}

	public void endTest(Test test) {
	}

	public void startTest(Test test) {
		fNotifier.fireTestStarted(test, ((TestCase) test).getName());
	}

}
