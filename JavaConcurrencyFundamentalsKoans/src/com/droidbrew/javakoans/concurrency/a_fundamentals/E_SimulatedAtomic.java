package com.droidbrew.javakoans.concurrency.a_fundamentals;


import static com.droidbrew.javakoans.concurrency.base.Koan.*;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;


import org.junit.Test;

class SimulatedCAS{
	private int value;
	public synchronized int get() { return value; }
	public synchronized int compareAndSwap(int expectedValue,
			int newValue) {
		int oldValue = value;
		if (oldValue == expectedValue)
			value = newValue;
		return oldValue;
	}
	public synchronized boolean compareAndSet(int expectedValue,
			int newValue) {
		return (expectedValue
				== compareAndSwap(expectedValue, newValue));
	}
}

public class E_SimulatedAtomic {
	
	class Counter{
		private SimulatedCAS value = new SimulatedCAS();
		public int getValue() {
			return value.get();
		}
		public int getNext() {
			int v;
			do {
				v = value.get();
			}
			while (v != value.compareAndSwap(v, v + 1));
			return v + 1;
		}
	}

	private Counter c = new Counter();
	
	@Test
	public void testSingleThread() {
		assertEquals(1, c.getNext());
		assertEquals(2, c.getNext());
		assertEquals(3, c.getNext());
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
		// check if is this test is blinking. Why?
		assertTrue("results intersection: " + r1.result, r1.result.size() == 0);

	}

}

