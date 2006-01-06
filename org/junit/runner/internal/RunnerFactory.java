package org.junit.runner.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.InitializationErrorListener;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.extensions.Filter;
import org.junit.runner.plan.CompositePlan;
import org.junit.runner.plan.LeafPlan;
import org.junit.runner.plan.Plan;
import org.junit.runner.plan.Plan.Visitor;

public class RunnerFactory {
	public List<Runner> getRunners(InitializationErrorListener notifier, Class... testClasses) {
		List<Runner> runners= new ArrayList<Runner>();
		for (Class<? extends Object> each : testClasses) {
				Runner runner = getRunner(notifier, each);
				if (runner != null)
					runners.add(runner);
			}
		return runners;
	}

	public Runner getRunner(InitializationErrorListener notifier, Class<? extends Object> each) {
		try {
			Class klass= getRunnerClass(each);
			Constructor constructor= klass.getConstructor(Class.class); // TODO good error message if no such constructor
			Runner runner= (Runner) constructor.newInstance(new Object[] {each});
			if (runner.canRun(notifier))
				return runner;
		} catch (InvocationTargetException e) {
			notifier.initializationError(e.getCause());
		} catch (Exception e) {
			notifier.initializationError(e);
		}
			return null;
	}

	private Class getRunnerClass(Class< ? extends Object> testClass) {
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

	private boolean isPre4Test(Class< ? extends Object> testClass) {
		return junit.framework.TestCase.class.isAssignableFrom(testClass);
	}

	public Runner getRunner(InitializationErrorListener notifier, Class<? extends Object> each, LeafPlan plan) {
		return filterByPlan(getRunner(notifier, each), plan);
	}
	
	private Runner filterByPlan(Runner runner, final LeafPlan desiredPlan) {
		Filter filter = new Filter() {
			@Override
			public boolean shouldRun(Plan plan) {
				return plan.accept(new Visitor<Boolean>() {
					public Boolean visitComposite(CompositePlan plan) {
						ArrayList<Plan> children = plan.getChildren();
						for (Plan each : children) {
							if (shouldRun(each))
								return true;
						}
						return false;
					}
					
					public Boolean visitLeaf(LeafPlan plan) {
						return plan.equals(desiredPlan);
					}
				});
			}
		};
		return filter.apply(runner);
	}
}
