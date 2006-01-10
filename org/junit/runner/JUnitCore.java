package org.junit.runner;

import java.util.ArrayList;
import java.util.List;

import junit.runner.Version;
import org.junit.runner.internal.OldTestClassRunner;
import org.junit.runner.internal.TextListener;
import org.junit.runner.request.Request;


public class JUnitCore {
	
	private Result fResult;
	public RunNotifier fNotifier;

	public JUnitCore() {
		fResult= new Result();
		fNotifier= new RunNotifier();
		fResult.addListenerTo(fNotifier);
	}

	public static void main(String... args) {
		new JUnitCore().runMain(args);
	}
	
	public static Result runClasses(Class... classes) {
		return new JUnitCore().run(classes);
	}
	
	private void runMain(String... args) {
		System.out.println("JUnit version " + Version.id());
		int i= 0;
		// TODO some kind of plugin mechanism here?
		List<Class> classes= new ArrayList<Class>();
		for (; i < args.length; i++) {
			try {
				classes.add(Class.forName(args[i]));
			} catch (ClassNotFoundException e) {
				System.out.println("Could not find class: " + args[i]);
			}
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

	private Result run(Request request) {
		return run(request.getRunner(fNotifier));
	}

	public void run(junit.framework.Test test) { 
		run(new OldTestClassRunner(test));
	}
	
	public Result run(Runner runner) {							
		fNotifier.fireTestRunStarted(runner.getPlan());
		runner.run(fNotifier);
		fNotifier.fireTestRunFinished(fResult);
		return fResult;
	}
	
	public void addListener(RunListener listener) {
		fNotifier.addListener(listener);
	}

	public void removeListener(RunListener listener) {
		fNotifier.removeListener(listener);
	}
}
