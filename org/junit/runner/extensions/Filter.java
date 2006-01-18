package org.junit.runner.extensions;

import org.junit.plan.Plan;
import org.junit.runner.Runner;

public abstract class Filter {
	public static Filter ALWAYS = new Filter() {
		@Override
		public boolean shouldRun(Plan plan) {
			return true;
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
}
