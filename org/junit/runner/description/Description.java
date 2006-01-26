package org.junit.runner.description;

public abstract class Description {
	public interface Visitor<T> {
		T visitSuite(SuiteDescription description);

		T visitTest(TestDescription description);
	}

	public abstract int testCount();

	public abstract <T> T accept(Visitor<T> visitor);

	public abstract String getName();
}
