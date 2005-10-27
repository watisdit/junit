package org.junit.runner.internal;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.junit.runner.RunNotifier;
import org.junit.runner.Runner;

public class OldTestClassRunner extends Runner implements junit.framework.TestListener {
	
	@SuppressWarnings("unchecked")
	public OldTestClassRunner(RunNotifier notifier, Class< ? extends Object> klass) {
		this(notifier, new TestSuite((Class<? extends TestCase>) klass));
	}

	public OldTestClassRunner(RunNotifier notifier, Test test) {
		super(notifier);
		fTest= test;
	}

	private Test fTest;
	
	@Override
	public void run() {
		TestResult result= new TestResult();
		result.addListener(this);
		fTest.run(result);
	}

	@Override
	public int testCount() {
		return fTest.countTestCases();
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
		getNotifier().fireTestFailure(failure);
	}

	//TODO method not covered
	public void addFailure(Test test, AssertionFailedError t) {
		addError(test, t);
	}

	public void endTest(Test test) {
	}

	public void startTest(Test test) {
		getNotifier().fireTestStarted(test, ((TestCase) test).getName());
	}

}
