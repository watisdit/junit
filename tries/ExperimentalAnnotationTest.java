package tries;

import junit.Test;
import junit.framework.TestCase;

// Erich, here is what I thought of when I read your comment about replacing
// decorators with annotations. I wish there was some simple plug-in architecture
// for introducing new annotations. Otherwise the Runner is going to be a monster.

public class ExperimentalAnnotationTest extends TestCase {
	static public class SlowTest {
		//@Timeout 10
		@Test public void tooSlow() throws InterruptedException {
			Thread.sleep(20);
		}
	}
	
	public void testTimeout() throws Exception {
		Runner runner= new Runner();
		runner.run(SlowTest.class);
		assertEquals(1, runner.getFailureCount());
	}
	
	static int count;
	static public class RepeatedTest {
		//@Repeat 2
		@Test public void test() {
			count++;
		}
	}
	
	public void testRepeat() throws Exception {
		Runner runner= new Runner();
		count= 0;
		runner.run(SlowTest.class);
		assertEquals(2, runner.getRunCount());
	}
	
	// I'm sure the test below isn't right.
	//@Parallel
	static public class ParallelTests {
		@Test public void one() throws InterruptedException {
			wait();
		}
		@Test public void two() throws InterruptedException {
			wait();
		}
	}
	
	public void testParallel() {
		assertEquals(3, Thread.activeCount()); //???
	}

}
