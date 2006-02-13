package org.junit.runner;

import java.util.ArrayList;
import java.util.List;

import junit.runner.Version;
import org.junit.internal.runners.OldTestClassRunner;
import org.junit.internal.runners.TextListener;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;


public class JUnitCore {
	
	private Result fResult;
	public RunNotifier fNotifier;

	public JUnitCore() {
		fNotifier= new RunNotifier();
	}

	public static void main(String... args) {
		Result result= new JUnitCore().runMain(args);
		killAllThreads(result);
	}

	private static void killAllThreads(Result result) {
		System.exit(result.wasSuccessful() ? 0 : 1);
	}
	
	public static Result runClasses(Class... classes) {
		return new JUnitCore().run(classes);
	}
	
	// only public for testing
	public Result runMain(String... args) {
		System.out.println("JUnit version " + Version.id());
		List<Class> classes= new ArrayList<Class>();
		for (String each : args)
			try {
				classes.add(Class.forName(each));
			} catch (ClassNotFoundException e) {
				System.out.println("Could not find class: " + each);
			}
		RunListener listener= new TextListener();
		addListener(listener);
		return run(classes.toArray(new Class[0]));
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
		RunListener listener= fResult.createListener();
		addListener(listener);
		try {
			fNotifier.fireTestRunStarted(runner.getDescription());
			runner.run(fNotifier);
			fNotifier.fireTestRunFinished(fResult);
		} finally {
			removeListener(listener);
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
