package org.junit.global;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.junit.runner.Failure;
import org.junit.runner.Runner;

public class RecordingClient implements org.junit.runner.TestListener {

	private PrintStream fWriter;
	private String fEmail;
	private String fHost= "localhost"; // This should default to recorder.org.junit

	public RecordingClient() {
		this("");
	}
	
	public RecordingClient(String email) {
		this(email, System.out);
	}

	public RecordingClient(String email, PrintStream writer) {
		fEmail= email;
		fWriter= writer;
	}

	public void setHost(String host) {
		fHost= host;
	}
	
	public void setEmail(String email) {
		fEmail= email;
	}
	
	public void testRunStarted() {
	}

	public void testRunFinished(Runner runner) {
		try {
			processRun(runner);
		} catch (Exception e) {
			fWriter.println("Error processing test run:\n" + e);
		}
	}

//	private void writeRecord(Runner runner) throws IOException {
//		URL server= new URL("http", "localhost", "record:" + "email=" + fEmail + ":runCount=" + runner.getRunCount() + ":failCount=" + runner.getFailureCount());
//		System.out.println("c: About to open connection");
//		URLConnection connection= server.openConnection();
//		System.out.println("c: About to get stream");
//		InputStream stream= connection.getInputStream();
//		System.out.println("c: About to create reader");
//		BufferedReader reader= new BufferedReader(new InputStreamReader(stream));
//		System.out.println("c: Opened stream");
//		System.out.println("c: Client read: " + reader.readLine());
//		reader.close();
//	}

	private void processRun(Runner runner) throws Exception {
		Socket socket= new Socket(fHost, 80);
		try {
			writeRecord(runner, socket);
			processResponse(runner, socket);
		} finally {
			socket.close();
		}
	}

	private void writeRecord(Runner runner, Socket socket) throws IOException {
		PrintWriter writer= new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
		//TODO: Fetch the real version number
		writer.println("email=" + fEmail + ":run count=" + runner.getRunCount() + ":fail count=" + runner.getFailureCount() + ":" + "run time=" + runner.getRunTime() + ":" + "version=" + "4.0" + ":");
		writer.flush();
	}
	
	private void processResponse(Runner runner, Socket socket) throws IOException {
		BufferedReader reader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
		Map<String, String> values= parse(reader.readLine());
		fWriter.println("JUnit User #" + values.get("user") + ", " + values.get("email"));
		fWriter.println("This run: " + runner.getRunCount() + " test run, " + runner.getFailureCount() + " failed");
		fWriter.println("Today: " + values.get("run today") + " test run, " + values.get("failed today") + " failed");
		fWriter.println("Total: " + values.get("run total") + " test run, " + values.get("failed total") + " failed");
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

	public void testStarted(Object test, String name) {
	}

	public void testFailure(Failure failure) {
	}

	public void testIgnored(Object method) {
	}

}
