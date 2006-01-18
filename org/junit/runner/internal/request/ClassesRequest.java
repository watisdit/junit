/**
 * 
 */
package org.junit.runner.internal.request;

import org.junit.runner.Request;
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
	public Runner getRunner() {
		CompositeRunner runner = new CompositeRunner(fName);
		for (Class<? extends Object> each : fClasses) {
			Runner childRunner = Request.aClass(each).getRunner();
			if (childRunner != null)
				runner.add(childRunner);
		}
		return runner;
	}
}