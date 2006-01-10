package org.junit.runner.request;

import java.util.ArrayList;

import org.junit.runner.InitializationErrorListener;
import org.junit.runner.Runner;
import org.junit.runner.extensions.Filter;
import org.junit.runner.plan.CompositePlan;
import org.junit.runner.plan.LeafPlan;
import org.junit.runner.plan.Plan;
import org.junit.runner.plan.Plan.Visitor;

public abstract class Request {
	public static Request aMethod(Class<?> clazz, String methodName) {
		return Request.aClass(clazz)
				.filterWith(new LeafPlan(clazz, methodName));
	}

	public static Request aClass(Class<?> clazz) {
		return new ClassRequest(clazz);
	}

	public static Request classes(String name, Class... classes) {
		return new ClassesRequest(name, classes);
	}

	public abstract Runner getRunner(InitializationErrorListener notifier);

	public Runner getRunner() throws Exception {
		// convenience for testing: don't use in general
		return getRunner(new InitializationErrorListener() {
			public void initializationError(Throwable throwable) {
				throw new RuntimeException(throwable);
			}
		});
	}

	public Request filterWith(Filter filter) {
		return new FilterRequest(this, filter);
	}

	public Request filterWith(final LeafPlan desiredPlan) {
		return filterWith(new Filter() {
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
						return desiredPlan.equals(plan);
					}
				});
			}
		});
	}
}
