package tries;

import java.io.PrintStream;

import junit.ITestListener;

public class TextListener implements ITestListener {

	private final PrintStream fWriter;

	static public void run(Class testClass) {
		TextListener listener= new TextListener();
		Runner runner= new Runner();
		runner.addListener(listener);
		try {
			runner.run(testClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public TextListener() {
		this(System.out); //TODO: Should this be error?
	}
	
	public TextListener(PrintStream writer) {
		this.fWriter= writer;
	}

	// ITestListener implementation
	public void testStarted(Object test, String name) {
		fWriter.append('.');
	} 

	public void testRunFinished(Runner runner) {
		fWriter.println();
		fWriter.println(runner.getRunCount() + " run, " + runner.getFailureCount() + " failed");
	}

	public void failure(Throwable exception) {
		fWriter.append('E');
	}

}
