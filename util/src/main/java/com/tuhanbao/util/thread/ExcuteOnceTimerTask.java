package com.tuhanbao.util.thread;

import java.util.Timer;
import java.util.TimerTask;

import com.tuhanbao.util.log.LogManager;

public abstract class ExcuteOnceTimerTask extends TimerTask {
	private Timer timer;
	
	@Override
	public void run() {
		try {
			runTask();
		} 
		catch (Exception e) {
			LogManager.error(e);
		}
		finally {
			if (this.timer != null) {
				this.timer.cancel();
			}
		}
	}
	
	public void setTimer(Timer timer) {
		this.timer = timer;
	}
	
	protected abstract void runTask() throws Exception;

}
