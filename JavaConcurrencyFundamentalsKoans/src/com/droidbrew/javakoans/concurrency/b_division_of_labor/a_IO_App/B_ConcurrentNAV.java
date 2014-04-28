package com.droidbrew.javakoans.concurrency.b_division_of_labor.a_IO_App;

import static com.droidbrew.javakoans.concurrency.base.Koan.___;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.droidbrew.javakoans.concurrency.base.Koan.*;

import static org.junit.Assert.*;

import org.junit.Test;


public class B_ConcurrentNAV extends AbstractNAV {
  public double computeNetAssetValue(final Map<String, Integer> stocks) 
      throws InterruptedException, ExecutionException {      
    final int numberOfCores = Runtime.getRuntime().availableProcessors();
    final double blockingCoefficient = 0.9;
    final int poolSize = (int)(numberOfCores / (1 - blockingCoefficient));
    
    System.out.println("Number of Cores available is " + numberOfCores);
    System.out.println("Pool size is " + poolSize);
    final List<Callable<Double>> partitions = 
      new ArrayList<Callable<Double>>();
    for(final String ticker : stocks.keySet()) {
      partitions.add(new Callable<Double>() {
        public Double call() throws Exception {
          return stocks.get(ticker) * YahooFinance.getPrice(ticker);
        }        
      });
    }
        
    final ExecutorService executorPool = 
      Executors.newFixedThreadPool(poolSize);    
    final List<Future<Double>> valueOfStocks = 
      executorPool.invokeAll(partitions, 10000, TimeUnit.SECONDS);
      
    double netAssetValue = 0.0; 
    for(final Future<Double> valueOfAStock : valueOfStocks)  
      netAssetValue += valueOfAStock.get(); 

    executorPool.shutdown();
    return netAssetValue;   
  } 

  @Test
  public void testCalculationSpeed() throws ExecutionException, IOException, InterruptedException {
	AbstractNAV nav = new B_ConcurrentNAV();
	nav.timeAndComputeValue();
    //assertEquals(___, nav.getAmountValue());
    assertTrue("time of calculation exceeds expectations and equals " + nav.getTimeSpent() + " seconds.",
    		nav.getTimeSpent() < 0.5);
  }
}
