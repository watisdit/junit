package org.junit.runner.plan;

public abstract class Plan {
	public interface Visitor<T> {
		T visitComposite(CompositePlan plan);

		T visitLeaf(LeafPlan plan);
	}

	public abstract int testCount();

	public abstract <T> T accept(Visitor<T> visitor);

	public abstract String getName();
}
