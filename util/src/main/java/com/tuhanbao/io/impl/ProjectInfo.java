package com.tuhanbao.io.impl;

public abstract class ProjectInfo
{
	//代码根路径（定位到src路径下）
    protected String srcPath;
    
    //项目名称
    protected String projectName;

    public ProjectInfo(String rootPath, String projectName) {
    	this.srcPath = rootPath;
    	this.projectName = projectName;
    }

    public String getSrcPath() {
		return srcPath;
	}

	public String getProjectName() {
		return projectName;
	}

	/**
     * 返回常量类的java path
     * @return
     */
	public abstract String getConstantsPath();
	
	/**
	 * 返回常量类的全路径
	 * @return
	 */
	public abstract String getFullConstantsUrl();

	/**
	 * 返回枚举类的java path
	 * @return
	 */
	public abstract String getEnumUrl();
	
	/**
	 * 返回枚举类的全路径
	 * @return
	 */
	public abstract String getFullEnumUrl();
}
