package com.tuhanbao.autotool.mvc;

import com.tuhanbao.HHNZPackageUtil;

/**
 * 一套代码的所有文件
 * @author Administrator
 *
 */
public class CodeBean {
	
	private JavaClass model;
	
	private JavaClass service;
	
	private JavaClass iService;
	
	public CodeBean(String modelName) {
		model = new JavaClass(modelName);
		service = new JavaClass(HHNZPackageUtil.getServiceUrl(modelName));
		iService = new JavaClass(HHNZPackageUtil.getIServiceUrl(modelName));
	}
	
	public JavaClass getModel() {
		return model;
	}

	public JavaClass getService() {
		return service;
	}

	public JavaClass getiService() {
		return iService;
	}

	class JavaClass {
		public String name;
		public String packageUrl;
		
		public JavaClass(String url) {
			
		}
	}
}


