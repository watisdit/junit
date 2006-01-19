package org.junit.runner.internal.request;

import java.util.Comparator;

import org.junit.plan.Plan;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runner.extensions.Sorter;

public class SortingRequest extends Request {
	private final Request fRequest;
	private final Comparator<Plan> fComparator;

	public SortingRequest(Request request, Comparator<Plan> comparator) {
		fRequest = request;
		fComparator = comparator;
	}

	@Override
	public Runner getRunner() {
		Runner runner = fRequest.getRunner();
		new Sorter(fComparator).apply(runner);
		return runner;
	}
}
