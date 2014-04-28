package com.droidbrew.javakoans.concurrency.d_concurrent_refactoring;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.junit.Test;

public class F_LockEnergySource {
	private final long MAXLEVEL = 100;
	private long level = MAXLEVEL;
	private long replenish_counter = 0;
	private long usage = 0; // third related variable
	private final ReadWriteLock monitor = new ReentrantReadWriteLock();
	private static final ScheduledExecutorService replenishTimer =
			Executors.newScheduledThreadPool(10,
					new java.util.concurrent.ThreadFactory() {
				public Thread newThread(Runnable runnable) {
					Thread thread = new Thread(runnable);
					thread.setDaemon(true);
					return thread;
				}
			});
	private ScheduledFuture<?> replenishTask;

	// rafactoring: extract EnergySource out of unut test, make constuctor private
	public F_LockEnergySource() {}
	
	private void init() {
		replenishTask = replenishTimer.scheduleAtFixedRate(new Runnable() {
			public void run() { replenish(); }
			}, 0, 1, TimeUnit.SECONDS);
	}

	public static F_LockEnergySource create() {
		final F_LockEnergySource energySource = new F_LockEnergySource();
		energySource.init();
		return energySource;
	}


	public long getUnitsAvailable() {
		monitor.readLock().lock();
		try {
			return level;
		} finally {
			monitor.readLock().unlock();
		}
	}
	
	public long getUsageCount() {
		monitor.readLock().lock();
		try {
			return usage;
		} finally {
			monitor.readLock().unlock();
		}
	}

	public boolean useEnergy(final long units) {
		monitor.writeLock().lock();
		try {
			if (units > 0 && level >= units) {
				level -= units;
				usage++;
				return true;
			} else {
				return false;
			}
		} finally {
			monitor.writeLock().unlock();
		}
	}

	public synchronized void stopEnergySource() { replenishTask.cancel(false); }  	// now this call in not mandatory before
																		// JVM shutdown

	private void replenish() {
		monitor.writeLock().lock();
		try {
			replenish_counter++;		
			if (level < MAXLEVEL) level++;
		} finally {
			monitor.writeLock().unlock();
		}
	}
	
	public long getReplenish_counter() {
		monitor.readLock().lock();
		try {
			return replenish_counter;
		} finally {
			monitor.readLock().unlock();
		}
	}

	@Test
	public void testCorrectWork() throws InterruptedException{
		long rest_of_units = 0;
		long total_replenish_counter = 0;
		int number_of_sources = 100;
		final F_LockEnergySource[] sources = new F_LockEnergySource[number_of_sources];
		for(int i=0; i<100; i++)
			sources[i] = F_LockEnergySource.create();
		
		for(final F_LockEnergySource source : sources){

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

