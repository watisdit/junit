package org.junit.runner.extensions;

import java.util.Comparator;

import org.junit.plan.Plan;
import org.junit.runner.Runner;

public class Sorter implements Comparator<Plan> {

	private final Comparator<Plan> fComparator;

	public Sorter(Comparator<Plan> comparator) {
		fComparator = comparator;
	}

	public void apply(Runner runner) {
		if (runner instanceof Sortable) {
			Sortable sortable = (Sortable) runner;
			sortable.sort(this);
		}
	}

	public int compare(Plan o1, Plan o2) {
		return fComparator.compare(o1, o2);
	}
}
