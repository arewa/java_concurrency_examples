
package com.droidbrew.javakoans.concurrency.b_division_of_labor.a_IO_App;

import java.util.Map;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.droidbrew.javakoans.concurrency.base.Koan.*;

import static org.junit.Assert.*;

import org.junit.Test;

public class A_SequentialNAV extends AbstractNAV {
  public double computeNetAssetValue(
    final Map<String, Integer> stocks) throws IOException {
    double netAssetValue = 0.0;
    for(String ticker : stocks.keySet()) {
      netAssetValue += stocks.get(ticker) * YahooFinance.getPrice(ticker);
    }
    return netAssetValue;   
  } 
   
  @Test
  public void testCalculationSpeed() throws ExecutionException, IOException, InterruptedException {
	AbstractNAV nav = new A_SequentialNAV();
	nav.timeAndComputeValue();
    assertEquals(___, nav.getAmountValue());
    assertTrue("time of calculation exceeds expectations and equals " + nav.getTimeSpent() + " seconds.",
    		nav.getTimeSpent() < 1);
  }
}
