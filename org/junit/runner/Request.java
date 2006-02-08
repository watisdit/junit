package org.junit.runner;

import java.util.Comparator;

import org.junit.internal.requests.ClassRequest;
import org.junit.internal.requests.ClassesRequest;
import org.junit.internal.requests.ErrorReportingRequest;
import org.junit.internal.requests.FilterRequest;
import org.junit.internal.requests.SortingRequest;
import org.junit.runner.manipulation.Filter;

public abstract class Request {
	public static Request aMethod(Class<?> clazz, String methodName) {
		return Request.aClass(clazz)
				.filterWith(Description.createTestDescription(clazz, methodName));
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

	public Request filterWith(final Description desiredDescription) {
		return filterWith(new Filter() {
			@Override
			public boolean shouldRun(Description description) {
				// TODO: test for equality even if we have children?
				if (description.isTest())
					return desiredDescription.equals(description);
				else {
					for (Description each : description.getChildren()) {
						if (shouldRun(each))
							return true;
					}
					return false;					
				}
			}

			@Override
			public String describe() {
				return String.format("Method %s", desiredDescription.getDisplayName());
			}
		});
	}

	public Request sortWith(Comparator<Description> comparator) {
		return new SortingRequest(this, comparator);
	}
}
