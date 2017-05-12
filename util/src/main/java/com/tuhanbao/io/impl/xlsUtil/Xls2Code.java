package com.tuhanbao.io.impl.xlsUtil;

import java.io.IOException;
import java.util.List;

import com.tuhanbao.io.impl.ProjectInfo;
import com.tuhanbao.io.impl.classUtil.ClassInfo;

public interface Xls2Code
{
    public List<ClassInfo> getClassInfos(ProjectInfo project, String[][] arrays) throws IOException;
}
