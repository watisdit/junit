package org.junit.internal.runners;



public class TestMethodRunner {
	private final JavaMethod fJavaMethod;

	private final TestEnvironment fEnvironment;

	public TestMethodRunner(JavaMethod method, TestEnvironment environment) {
		fEnvironment= environment;
		fJavaMethod= method;
	}

	public void run() {
		fEnvironment.run(fJavaMethod);
	}
}
