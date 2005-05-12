package org.junit.global;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Recorder {

	private class Run {
		final long fTimestamp;
		final String fEmail;
		final int fRunCount;
		final int fFailedCount;
		private final int fRunTime;
		private final String fVersion;

		private Run(long timestamp, String email, int runCount, int failedCount, int runTime, String version) {
			fTimestamp= timestamp;
			fEmail= email;
			fRunCount= runCount;
			fFailedCount= failedCount;
			fRunTime= runTime;
			fVersion= version;
		}
	}
	
	private final List<Run> runs= new ArrayList<Run>();
	private final List<String> users= new ArrayList<String>();
	protected boolean running= false;
	private Thread serverThread;
	private ServerSocket server;
	
	public void start() {
		try {
			server= new ServerSocket(80);
			running= true;
			serverThread= new Thread() {
				@Override
				public void run() {
					while (running) {
						processRecord();
					}
				}
			};
			serverThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processRecord() {
		try {
			Socket socket= null;
			try {
				socket= server.accept();	
				try {
					Run run= readRecord(socket);
					writeResponse(socket, run);
				} finally {
					socket.close();
				}
			} catch (SocketException e) { // It is normal to be waiting on an accept and have the server shut down
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Run readRecord(Socket socket) throws IOException {
		BufferedReader reader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
		Map<String,String> values= parse(reader.readLine());
		String email= values.get("email");
		int runCount= Integer.parseInt(values.get("run count"));
		int failCount= Integer.parseInt(values.get("fail count")); //TODO: Add JUnit version
		int runTime= Integer.parseInt(values.get("run time"));
		String version= values.get("version");
		if (! users.contains(email))
			users.add(users.size(), email);
		Run result= new Run(System.currentTimeMillis(), email, runCount, failCount, runTime, version);
		addRun(result);
		return result;
	}

	private void addRun(Run result) {
		//TODO: Write to a log here
		runs.add(result);
	}
	
	private void writeResponse(Socket socket, Run run) throws IOException {
		PrintWriter writer= new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
		writer.println("user=" + userNumber(run.fEmail) + ":email=" + run.fEmail + ":run today=" + runToday() + ":failed today=" + failedToday() + ":run total=" + runToday() + ":failed total=" + failedToday() + ":");
		writer.flush();
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


	public void stop() {
		running= false;
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int runToday() {
		int result= 0;
		for (Run each : runs) {
			result+= each.fRunCount;
		}
		return result;
	}

	private int failedToday() {
		int result= 0;
		for (Run each : runs) {
			result+= each.fFailedCount;
		}
		return result;
	}
	
	private int userNumber(String email) {
		return users.indexOf(email);
	}

}
