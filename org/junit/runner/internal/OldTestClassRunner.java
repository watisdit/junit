package org.junit.runner.internal;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.junit.runner.Runner;

public class OldTestClassRunner implements junit.framework.TestListener,  Runner {
	
	private Test fTest;
	private RunNotifier fNotifier;

	@SuppressWarnings("unchecked")
	public void initialize(RunNotifier notifier, Class<? extends Object> testClass) {
		initialize(notifier);
		fTest= new TestSuite((Class<? extends TestCase>) testClass);
	}
	
	public void initialize(RunNotifier notifier, Test test) {
		initialize(notifier);
		fTest= test;
	}
	
	private void initialize(RunNotifier notifier) {
		fNotifier= notifier;		
	}
	
	public void run() {
		TestResult result= new TestResult();
		result.addListener(this);
		fTest.run(result);
	}

	// Implement junit.framework.TestListener
	//TODO method not covered
	public void addError(Test test, Throwable t) {
		String name;
		if (test instanceof TestCase)
			name= ((TestCase) test).getName();
		else
			name= test.toString();
		TestFailure failure= new TestFailure(test, name, t);
		fNotifier.fireTestFailure(failure);
	}

	//TODO method not covered
	public void addFailure(Test test, AssertionFailedError t) {
		addError(test, t);
	}

	public void endTest(Test test) {
	}

	public void startTest(Test test) {
		fNotifier.fireTestStarted(test, ((TestCase) test).getName());
	}

	public int testCount() {
		return fTest.countTestCases();
	}

}
