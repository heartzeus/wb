package com.tuhanbao;

import java.io.IOException;

import com.tuhanbao.io.txt.util.TxtUtil;
import com.tuhanbao.util.exception.MyException;
import com.tuhanbao.util.log.LogManager;

public class HHNZPackageUtil {
	private String rootPath;
	
	private Config config;
	
	private String controllerTemplate, iServiceTemplate, serviceTemplate;
	
	private static final String SERVICE = "#SERVICE#";
	private static final String SERVICE_NAME = "#SERVICE_NAME#";
	private static final String ISERVICE = "#ISERVICE#";
	private static final String ISERVICE_NAME = "#ISERVICE_NAME#";
	private static final String MODEL = "#MODEL#";
	private static final String MODEL_NAME = "#MODEL_NAME#";
	
	public HHNZPackageUtil(String rootPath, Config config) {
		this.rootPath = rootPath;
		this.config = config;
		
		try {
			controllerTemplate = TxtUtil.read(rootPath + Constants.FILE_SEP + config.getcTemplate());
			iServiceTemplate = TxtUtil.read(rootPath + Constants.FILE_SEP + config.getiTemplate());
			serviceTemplate = TxtUtil.read(rootPath + Constants.FILE_SEP + config.getsTemplate());
		} catch (IOException e) {
			LogManager.error(e);
		}
	}
	
	public static String getModelUrl(String projectName) {
		StringBuilder sb = new StringBuilder(Constants.PACKAGE_HEAD);
		sb.append(Constants.PACKAGE_GAP).append(Constants.API).append(Constants.PACKAGE_GAP);
		sb.append(projectName).append(Constants.PACKAGE_GAP).append(Constants.MODEL);
		return sb.toString();
	}

	public static String getControllerUrl(String projectName) {
		StringBuilder sb = new StringBuilder(Constants.PACKAGE_HEAD);
		sb.append(Constants.PACKAGE_GAP).append(Constants.CONTROLLER).append(Constants.PACKAGE_GAP);
		sb.append(projectName).append(Constants.PACKAGE_GAP).append(Constants.CONTROLLER);
		return sb.toString();
	}

	public static String getServiceUrl(String projectName) {
		StringBuilder sb = new StringBuilder(Constants.PACKAGE_HEAD);
		sb.append(Constants.PACKAGE_GAP).append(Constants.IMPL).append(Constants.PACKAGE_GAP);
		sb.append(projectName).append(Constants.PACKAGE_GAP).append(Constants.SERVICE);
		return sb.toString();
	}

	public static String getIServiceUrl(String projectName) {
		StringBuilder sb = new StringBuilder(Constants.PACKAGE_HEAD);
		sb.append(Constants.PACKAGE_GAP).append(Constants.API).append(Constants.PACKAGE_GAP);
		sb.append(projectName).append(Constants.PACKAGE_GAP).append(Constants.SERVICE);
		return sb.toString();
	}
	
	public static String getControllerUrlByModelName(String modelName) {
		return getOtherNameByModelName(modelName, Constants.CONTROLLER, Constants.CONTROLLER);
	}
	public static String getServiceUrlByModelName(String modelName) {
		return getOtherNameByModelName(modelName, Constants.IMPL, Constants.SERVICE);
	}
	public static String getIServiceUrlByModelName(String modelName) {
		return getOtherNameByModelName(modelName, Constants.API, Constants.SERVICE);
	}

	private static String getOtherNameByModelName(String modelName, String packageName, String changedToName) {
		String apiStr = Constants.PACKAGE_GAP + Constants.API + Constants.PACKAGE_GAP;
		String modelStr = Constants.PACKAGE_GAP + Constants.MODEL + Constants.PACKAGE_GAP;
		if (modelName.contains(apiStr)) {
			modelName = modelName.replace(apiStr, Constants.PACKAGE_GAP + packageName + Constants.PACKAGE_GAP);
		}
		else throw new MyException("can not find this model's " + changedToName + " direction because no 'api'!");
		if (modelName.contains(modelStr)) {
			modelName = modelName.replace(modelStr, ".changedToName.");
		}
		else throw new MyException("can not find this model's " + changedToName +" direction because no 'model'!");
		return modelName;
	}
	
	public void writeControllerJava(String modelName) throws IOException {
		String txt = getJavaFromTemplate(modelName, this.controllerTemplate);
		TxtUtil.write(getControllerUrlByModelName(modelName), txt);
	}
	public void getIServiceTemplate(String modelName) throws IOException {
		String txt = getJavaFromTemplate(modelName, this.controllerTemplate);
		TxtUtil.write(getIServiceUrlByModelName(modelName), txt);
	}
	public void getServiceTemplate(String modelName) throws IOException {
		String txt = getJavaFromTemplate(modelName, this.controllerTemplate);
		TxtUtil.write(getServiceUrlByModelName(modelName), txt);
	}
	
	private String getJavaFromTemplate(String modelName, String template) {
//		CodeBean codeBean = new CodeBean(modelName);
//		template = template.replace(MODEL, codeBean.getModel().packageUrl);
//		template = template.replace(MODEL_NAME, codeBean.getModel().name);
//		template = template.replace(SERVICE, codeBean.getService().packageUrl);
//		template = template.replace(SERVICE_NAME, codeBean.getService().name);
//		template = template.replace(ISERVICE, codeBean.getiService().packageUrl);
//		template = template.replace(ISERVICE_NAME, codeBean.getiService().name);
//		return template;
		return null;
	}
}
