package com.tuhanbao.io.impl.classUtil;

import com.tuhanbao.io.impl.ProjectInfo;



public class EnumClassInfo extends ClassInfo implements IEnumType
{
    private int type;
    
    public static final int INT = 0, STRING = 1;

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

	@Override
	public String getFullClassName(ProjectInfo projectInfo) {
		return projectInfo.getConstantsUrl() + ".enums." + this.getClassName();
	}
}
