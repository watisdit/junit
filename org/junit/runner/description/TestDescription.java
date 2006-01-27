package org.junit.runner.description;

public class TestDescription extends Description {
	private final Class fClass;
	private final String fName;

	public TestDescription(Class clazz, String name) {
		fClass= clazz;
		fName= name;
	}

	@Override
	public int testCount() {
		return 1;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitTest(this);
	}

	public Class getTestClass() {
		return fClass;
	}

	@Override
	public int hashCode() {
		return getTestClass().hashCode() + getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof TestDescription) && equalsDescription((TestDescription) obj);
	}

	private boolean equalsDescription(TestDescription description) {
		return description.getTestClass().equals(getTestClass())
				&& description.getName().equals(getName());
	}

	@Override
	public String getName() {
		return fName;
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", getName(), getTestClass().getName());
	}
}
