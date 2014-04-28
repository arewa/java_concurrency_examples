package com.droidbrew.javakoans.concurrency.c_modern;

import static com.droidbrew.javakoans.concurrency.base.Koan.___;
import static org.junit.Assert.*;

import org.junit.Test;


import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Account implements Comparable<Account> {
  private int balance;
  public int getBalance() {
	return balance;
}

public final Lock monitor = new ReentrantLock();
  
  public Account(final int initialBalance) { balance = initialBalance; }
  
  public int compareTo(final Account other) {
    return new Integer(hashCode()).compareTo(other.hashCode());
  }
  
  public void deposit(final int amount) {
    monitor.lock();
    try {
      if (amount > 0) balance += amount; 
    } finally { //In case there was an Exception we're covered
      monitor.unlock();
    }
  }
  
  public boolean withdraw(final int amount) {
    try {
      monitor.lock();
      if(amount > 0 && balance >= amount) 
      {
        balance -= amount; 
        return true;
      }
      return false;
    } finally {
      monitor.unlock();
    }
  }
}

class LockException extends Exception {
	  public LockException(final String message) {
	    super(message);
	  }
	}


public class H_AccountService {
  public boolean transfer(final Account from, final Account to, final int amount) 
    throws LockException, InterruptedException {
    final Account[] accounts = new Account[] {from, to};
    Arrays.sort(accounts);
    if(accounts[0].monitor.tryLock(1, TimeUnit.SECONDS)) {
      try {
        if (accounts[1].monitor.tryLock(1, TimeUnit.SECONDS)) {
          try {
            if(from.withdraw(amount)) {
              to.deposit(amount);
              return true;
            } else {
              return false;
            }
          } finally {
            accounts[1].monitor.unlock();
          }
        }
      } finally {
        accounts[0].monitor.unlock();
      }
    }  
    throw new LockException("Unable to acquire locks on the accounts");
  }
  
  @Test
  public void testDeadLock() throws InterruptedException{
	  final Account a = new Account(1000000);
	  final Account b = new Account(5000000);
	  
	  Thread t1 = new Thread(
			  	new Runnable() {
					
					@Override
					public void run() {
						H_AccountService bank = new H_AccountService();
						for(int i=0; i<1000; i++)
							try {
								bank.transfer(a, b, 100);
							} catch (LockException e) {
								i--;
								e.printStackTrace();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
					}
				}
			  );
	  
	  Thread t2 = new Thread(
			  	new Runnable() {
					
					@Override
					public void run() {
						H_AccountService bank = new H_AccountService();
						for(int i=0; i<1000; i++)
							try {
								bank.transfer(b, a, 100);
							} catch (LockException e) {
								i--;
								e.printStackTrace();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
					}
				}
			  );
	  
	  t1.start();
	  t2.start();
	  t1.join();
	  t2.join();
	  assertEquals(1000000, a.getBalance());
	  assertEquals(5000000, b.getBalance());
	  // BTW - this is example of "concurrent test" of suspicious classes. High senseless loading and check 
	  // if balance was not corrupted
  }
}
