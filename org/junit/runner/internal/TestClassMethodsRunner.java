package org.junit.runner.internal;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.notify.RunNotifier;
import org.junit.notify.StoppedByUserException;
import org.junit.plan.CompositePlan;
import org.junit.plan.LeafPlan;
import org.junit.plan.Plan;
import org.junit.runner.extensions.Filter;
import org.junit.runner.extensions.Filterable;
import org.junit.runner.extensions.Sortable;
import org.junit.runner.extensions.Sorter;

public class TestClassMethodsRunner extends ClassRunner implements Filterable, Sortable {
	private final List<Method> fTestMethods;
	
	public TestClassMethodsRunner(Class<? extends Object> klass) throws InitializationError {
		super(klass);
		MethodValidator validator = new MethodValidator(klass);
		validator.validateInstanceMethods();
		validator.assertValid();
		fTestMethods = new TestIntrospector(getTestClass()).getTestMethods(Test.class);
	}
	
	@Override
	public void run(RunNotifier notifier) {
			List<Method> methods = fTestMethods;
			for (Method method : methods) {
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
		List<Method> testMethods = fTestMethods;
		for (Method method : testMethods)
				spec.addChild(methodPlan(method));
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

	private LeafPlan methodPlan(Method method) {
		return new LeafPlan(getTestClass(), testName(method));
	}

	public void filter(Filter filter) {
		for (Iterator iter = fTestMethods.iterator(); iter.hasNext();) {
			Method method = (Method) iter.next();
			if (!filter.shouldRun(methodPlan(method)))
				iter.remove();
		}
	}

	public void sort(final Sorter sorter) {
		Collections.sort(fTestMethods, new Comparator<Method>() {
			public int compare(Method o1, Method o2) {
				return sorter.compare(methodPlan(o1), methodPlan(o2));
			}
		});
	}
}
