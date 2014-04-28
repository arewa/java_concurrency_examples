package com.droidbrew.javakoans.concurrency.a_fundamentals;



import static com.droidbrew.javakoans.concurrency.base.Koan.*;

import static org.junit.Assert.*;

import org.junit.Test;

public class F_TimeContinuumViolation {
	int x = 0, y = 0;
	int a = 0, b = 0;
	
	public String launch() throws InterruptedException {
		Thread one = new Thread(new Runnable() {
			public void run() {
				a = 1;
				x = b;
			}
		});
		
		Thread other = new Thread(new Runnable() {
			public void run() {
				b = 1;
				y = a;
			}
		});
		
		
		one.start(); other.start();
		one.join(); other.join();
		
		return "("+ x + "," + y + ")";
	}
	
	@Test
	public void testJMM() throws InterruptedException {
		
		int counterOfViolations = 0;
		
		//Yay! safary for Boson of Higgs ;)
		
		for(int i =0 ; i<100000; i++){
			F_TimeContinuumViolation tcv = new F_TimeContinuumViolation();
			if(tcv.launch().equals("(0,0)"))
					counterOfViolations++;
		}
		
		assertTrue("violations detected: " + counterOfViolations, counterOfViolations == 0);
	}
}
