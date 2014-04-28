package com.droidbrew.javakoans.concurrency.b_division_of_labor.b_Computational_App;

import static com.droidbrew.javakoans.concurrency.base.Koan.___;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class B_ConcurrentPrimeFinder extends AbstractPrimeFinder {
	private int poolSize;
	private int numberOfParts;

	public static B_ConcurrentPrimeFinder factory(final int thePoolSize, 
			final int theNumberOfParts) {
		B_ConcurrentPrimeFinder finder = new B_ConcurrentPrimeFinder();
		finder.poolSize = thePoolSize;
		finder.numberOfParts = theNumberOfParts;
		return finder;
	}  

	public int countPrimes(final int number) {
		int count = 0;      
		try {
			final List<Callable<Integer>> partitions = 
					new ArrayList<Callable<Integer>>(); 
			final int chunksPerPartition = number / numberOfParts;
			for(int i = 0; i < numberOfParts; i++) {
				final int lower = (i * chunksPerPartition) + 1;
				final int upper = 
						(i == numberOfParts - 1) ? number 
								: lower + chunksPerPartition - 1;
				partitions.add(new Callable<Integer>() {
					public Integer call() {
						return countPrimesInRange(lower, upper);
					}        
				});
			}
			final ExecutorService executorPool = 
					Executors.newFixedThreadPool(poolSize); 
			final List<Future<Integer>> resultFromParts = 
					executorPool.invokeAll(partitions, 10000, TimeUnit.SECONDS);
			executorPool.shutdown(); 
			for(final Future<Integer> result : resultFromParts)
				count += result.get(); 
		} catch(Exception ex) { throw new RuntimeException(ex); }
		return count;          
	}


	@Test
	public void testCalculationSpeed() throws ExecutionException, IOException, InterruptedException {
		int topNumber = 10000000;
		int poolSize = 2;
		int numberOfParts = 2;
		AbstractPrimeFinder pf = B_ConcurrentPrimeFinder.factory(poolSize, numberOfParts);
		pf.timeAndCompute(topNumber);
		assertEquals(___, pf.getNumberOfPrimes());
		assertTrue("time of calculation exceeds expectations and equals " + pf.getTimeSpent() + " seconds.",
				pf.getTimeSpent() < 3);
	}
}
