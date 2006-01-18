package org.junit.runner.internal;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;
import org.junit.notify.RunNotifier;
import org.junit.notify.StoppedByUserException;
import org.junit.plan.CompositePlan;
import org.junit.plan.LeafPlan;
import org.junit.plan.Plan;
import org.junit.runner.extensions.Filter;
import org.junit.runner.extensions.Filterable;

public class TestClassMethodsRunner extends ClassRunner implements Filterable {
	private Filter fFilter = Filter.ALWAYS;
	
	public TestClassMethodsRunner(Class<? extends Object> klass) throws InitializationError {
		super(klass);
		MethodValidator validator = new MethodValidator(klass);
		validator.validateInstanceMethods();
		validator.assertValid();
	}
	
	@Override
	public void run(RunNotifier notifier) {
			List<Method> methods = getTestMethods();
			for (Method method : methods) {
				if (shouldRun(method))
					try {
						invokeTestMethod(method, notifier);
					} catch (StoppedByUserException e) {
						throw e;
					} catch (Exception e) {
						notifier.fireNonTestFailure(e);
					}
			}
	}

	@Override
	public Plan getPlan() {
		CompositePlan spec = new CompositePlan(getName());
		List<Method> testMethods = getTestMethods();
		for (Method method : testMethods) {
			if (shouldRun(method))
				spec.addChild(methodPlan(method));
		}
		return spec;
	}

	protected String getName() {
		return getTestClass().getName();
	}
	
	protected Object createTest() throws Exception {
		return getTestClass().getConstructor().newInstance();
	}

	protected void invokeTestMethod(Method method, RunNotifier notifier) throws Exception {
		new TestMethodRunner(createTest(), method, notifier, methodPlan(method)).run();
	}

	protected String testName(Method method) {
		return method.getName();
	}

	private List<Method> getTestMethods() {
		return new TestIntrospector(getTestClass()).getTestMethods(Test.class);
	}
	
	private LeafPlan methodPlan(Method method) {
		return new LeafPlan(getTestClass(), testName(method));
	}

	private boolean shouldRun(Method method) {
		return fFilter.shouldRun(methodPlan(method));
	}
	
	public void filter(Filter filter) {
		fFilter = filter;
	}
}
