package org.junit.runner;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class PreJUnit4TestCaseRunner implements TestListener,  RunnerStrategy {
	
	private Runner fRunner;
	private Test fTest;

	PreJUnit4TestCaseRunner(Runner runner, Class testClass) {
		fRunner= runner;
		fTest= new TestSuite(testClass);
	}
	
	PreJUnit4TestCaseRunner(Runner runner, Test test) {
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
		fRunner.addFailure(new Failure(test, ((TestCase) test).getName(), t)); //TODO: Either cast to TestCase or dynamically invoke getName() or add getName() to junit.framework.Test
	}

	public void addFailure(Test test, AssertionFailedError t) {
		fRunner.addFailure(new Failure(test, ((TestCase) test).getName(), t));
	}

	public void endTest(Test test) {
	}

	public void startTest(Test test) {
		fRunner.startTestCase(test, ((TestCase) test).getName());
	}

}
