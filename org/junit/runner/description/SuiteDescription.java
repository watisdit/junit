package org.junit.runner.description;

import java.util.ArrayList;

public class SuiteDescription extends Description {
	private final ArrayList<Description> fChildren = new ArrayList<Description>();

	private final String fName;

	public SuiteDescription(Class<? extends Object> testClass) {
		this(testClass.getName());
	}

	public SuiteDescription(String name) {
		fName = name;
	}

	@Override
	public int testCount() {
		int n = 0;
		for (Description child : getChildren()) {
			n += child.testCount();
		}
		return n;
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitSuite(this);
	}

	public void addChild(Description description) {
		if (description == null)
			throw new NullPointerException();

		getChildren().add(description);
	}

	public ArrayList<Description> getChildren() {
		return fChildren;
	}

	@Override
	public String getName() {
		return fName;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof SuiteDescription)
				&& equalsSuiteDescription((SuiteDescription) obj);
	}

	private boolean equalsSuiteDescription(SuiteDescription desc) {
		return getName().equals(desc.getName())
				&& getChildren().equals(desc.getChildren());
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}
}
