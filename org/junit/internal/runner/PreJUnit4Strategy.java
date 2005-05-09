package org.junit.internal.runner;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.junit.runner.Runner;
import org.junit.runner.TestFailure;

public class PreJUnit4Strategy implements TestListener,  RunnerStrategy {
	
	private Runner fRunner;
	private Test fTest;

	public PreJUnit4Strategy(Runner runner, Class testClass) {
		fRunner= runner;
		fTest= new TestSuite(testClass);
	}
	
	public PreJUnit4Strategy(Runner runner, Test test) {
		fRunner= runner;
		fTest= test;
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
		fRunner.addFailure(new TestFailure(test, name, t));
	}

	public void addFailure(Test test, AssertionFailedError t) {
		addError(test, t);
	}

	public void endTest(Test test) {
	}

	public void startTest(Test test) {
		fRunner.startTestCase(test, ((TestCase) test).getName());
	}

}
