package org.junit.runner;

import java.util.ArrayList;
import java.util.List;

import junit.runner.Version;

import org.junit.global.RecordingClient;
import org.junit.internal.runner.TextListener;


public class JUnitCore {
	
	RecordingClient recorder= null;
	Runner runner= new Runner();
	
	private JUnitCore() {
	}

	public static void main(String... args) {
		new JUnitCore().run(args);
	}
	
	private void run(String... args) {
		System.out.println("JUnit version " + Version.id());
		TestListener listener= new TextListener();
		runner.addListener(listener);
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
		runner.run(classes.toArray(new Class[0]));
	}

	private void addRecorder() {
		recorder= new RecordingClient();
		runner.addListener(recorder);
	}

}
