package org.junit.runner;

import java.util.ArrayList;
import java.util.Comparator;

import org.junit.plan.CompositePlan;
import org.junit.plan.LeafPlan;
import org.junit.plan.Plan;
import org.junit.plan.Plan.Visitor;
import org.junit.runner.extensions.Filter;
import org.junit.runner.internal.request.ClassRequest;
import org.junit.runner.internal.request.ClassesRequest;
import org.junit.runner.internal.request.ErrorReportingRequest;
import org.junit.runner.internal.request.FilterRequest;
import org.junit.runner.internal.request.SortingRequest;

public abstract class Request {
	public static Request aMethod(Class<?> clazz, String methodName) {
		return Request.aClass(clazz)
				.filterWith(new LeafPlan(clazz, methodName));
	}

	public static Request aClass(Class<?> clazz) {
		return new ClassRequest(clazz);
	}

	public static Request classes(String collectionName, Class... classes) {
		return new ClassesRequest(collectionName, classes);
	}

	public static Request anErrorReport(Class<?> klass, Throwable cause) {
		return new ErrorReportingRequest(klass, cause);
	}
	
	
	public abstract Runner getRunner();

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

	public Request sortWith(Comparator<Plan> comparator) {
		return new SortingRequest(this, comparator);
	}
}
