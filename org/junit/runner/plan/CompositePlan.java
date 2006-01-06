package org.junit.runner.plan;

import java.util.ArrayList;


public class CompositePlan extends Plan {
	private final ArrayList<Plan> fChildren = new ArrayList<Plan>();
	private final String fName;

	public CompositePlan(Class<? extends Object> testClass) {
		this(testClass.getName());
	}

	public CompositePlan(String name) {
		fName = name;
	}

	@Override
	public int testCount() {
		int n = 0;
		for (Plan child : getChildren()) {
			n += child.testCount();
		}
		return n;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitComposite(this);
	}

	public void addChild(Plan plan) {
		if (plan == null)
			throw new NullPointerException();

		getChildren().add(plan);
	}

	public ArrayList<Plan> getChildren() {
		return fChildren;
	}

	@Override
	public String getName() {
		return fName;
	}
}
