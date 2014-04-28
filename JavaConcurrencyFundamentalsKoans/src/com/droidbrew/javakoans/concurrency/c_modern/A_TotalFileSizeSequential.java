package com.droidbrew.javakoans.concurrency.c_modern;

import static com.droidbrew.javakoans.concurrency.base.Koan.___;
import static org.junit.Assert.*;

import org.junit.Test;


import java.io.File;

public class A_TotalFileSizeSequential {
	
  private long getTotalSizeOfFilesInDir(final File file) {
    if (file.isFile()) return file.length();
    
    final File[] children = file.listFiles();
    long total = 0;
    if (children != null)
      for(final File child : children) 
        total += getTotalSizeOfFilesInDir(child);      
    return total;
  }
  
  @Test
  public void testTimeSpent() {
    final long start = System.nanoTime();
    final long total = new A_TotalFileSizeSequential()
      .getTotalSizeOfFilesInDir(new File("C:\\Users"));
    final long end = System.nanoTime();
    double secondsSpent = (end - start)/1.0e9;
    //assertEquals("Wrong total size: ", ___, total);
	assertTrue("time of calculation exceeds expectations and equals " + secondsSpent + " seconds.",
			secondsSpent < 3);
  }
}

