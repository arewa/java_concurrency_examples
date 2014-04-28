package com.droidbrew.javakoans.concurrency.d_concurrent_refactoring;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class A_EnergySource {
	private final long MAXLEVEL = 100;
	private long level = MAXLEVEL;
	private boolean keepRunning = true;
	private long replenish_counter = 0;

	public A_EnergySource() { 
		new Thread(new Runnable() {
			public void run() { replenish(); }
		}).start();
	}

	public long getUnitsAvailable() { return level; }

	public boolean useEnergy(final long units) {
		if (units > 0 && level >= units) {
			level -= units;
			return true;
		}
		return false;
	}

	public void stopEnergySource() { keepRunning = false; }

	private void replenish() {
		while(keepRunning) {
			replenish_counter++;
			
			if (level < MAXLEVEL) level++;

			try { Thread.sleep(1000); } catch(InterruptedException ex) {}
		}
	}
	
	public long getReplenish_counter() {
		return replenish_counter;
	}

	@Test
	public void testCorrectWork() throws InterruptedException{
		long rest_of_units = 0;
		long total_replenish_counter = 0;
		int number_of_sources = 100;
		final A_EnergySource[] sources = new A_EnergySource[number_of_sources];
		for(int i=0; i<100; i++)
			sources[i] = new A_EnergySource();
		
		for(final A_EnergySource source : sources){

		Thread[] threads = {new Thread(new Runnable() {	
			@Override
			public void run() {
				for(int i=0; i<30; i++){
					source.useEnergy(1);
				}
			}
		}),
		new Thread(new Runnable() {	
			@Override
			public void run() {
				for(int i=0; i<30; i++){
					source.useEnergy(1);
				}
			}
		}),
		new Thread(new Runnable() {	
			@Override
			public void run() {
				for(int i=0; i<30; i++){
					source.useEnergy(1);
				}
			}
		})};
		
		for(Thread t : threads) t.start();
		for(Thread t : threads) t.join();
		
		rest_of_units += source.getUnitsAvailable();
		total_replenish_counter += source.getReplenish_counter();
		}
		
		assertEquals(100, total_replenish_counter);
		// blinking
		assertEquals(number_of_sources*10, rest_of_units);

	}
}

