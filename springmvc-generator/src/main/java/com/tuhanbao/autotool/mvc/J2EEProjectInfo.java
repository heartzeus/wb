package com.tuhanbao.autotool.mvc;
import com.tuhanbao.Constants;
import com.tuhanbao.io.impl.ProjectInfo;
import com.tuhanbao.io.objutil.FileUtil;
import com.tuhanbao.io.objutil.StringUtil;


public class J2EEProjectInfo extends ProjectInfo {
	
	private String modelUrl, constantsUrl, enumUrl, mapperUrl, serviceUrl, iServiceUrl, controllerUrl;
	
	private String resUrl, jspUrl, jsUrl, configUrl;
	
	public String apiUrl, implUrl;
	
	private String projectUrl;
	
	private static final String MAVEN_SRC = "src/main/java";

	private static final String MAVEN_RES = "src/main/resources";

	private static final String JSP = "src/main/webapp/jsp";
	
	private static final String JS = "src/main/webapp/static/myjs";
	
	private String packageHead;
	
	private String pagePath;
	
	private String filterName;
	
	private String rootPath;
	
	public J2EEProjectInfo(String rootPath, String projectName) {
	    this(rootPath, projectName, Constants.PACKAGE_HEAD);
	}
	
	public J2EEProjectInfo(String rootPath, String projectName, String packageHead) {
		super(FileUtil.appendPath(rootPath, MAVEN_SRC), projectName);
		this.rootPath = rootPath;
		this.packageHead = packageHead;
		this.pagePath = packageHead + ".db.page.Page" ;
		this.filterName = packageHead + ".filter.Filter";
		this.projectUrl = rootPath;
		this.resUrl = MAVEN_RES;
		this.configUrl = FileUtil.appendPath(resUrl, Constants.CONFIG_URL);
		this.jspUrl = FileUtil.appendPath(rootPath, JSP);
		this.jsUrl = FileUtil.appendPath(rootPath, JS);
		apiUrl = packageHead + Constants.STOP_EN + Constants.API + Constants.STOP_EN + projectName;
		implUrl = packageHead + Constants.STOP_EN + Constants.IMPL + Constants.STOP_EN + projectName;
		controllerUrl = packageHead + Constants.STOP_EN + Constants.CONTROLLER + Constants.STOP_EN + projectName;
		
		modelUrl = apiUrl + Constants.STOP_EN + Constants.MODEL;
		constantsUrl = apiUrl + Constants.STOP_EN + Constants.CONSTANTS;
		enumUrl = constantsUrl + Constants.STOP_EN + Constants.ENUMS;
		mapperUrl = implUrl + Constants.STOP_EN + Constants.MAPPER;
		serviceUrl = implUrl + Constants.STOP_EN + Constants.SERVICE;
		iServiceUrl = apiUrl + Constants.STOP_EN + Constants.SERVICE;
		controllerUrl = controllerUrl + Constants.STOP_EN + Constants.CONTROLLER;
	}

    public String getServiceBeanPath(String module) {
		if (StringUtil.isEmpty(module))
			return modelUrl;
		else
			return modelUrl + Constants.STOP_EN + module;
	}
	
	public String getFullModelUrl(String module) {
		if (StringUtil.isEmpty(module))
			return srcPath + Constants.FILE_SEP + modelUrl.replace(Constants.STOP_EN, Constants.FILE_SEP)
			 	+ Constants.FILE_SEP;
		else 
			return srcPath + Constants.FILE_SEP + modelUrl.replace(Constants.STOP_EN, Constants.FILE_SEP)
				 + Constants.FILE_SEP + module.replace(".", Constants.FILE_SEP) + Constants.FILE_SEP;
	}

    @Override
	public String getConstantsPath() {
		return constantsUrl;
	}
	
    @Override
	public String getFullConstantsUrl() {
		return srcPath + Constants.FILE_SEP + constantsUrl.replace(Constants.STOP_EN, Constants.FILE_SEP);
	}

    @Override
	public String getEnumUrl() {
		return enumUrl;
	}
	
    @Override
	public String getFullEnumUrl() {
		return srcPath + Constants.FILE_SEP + enumUrl.replace(Constants.STOP_EN, Constants.FILE_SEP);
	}
    
    public String getMapperPath(String module) {
    	if (StringUtil.isEmpty(module))
    		return mapperUrl;
    	else
    		return mapperUrl + Constants.STOP_EN + module;
    }

    public String getFullMapperUrl(String module) {
    	if (StringUtil.isEmpty(module))
    		return srcPath + Constants.FILE_SEP + mapperUrl.replace(Constants.STOP_EN, Constants.FILE_SEP)
    			+ Constants.FILE_SEP;
    	else 
    		return srcPath + Constants.FILE_SEP + mapperUrl.replace(Constants.STOP_EN, Constants.FILE_SEP)
    			+ Constants.FILE_SEP + module.replace(".", Constants.FILE_SEP) + Constants.FILE_SEP;
    }
    
    public String getServicePath(String module) {
    	if (StringUtil.isEmpty(module)) return serviceUrl;
    	else return serviceUrl + Constants.STOP_EN + module;
    }
    
    public String getFullServiceUrl(String module) {
    	if (StringUtil.isEmpty(module))
    		return srcPath + Constants.FILE_SEP + serviceUrl.replace(Constants.STOP_EN, Constants.FILE_SEP)
    			+ Constants.FILE_SEP;
    	else
    		return srcPath + Constants.FILE_SEP + serviceUrl.replace(Constants.STOP_EN, Constants.FILE_SEP)
    			+ Constants.FILE_SEP + module.replace(".", Constants.FILE_SEP) + Constants.FILE_SEP;
    }
    
    public String getIServicePath(String module) {
    	if (StringUtil.isEmpty(module)) return iServiceUrl;
    	else return iServiceUrl + Constants.STOP_EN + module;
    }
    
    public String getFullIServiceUrl(String module) {
    	if (StringUtil.isEmpty(module))
    		return srcPath + Constants.FILE_SEP + iServiceUrl.replace(Constants.STOP_EN, Constants.FILE_SEP)
    			+ Constants.FILE_SEP;
    	else
    		return srcPath + Constants.FILE_SEP + iServiceUrl.replace(Constants.STOP_EN, Constants.FILE_SEP)
    			+ Constants.FILE_SEP + module.replace(".", Constants.FILE_SEP) + Constants.FILE_SEP;
    }
    
    public String getResUrl() {
    	return resUrl;
    }
    
    public String getJSPUrl(String module) {
    	return FileUtil.appendPath(jspUrl, module);
    }
    
    public String getJSUrl(String module) {
    	return FileUtil.appendPath(jsUrl, module);
    }
    
    public String getControllerPath(String module) {
		if (StringUtil.isEmpty(module))
			return controllerUrl;
		else
			return controllerUrl + Constants.STOP_EN + module;
	}
    
    public String getImplPath() {
    	return implUrl;
    }
    
    public String getProjectUrl() {
    	return this.projectUrl;
    }

	public String getConfigPath() {
		return this.configUrl;
	}

    public String getPackageHead() {
        return packageHead;
    }

    public String getPagePath() {
        return pagePath;
    }

    public String getFilterName() {
        return filterName;
    }
    
    public String getRootPath() {
        return rootPath;
    }
}
