/**
 * 
 */
package org.junit.runner.request;


import org.junit.runner.InitializationErrorListener;
import org.junit.runner.Runner;
import org.junit.runner.extensions.Filter;

public final class FilterRequest extends Request {
	private final Request fRequest;

	private final Filter fFilter;

	FilterRequest(Request classRequest, Filter filter) {
		fRequest = classRequest;
		fFilter = filter;
	}

	@Override
	public Runner getRunner(InitializationErrorListener notifier) {
		return fFilter.apply(fRequest.getRunner(notifier));
	}
}