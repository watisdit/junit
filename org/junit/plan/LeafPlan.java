package org.junit.plan;

public class LeafPlan extends Plan {
	private final Class fClass;
	private final String fName;

	public LeafPlan(Class clazz, String name) {
		fClass = clazz;
		fName = name;
	}

	@Override
	public int testCount() {
		return 1;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitLeaf(this);
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
		LeafPlan plan = (LeafPlan) obj;
		return plan.getTestClass().equals(getTestClass())
				&& plan.getName().equals(getName());
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
