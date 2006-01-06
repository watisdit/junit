package org.junit.runner.extensions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.runner.RunNotifier;
import org.junit.runner.Runner;
import org.junit.runner.plan.CompositePlan;
import org.junit.runner.plan.Plan;

public class CompositeRunner extends Runner implements Filterable {
	private final List<Runner> fRunners = new ArrayList<Runner>();
	private final String fName;
	
	public CompositeRunner(String name) {
		fName = name;
	}
	
	@Override
	public void run(RunNotifier notifier) {
		for (Runner each : fRunners)
			each.run(notifier);
	}

	@Override
	public Plan getPlan() {
		CompositePlan spec = new CompositePlan(fName);
		for (Runner runner : fRunners) {
			spec.addChild(runner.getPlan());
		}
		return spec;
	}

	public List<Runner> getRunners() {
		return fRunners;
	}

	public void addAll(List<? extends Runner> runners) {
		fRunners.addAll(runners);
	}

	public void add(Runner runner) {
		fRunners.add(runner);
	}
	
	public void filter(Filter filter) {
		for (Iterator iter = fRunners.iterator(); iter.hasNext();) {
			Runner runner = (Runner) iter.next();
			if (filter.shouldRun(runner.getPlan())) {
				filter.apply(runner);
			} else {
				iter.remove();
			}
		}
	}

	protected String getName() {
		return fName;
	}
}
