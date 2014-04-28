package com.droidbrew.javakoans.concurrency.a_fundamentals;


import static com.droidbrew.javakoans.concurrency.base.Koan.*;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;


class Counter{
	private int count = 0;
	
	// insight: add synchronized and see if test is still blinking
	public int getNext(){
		return count++;
	}
}

public class B_SimpleCounter {

	private Counter c = new Counter();
	
	@Test
	public void testSingleThread() {
		assertEquals(0, c.getNext());
		assertEquals(1, c.getNext());
		assertEquals(2, c.getNext());
	}
	
	class Runner implements Runnable{
		
		public Set<Integer> result = new HashSet<>();
		
		@Override
		public void run() {
			for(int i=0; i<1000; i++)
				result.add(c.getNext());
		}
		
	}
	
	//run several times - test is blinking
	@Test
	public void weReExpecingToHaveUniqSets() {
	
		Runner r1 = new Runner();
		Runner r2 = new Runner();
		
		Thread t1 = new Thread(r1);
		Thread t2 = new Thread(r2);
		
		t1.start();
		t2.start();
		
		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		r1.result.retainAll(r2.result);
		// fix the blinking test
		assertTrue("results intersection: " + r1.result, r1.result.size() < 20);

	}

}
