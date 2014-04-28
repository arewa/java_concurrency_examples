package com.droidbrew.javakoans.concurrency.d_concurrent_refactoring;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class C_TimerEnergySource {
	private final long MAXLEVEL = 100;
	private long level = MAXLEVEL;
	private long replenish_counter = 0;
	private static final ScheduledExecutorService replenishTimer =
		Executors.newScheduledThreadPool(10);
	private ScheduledFuture<?> replenishTask;

	// rafactoring: extract EnergySource out of unut test, make constuctor private
	public C_TimerEnergySource() {}
	

	private void init() {
		replenishTask = replenishTimer.scheduleAtFixedRate(new Runnable() {
			public void run() { replenish(); }
			}, 0, 1, TimeUnit.SECONDS);
	}

	public static C_TimerEnergySource create() {
		final C_TimerEnergySource energySource = new C_TimerEnergySource();
		energySource.init();
		return energySource;
	}


	public long getUnitsAvailable() { return level; }

	public boolean useEnergy(final long units) {
		if (units > 0 && level >= units) {
			level -= units;
			return true;
		}
		return false;
	}

	public void stopEnergySource() { replenishTask.cancel(false); }  // don't forget unlock JVM for shutdown!

	private void replenish() {
			replenish_counter++;		
			if (level < MAXLEVEL) level++;
	}
	
	public long getReplenish_counter() {
		return replenish_counter;
	}

	@Test
	public void testCorrectWork() throws InterruptedException{
		long rest_of_units = 0;
		long total_replenish_counter = 0;
		int number_of_sources = 100;
		final C_TimerEnergySource[] sources = new C_TimerEnergySource[number_of_sources];
		for(int i=0; i<100; i++)
			sources[i] = C_TimerEnergySource.create();
		
		for(final C_TimerEnergySource source : sources){

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
		//blinking
		assertEquals(number_of_sources*10, rest_of_units);

	}
}

