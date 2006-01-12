/**
 * 
 */
package org.junit.runner.request;

import org.junit.runner.InitializationErrorListener;
import org.junit.runner.Runner;
import org.junit.runner.internal.CompositeRunner;

public class ClassesRequest extends Request {
	private final Class[] fClasses;
	private final String fName;

	public ClassesRequest(String name, Class... classes) {
		fClasses = classes;
		fName = name;
	}

	@Override
	public Runner getRunner(InitializationErrorListener notifier) {
		CompositeRunner runner = new CompositeRunner(fName);
		for (Class<? extends Object> each : fClasses) {
			Runner childRunner = Request.aClass(each).getRunner(notifier);
			if (childRunner != null)
				runner.add(childRunner);
		}
		return runner;
	}
}