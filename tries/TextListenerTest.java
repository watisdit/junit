package tries;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import junit.Test;
import junit.framework.TestCase;

public class TextListenerTest extends TestCase {
	
	private Runner runner;
	private OutputStream results;
	private TextListener listener;

	public void setUp() {
		runner= new Runner();
		results= new ByteArrayOutputStream();
		PrintStream writer= new PrintStream(results);
		listener= new TextListener(writer);
		runner.addListener(listener);
	}
	
	public static class OneTest {
		@Test public void one() {};
	}
	
	public void testSuccess() throws Exception {
		runner.run(OneTest.class);
		assertEquals(convert(".\n1 run, 0 failed\n"), results.toString());
	}
	
	public static class ErrorTest {
		@Test public void error() throws Exception {throw new Exception();}
	}
	
	public void testError() throws Exception {
		runner.run(ErrorTest.class);
		assertEquals(convert(".E\n1 run, 1 failed\n"), results.toString());
	}
	
	private String convert(String string) {
		OutputStream results= new ByteArrayOutputStream();
		PrintStream writer= new PrintStream(results);
		writer.println();
		return string.replace("\n", results.toString());
	}
	
}
