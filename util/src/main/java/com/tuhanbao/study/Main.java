package com.tuhanbao.study;

import com.tuhanbao.study.unsafe.UnsafeTest;
import com.tuhanbao.util.exception.MyException;

public class Main {
	public static void main(String args[]) {
		try {
			throw new RuntimeException();
		}
		catch (Exception e) {
//			try {
				e.printStackTrace();
//			}
//			catch (Exception e1) {
//				System.out.println(e1.getMessage());
//			}
		}
		System.out.println("123");
//		ITest test = new UnsafeTest();
//		test.test();
	}
}
