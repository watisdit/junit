package org.junit.runner;

import java.util.ArrayList;
import java.util.List;

import junit.runner.Version;

import org.junit.extensions.PonderosaClient;
import org.junit.internal.runner.TextListener;


public class JUnitCore {
	
	PonderosaClient recorder= null;
	Runner runner= new Runner();
	
	private JUnitCore() {
	}

	public static void main(String... args) {
		new JUnitCore().runMain(args);
	}
	
	public static void run(Class... classes) {
		new JUnitCore().runClasses(classes);
	}
	
	private void runMain(String... args) {
		System.out.println("JUnit version " + Version.id());
		int i= 0;
		while (args[i].startsWith("-")) {
			if (args[i].equals("-email")) {
				if (recorder == null)
					addRecorder();
				recorder.setEmail(args[++i]);
				i++;
			}
			if (args[i].equals("-host")) {
				if (recorder == null)
					addRecorder();
				recorder.setHost(args[++i]);
				i++;
			}
		}
		List<Class> classes= new ArrayList<Class>();
		for (; i < args.length; i++) {
			try {
				classes.add(Class.forName(args[i]));
			} catch (ClassNotFoundException e) {
				System.out.println("Could not find class: " + args[i]);
			}
		}
		runClasses(classes.toArray(new Class[0]));
	}

	private void runClasses(Class[] classes) {
		TestListener listener= new TextListener();
		runner.addListener(listener);
		runner.run(classes);
	}

	private void addRecorder() {
		recorder= new PonderosaClient();
		runner.addListener(recorder);
	}

	public String getVersion() {
		return Version.id();
	}
}
