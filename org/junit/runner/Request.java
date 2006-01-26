package org.junit.runner;

import java.util.ArrayList;
import java.util.Comparator;

import org.junit.internal.requests.ClassRequest;
import org.junit.internal.requests.ClassesRequest;
import org.junit.internal.requests.ErrorReportingRequest;
import org.junit.internal.requests.FilterRequest;
import org.junit.internal.requests.SortingRequest;
import org.junit.runner.description.Description;
import org.junit.runner.description.SuiteDescription;
import org.junit.runner.description.TestDescription;
import org.junit.runner.description.Description.Visitor;
import org.junit.runner.manipulation.Filter;

public abstract class Request {
	public static Request aMethod(Class<?> clazz, String methodName) {
		return Request.aClass(clazz)
				.filterWith(new TestDescription(clazz, methodName));
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

	public Request filterWith(final TestDescription desiredDescription) {
		return filterWith(new Filter() {
			@Override
			public boolean shouldRun(Description description) {
				return description.accept(new Visitor<Boolean>() {
					public Boolean visitSuite(SuiteDescription description) {
						ArrayList<Description> children = description.getChildren();
						for (Description each : children) {
							if (shouldRun(each))
								return true;
						}
						return false;
					}

					public Boolean visitTest(TestDescription description) {
						return desiredDescription.equals(description);
					}
				});
			}

			@Override
			public String describe() {
				// TODO: Is this duped?  DUP doesn't work
				return String.format("Method %s.%s()", desiredDescription.getTestClass().getName(), desiredDescription.getName());
			}
		});
	}

	public Request sortWith(Comparator<Description> comparator) {
		return new SortingRequest(this, comparator);
	}
}
