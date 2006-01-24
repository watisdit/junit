package org.junit.runner.internal;

import junit.extensions.TestDecorator;
import junit.framework.AssertionFailedError;
import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.junit.notify.RunNotifier;
import org.junit.plan.CompositePlan;
import org.junit.plan.LeafPlan;
import org.junit.plan.Plan;
import org.junit.runner.Runner;

public class OldTestClassRunner extends Runner {
	
	@SuppressWarnings("unchecked")
	public OldTestClassRunner(Class< ? extends Object> klass) {
		this(new TestSuite((Class<? extends TestCase>) klass));
	}

	public OldTestClassRunner(Test test) {
		super();
		fTest= test;
	}

	private Test fTest;
	
	@Override
	public void run(RunNotifier notifier) {
		TestResult result= new TestResult();
		result.addListener(getListener(notifier));
		fTest.run(result);
	}

	private TestListener getListener(final RunNotifier notifier) {
		return new TestListener() {
			public void endTest(Test test) {
				// TODO: uncovered
				notifier.fireTestFinished(asPlan(test));
			}

			public void startTest(Test test) {
				notifier.fireTestStarted(asPlan(test));
			}
		
			// Implement junit.framework.TestListener
			//TODO method not covered
			public void addError(Test test, Throwable t) {
				TestFailure failure= new TestFailure(asPlan(test), t);
				notifier.fireTestFailure(failure);
			}
			

			private LeafPlan asPlan(Test test) {
				return new LeafPlan(test.getClass(), getName(test));
			}

			private String getName(Test test) {
				if (test instanceof TestCase)
					return ((TestCase) test).getName();
				else
					return test.toString();
			}

			//TODO method not covered
			public void addFailure(Test test, AssertionFailedError t) {
				addError(test, t);
			}
		
		};
	}
	
	@Override
	public Plan getPlan() {
		return makePlan(fTest);
	}

	private Plan makePlan(Test test) {
		if (test instanceof TestCase) {
			TestCase tc = (TestCase) test;
			return new LeafPlan(tc.getClass(), tc.getName());
		} else if (test instanceof TestSuite) {
			TestSuite ts = (TestSuite) test;
			CompositePlan plan = new CompositePlan(ts.getName());
			int n = ts.testCount();
			for (int i = 0; i < n; i++)
				plan.addChild(makePlan(ts.testAt(i)));
			return plan;
		} else if (test instanceof JUnit4TestAdapter) {
			JUnit4TestAdapter adapter = (JUnit4TestAdapter) test;
			return adapter.getPlan();
		} else if (test instanceof TestDecorator) {
			TestDecorator decorator = (TestDecorator) test;
			return makePlan(decorator.getTest());
		} else {
			// This is the best we can do in this case
			return new CompositePlan(test.getClass());
		}
	}
}
