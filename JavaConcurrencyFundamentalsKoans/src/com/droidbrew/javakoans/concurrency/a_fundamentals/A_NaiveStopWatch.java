package com.droidbrew.javakoans.concurrency.a_fundamentals;

import static com.droidbrew.javakoans.concurrency.base.Koan.*;
import static org.junit.Assert.*;

import org.junit.Test;


public class A_NaiveStopWatch {
	
	class PseudoButton{
		public String state;
		public void setText(String s){
			state = s;
		}
	}
	
	private PseudoButton startStopButton = new PseudoButton();
	
	private boolean running = false;

	//This will not work
	public void pressButton() {
		if (running) stopCounting(); else startCounting();
	}

	private void startCounting() {
		startStopButton.setText("Stop");
		running = true;
		for(int count = 0; running; count++) {
			System.out.print(String.format("%d", count));
			try {
				Thread.sleep(1000);
			} catch(InterruptedException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	private void stopCounting() {
		running = false;
		startStopButton.setText("Start");
	}
	
	@Test
	public void testSingleThread() {
		A_NaiveStopWatch ui = new A_NaiveStopWatch();
		ui.pressButton();
		assertEquals("Replace ___ with expected value", ___, ui.startStopButton.state);
		ui.pressButton();
		assertEquals("Replace ___ with expected value", ___, ui.startStopButton.state);
	}

}
