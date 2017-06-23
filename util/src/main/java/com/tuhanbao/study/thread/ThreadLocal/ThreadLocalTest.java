package com.tuhanbao.study.thread.ThreadLocal;

import com.tuhanbao.study.ITest;

public class ThreadLocalTest implements ITest {

	ThreadLocal<String> tl = new ThreadLocal<String>();
	
	@Override
	public Object test() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				printName();
			}
		}, "t1").start();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				printName();
			}
		}, "t2").start();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				printName();
			}
		}, "t3").start();
		return null;
	}

	private void printName() {
		String name = Thread.currentThread().getName();
		tl.set(name);
		System.out.println(name + " name is " + tl.get());
	}
}
