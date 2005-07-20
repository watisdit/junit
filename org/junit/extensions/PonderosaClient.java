package org.junit.extensions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.junit.runner.Failure;
import org.junit.runner.Runner;

public class PonderosaClient implements org.junit.runner.TestListener {

	private PrintStream fWriter;
	private String fEmail;
	private String fHost= "localhost"; // This should default to ponderosa.threeriversinstitute.org
	public static final int PORT = 80;

	public PonderosaClient() {
		this("");
	}
	
	public PonderosaClient(String email) {
		this(email, System.out);
	}

	public PonderosaClient(String email, PrintStream writer) {
		fEmail= email;
		fWriter= writer;
	}

	public void setHost(String host) {
		fHost= host;
	}
	
	public void setEmail(String email) {
		fEmail= email;
	}
	
	private void processRun(Runner runner) throws Exception {
		URL url= new URL("http", fHost, PORT, encodeRun(runner));
		URLConnection socket = url.openConnection();
		BufferedReader reader= null;
		try {
			reader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
			Map<String,String> values= parse(reader.readLine());
			fWriter.println("JUnit User #" + values.get("user") + ", " + values.get("email"));
			fWriter.println("This run: " + runner.getRunCount() + " test run, " + runner.getFailureCount() + " failed");
			fWriter.println("Today: " + values.get("run today") + " test run, " + values.get("failed today") + " failed");
			fWriter.println("Total: " + values.get("run total") + " test run, " + values.get("failed total") + " failed");
		} finally {
			reader.close();
		}
	}

	private String encodeRun(Runner runner) {
		return "run:email=" + fEmail + ":run count=" + runner.getRunCount() + ":fail count=" + runner.getFailureCount() + ":" + "run time=" + runner.getRunTime() + ":" + "version=" + "4.0" + ":";
	}

	private Map<String, String> parse(String string) {
		Map<String, String> results= new HashMap<String, String>();
		int i= 0;
		while (i < string.length()) {
			int end= string.indexOf('=', i);
			String key= string.substring(i, end);
			i= end;
			end= string.indexOf(':', i);
			String value= string.substring(i + 1, end);
			i= end + 1;
			results.put(key, value);
		}
		return results;
	}

	public void testRunStarted(int testCount) {
	}
	
	public void testRunFinished(Runner runner) {
		try {
			processRun(runner);
		} catch (Exception e) {
			fWriter.println("Error processing test run:\n" + e);
		}
	}
	
	public void testStarted(Object test, String name) {
	}

	public void testFailure(Failure failure) {
	}

	public void testIgnored(Object method) {
	}

}

