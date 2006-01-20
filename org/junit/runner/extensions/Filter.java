package org.junit.runner.extensions;

import org.junit.plan.Plan;
import org.junit.runner.Runner;

public abstract class Filter {
	public static Filter ALL = new Filter() {
		@Override
		public boolean shouldRun(Plan plan) {
			return true;
		}

		@Override
		public String describe() {
			return "all tests";
		}
	};

	public abstract boolean shouldRun(Plan plan);

	public Runner apply(Runner runner) {
		if (runner instanceof Filterable) {
			Filterable filterable = (Filterable)runner;
			filterable.filter(this);
		}
		return runner;
	}

	public abstract String describe();
}
