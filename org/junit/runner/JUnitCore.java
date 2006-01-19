package org.junit.runner;

import java.util.ArrayList;
import java.util.List;

import junit.runner.Version;
import org.junit.notify.RunListener;
import org.junit.notify.RunNotifier;
import org.junit.runner.internal.OldTestClassRunner;
import org.junit.runner.internal.TextListener;


public class JUnitCore {
	
	private Result fResult;
	public RunNotifier fNotifier;

	public JUnitCore() {
		fNotifier= new RunNotifier();
	}

	public static void main(String... args) {
		new JUnitCore().runMain(args);
		killAllThreads();
	}

	private static void killAllThreads() {
		// TODO: should we exit with status 1 on test failure?
		System.exit(0);
	}
	
	public static Result runClasses(Class... classes) {
		return new JUnitCore().run(classes);
	}
	
	// only public for testing
	public void runMain(String... args) {
		System.out.println("JUnit version " + Version.id());
		// TODO some kind of plugin mechanism here?
		List<Class> classes= new ArrayList<Class>();
		for (String each : args)
			try {
				classes.add(Class.forName(each));
			} catch (ClassNotFoundException e) {
				System.out.println("Could not find class: " + each);
			}
		RunListener listener= new TextListener();
		addListener(listener);
		run(classes.toArray(new Class[0]));
	}

	public String getVersion() {
		return Version.id();
	}
	
	public Result run(Class... testClasses) {
		return run(Request.classes("All", testClasses));
	}

	public Result run(Request request) {
		return run(request.getRunner());
	}

	public void run(junit.framework.Test test) { 
		run(new OldTestClassRunner(test));
	}
	
	public Result run(Runner runner) {
		fResult= new Result();
		RunListener listener = fResult.createListener();
		fNotifier.addListener(listener);
		try {
			fNotifier.fireTestRunStarted(runner.getPlan());
			runner.run(fNotifier);
			fNotifier.fireTestRunFinished(fResult);
		} finally {
			fNotifier.removeListener(listener);
		}
		return fResult;
	}
	
	public void addListener(RunListener listener) {
		fNotifier.addListener(listener);
	}

	public void removeListener(RunListener listener) {
		fNotifier.removeListener(listener);
	}
}
