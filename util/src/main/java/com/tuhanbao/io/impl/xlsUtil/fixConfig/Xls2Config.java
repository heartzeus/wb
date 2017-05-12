package com.tuhanbao.io.impl.xlsUtil.fixConfig;

import java.io.IOException;

import com.tuhanbao.io.impl.CodeType;
import com.tuhanbao.io.impl.ProjectInfo;
import com.tuhanbao.io.impl.codeUtil.CodeUtilManager;

public class Xls2Config
{
    public String getConfigStr(ProjectInfo project, String[][] arrays) throws IOException
    {
        return CodeUtilManager.getCodeUtil(CodeType.JAVA).xls2Config(arrays);
    }
}
