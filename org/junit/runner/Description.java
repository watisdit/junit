package org.junit.runner;

import java.util.ArrayList;

public class Description {
	public static Description createSuiteDescription(Class<? extends Object> testClass) {
		// TODO: pass class
		return new Description(testClass.getName(), null, null);
	}

	public static Description createSuiteDescription(String name) {
		return new Description(name, null, null);
	}

	public static Description createTestDescription(Class clazz, String name) {
		return new Description(String.format("%s(%s)", name, clazz.getName()),
				clazz, name);
	}
	
	private final ArrayList<Description> fChildren= new ArrayList<Description>();
	private final String fDisplayName;
	
	// TODO: do we want these?  Just try to parse the string instead?
	@SuppressWarnings("unused")
	private final Class fClass;
	@SuppressWarnings("unused")
	private final String fMethodName;

	public Description(final String displayName, final Class class1, final String methodName) {
		fDisplayName= displayName;
		fClass= class1;
		fMethodName= methodName;
	}

	public String getDisplayName() {
		return fDisplayName;
	}

	public void addChild(Description description) {
		if (description == null)
			throw new NullPointerException();
	
		getChildren().add(description);
	}

	public ArrayList<Description> getChildren() {
		return fChildren;
	}

	public boolean isSuite() {
		return !isTest();
	}

	public boolean isTest() {
		return getChildren().isEmpty();
	}

	public int testCount() {
		if (isTest())
			return 1;
		else {
			int n= 0;
			for (Description child : getChildren())
				n+= child.testCount();
			return n;
		}
	}

	@Override
	public int hashCode() {
		return getDisplayName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Description))
			return false;
		Description d = (Description) obj;
		return getDisplayName().equals(d.getDisplayName())
				&& getChildren().equals(d.getChildren());
	}
	
	@Override
	public String toString() {
		return getDisplayName();
	}
}
