package com.droidbrew.javakoans.concurrency.b_division_of_labor.b_Computational_App;

import static com.droidbrew.javakoans.concurrency.base.Koan.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

import org.junit.Test;

import com.droidbrew.javakoans.concurrency.b_division_of_labor.a_IO_App.AbstractNAV;
import com.droidbrew.javakoans.concurrency.b_division_of_labor.a_IO_App.B_ConcurrentNAV;

public class A_SequentialPrimeFinder extends AbstractPrimeFinder {
  public int countPrimes(final int number) {
    return countPrimesInRange(1, number);
  }
  
  @Test
  public void testCalculationSpeed() throws ExecutionException, IOException, InterruptedException {
	int topNumber = 10000000;
	AbstractPrimeFinder pf = new A_SequentialPrimeFinder();
	pf.timeAndCompute(topNumber);
    //assertEquals(___, pf.getNumberOfPrimes());
    assertTrue("time of calculation exceeds expectations and equals " + pf.getTimeSpent() + " seconds.",
    		pf.getTimeSpent() < 3);
  }
}
