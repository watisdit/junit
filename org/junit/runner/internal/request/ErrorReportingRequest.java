package org.junit.runner.internal.request;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.junit.plan.LeafPlan;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runner.internal.CompositeRunner;
import org.junit.runner.internal.ErrorReportingRunner;
import org.junit.runner.internal.InitializationError;

public class ErrorReportingRequest extends Request {

	private final Class<?> fClass;
	private final Throwable fCause;

	public ErrorReportingRequest(Class<?> klass, Throwable cause) {
		fClass = klass;
		fCause = cause;
	}

	@Override
	public Runner getRunner() {
		List<Throwable> goofs = getCauses(fCause);
		CompositeRunner runner = new CompositeRunner(fClass.getName());
		for (int i = 0; i < goofs.size(); i++) {
			final LeafPlan plan = new LeafPlan(fClass, "initializationError" + i);
			final Throwable throwable = goofs.get(i);
			runner.add(new ErrorReportingRunner(plan, throwable));
		}
		return runner;
	}
	
	private List<Throwable> getCauses(Throwable cause) {
		if (cause instanceof InvocationTargetException)
			return getCauses(cause.getCause());
		if (cause instanceof InitializationError)
			return ((InitializationError) cause).getCauses();
		// TODO: untested
		return Arrays.asList(cause);	
	}
}
