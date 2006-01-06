package org.junit.runner.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.ClassRunner;
import org.junit.runner.RunNotifier;
import org.junit.runner.extensions.Filter;
import org.junit.runner.extensions.Filterable;
import org.junit.runner.plan.CompositePlan;
import org.junit.runner.plan.LeafPlan;
import org.junit.runner.plan.Plan;

public class TestClassMethodsRunner extends ClassRunner implements Filterable {
	private Filter fFilter = Filter.ALWAYS;
	private final TestIntrospector fTestIntrospector;

	public TestClassMethodsRunner(Class<? extends Object> klass) {
		super(klass);
		fTestIntrospector = new TestIntrospector(klass);
	}

	@Override
	protected void addFatalErrors(ArrayList<Exception> fatalErrors) {
		fTestIntrospector.validateInstanceMethods(fatalErrors);
	}
	
	@Override
	public void run(RunNotifier notifier) {
			List<Method> methods = getTestMethods();
			for (Method method : methods) {
				if (shouldRun(method))
					try {
						invokeTestMethod(method, notifier);
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
		return fTestIntrospector.getTestMethods(Test.class);
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
