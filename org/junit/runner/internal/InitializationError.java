package org.junit.runner.internal;

import java.util.List;

public class InitializationError extends Exception {
	private static final long serialVersionUID = 1L;
	private final List<Throwable> fErrors;

	public InitializationError(List<Throwable> errors) {
		fErrors = errors;
	}

	public List<Throwable> getCauses() {
		return fErrors;
	}
}
