package com.tuhanbao.io.impl;

import com.tuhanbao.io.base.Constants;
import com.tuhanbao.io.objutil.FileUtil;

public class ProjectInfo4MemCache extends ProjectInfo
{
	private static final String PROJECT_HEAD = "com.hhbao.";
	
    public ProjectInfo4MemCache(String srcPath, String projectName) {
    	super(srcPath, projectName);
    }
    
    public String getServiceBeanPath()
    {
        return FileUtil.appendStr(".", PROJECT_HEAD, "service.cms.bean");
    }
    
    public String getFullServiceBeanUrl()
    {
    	return FileUtil.appendPath(srcPath, (getServiceBeanPath()).replace(".", Constants.FILE_SEP));
    }
    
    public String getBaseBeanPath()
    {
        return FileUtil.appendStr(".", PROJECT_HEAD, "base.cms.bean");
    }
    
    public String getFullBaseBeanUrl()
    {
    	return FileUtil.appendPath(srcPath, (getBaseBeanPath()).replace(".", Constants.FILE_SEP));
    }
    
    public String getSqlUrl()
    {
        return FileUtil.appendPath(srcPath, "init/sql/init.sql");
    }

	@Override
	public String getConstantsPath() {
		return FileUtil.appendStr(".", PROJECT_HEAD, "base.constants");
	}

	@Override
	public String getEnumUrl() {
		return FileUtil.appendStr(".", getConstantsPath(), "enums");
	}

	@Override
	public String getFullConstantsUrl() {
		 return FileUtil.appendPath(srcPath, (getConstantsPath()).replace(".", Constants.FILE_SEP));
	}

	@Override
	public String getFullEnumUrl() {
		return FileUtil.appendPath(srcPath, (getEnumUrl()).replace(".", Constants.FILE_SEP));
	}
}
