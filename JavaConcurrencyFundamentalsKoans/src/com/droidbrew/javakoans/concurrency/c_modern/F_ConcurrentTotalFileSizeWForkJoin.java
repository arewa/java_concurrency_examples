package com.droidbrew.javakoans.concurrency.c_modern;

import static com.droidbrew.javakoans.concurrency.base.Koan.___;
import static org.junit.Assert.*;

import org.junit.Test;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.List;
import java.util.ArrayList;
import java.io.File;

public class F_ConcurrentTotalFileSizeWForkJoin {
  
  private final static ForkJoinPool forkJoinPool = new ForkJoinPool();
  
  private static class FileSizeFinder extends RecursiveTask<Long> {
    final File file;
    
    public FileSizeFinder(final File theFile) {
      file = theFile;
    }
    
    @Override public Long compute() {
      long size = 0;
      if (file.isFile()) {
        size = file.length();
      } else {
        final File[] children = file.listFiles();
        if (children != null) {
          List<ForkJoinTask<Long>> tasks = 
            new ArrayList<ForkJoinTask<Long>>();
          for(final File child : children) {
            if (child.isFile()) {
              size += child.length();
            } else {
              tasks.add(new FileSizeFinder(child));
            }
          }
          
          for(final ForkJoinTask<Long> task : invokeAll(tasks)) {
            size += task.join();
          }
        }
      }
      
      return size;
    }
  }

  
  @Test
	public void testTimeSpent() throws InterruptedException {
		final long start = System.nanoTime();
		final long total = forkJoinPool.invoke(
		        new FileSizeFinder(new File("C:\\Users")));

		final long end = System.nanoTime();
		double secondsSpent = (end - start)/1.0e9;
		//assertEquals("Wrong total size: ", ___, total);
		assertTrue("time of calculation exceeds expectations and equals " + secondsSpent + " seconds.",
				secondsSpent < 1);
	}
}
