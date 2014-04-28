package com.droidbrew.javakoans.concurrency.b_division_of_labor.b_Computational_App;

import java.text.DecimalFormat;

public abstract class AbstractPrimeFinder {
	private int numberOfPrimes;
	private double timeSpent;

	public int getNumberOfPrimes() {
		return numberOfPrimes;
	}

	public double getTimeSpent() {
		return timeSpent;
	}

	public boolean isPrime(final int number) {
		if (number <= 1) return false;

		for(int i = 2; i <= Math.sqrt(number); i++)
			if (number % i == 0) return false;

		return true;
	}

	public int countPrimesInRange(final int lower, final int upper) {
		int total = 0;

		for(int i = lower; i <= upper; i++)
			if (isPrime(i)) total++;

		return total;
	}

	public void timeAndCompute(final int number) {
		final long start = System.nanoTime();

		numberOfPrimes = countPrimes(number);

		final long end = System.nanoTime();
		
	    timeSpent = (end - start)/1.0e9;
	}

	public abstract int countPrimes(final int number);
}

