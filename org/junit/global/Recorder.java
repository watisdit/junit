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

public class Recorder {  //TODO: Need to add a persistence strategy

	private class Run {
		final String fEmail;
		final int fRunCount;
		final int fFailedCount;

		private Run(String email, int runCount, int failedCount) {
			fEmail= email;
			fRunCount= runCount;
			fFailedCount= failedCount;
		}
	}
	
	private final List<Run> runs= new ArrayList<Run>();
	private final List<String> users= new ArrayList<String>();
	protected boolean running;
	private Thread serverThread;
	private ServerSocket server;
	
	public void start() {
		running= true;
		try {
			server= new ServerSocket(80);
		} catch (IOException e) {
			e.printStackTrace();
		}
		serverThread= new Thread() {
			@Override
			public void run() {
				while (running) {
					processRecord();
				}
			}
		};
		serverThread.start();
	}

	private void processRecord() {
		try {
			Socket socket= null;
			try {
				socket= server.accept();	
			} catch (SocketException e) { // It is normal to be waiting on an accept and have the server shut down
				return;
			}
			BufferedReader reader= new BufferedReader(new InputStreamReader(socket.getInputStream()));				    
			Run run= processRecord(reader.readLine());
			PrintWriter writer= new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
			writeResponse(run, writer);
			writer.flush();
			writer.close();
			reader.close();
		} catch (IOException e) {
			System.out.println("Throwing");
			e.printStackTrace();
		}
	}
	
	protected Run processRecord(String string) {
		Map<String, String> values= parse(string);
		String email= values.get("email");
		String runCount= values.get("run count");
		String failCount= values.get("fail count");
		if (! users.contains(email))
			users.add(users.size(), email);
		Run result= new Run(email, Integer.parseInt(runCount), Integer.parseInt(failCount)); //TODO: Add timestamp
		runs.add(result);
		return result;
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

	private void writeResponse(Run run, PrintWriter writer) {
		writer.println("user=" + userNumber(run.fEmail) + ":email=" + run.fEmail + ":run today=" + runToday() + ":failed today=" + failedToday() + ":run total=" + runToday() + ":failed total=" + failedToday() + ":");
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
