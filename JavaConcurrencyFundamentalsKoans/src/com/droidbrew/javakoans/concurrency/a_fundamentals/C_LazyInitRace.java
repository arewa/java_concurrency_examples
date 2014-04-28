package com.droidbrew.javakoans.concurrency.a_fundamentals;

import static com.droidbrew.javakoans.concurrency.base.Koan.*;

import static org.junit.Assert.*;

import org.junit.Test;

class BrokenSingleton{
	private BrokenSingleton(){}
	
	private static BrokenSingleton instance = null;
	
	public static BrokenSingleton getInstance(){
		if(instance == null){
			instance = new BrokenSingleton();
		}
		return instance;
	}
	
}

class Runner implements Runnable{
	
	public BrokenSingleton result = null;
	
	@Override
	public void run() {
		result = BrokenSingleton.getInstance();
	}
	
}

public class C_LazyInitRace {
	final int poolSize = 2;
	
	@Test 
	public void ifSingleton(){
		
		Runner[] tasks = new Runner[poolSize];
		Thread[] threads = new Thread[poolSize];
		
		for(int i=0; i<poolSize; i++){
			tasks[i] = new Runner();
			threads[i] = new Thread(tasks[i]);
		}
		
		for(int i=0; i<poolSize; i++)
			threads[i].start();
		
		boolean equalityCondition = true;
		
		try {
			for(int i=0; i<poolSize; i++){
				threads[i].join();
				if(tasks[i].result != tasks[0].result)
					equalityCondition = false;
			}
		} catch (InterruptedException e) {
				e.printStackTrace();
		}
		
		//fix blinking test
		assertTrue(equalityCondition);
		
		
	}
}
