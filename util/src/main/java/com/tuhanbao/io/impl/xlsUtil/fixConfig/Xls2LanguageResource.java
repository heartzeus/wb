package com.tuhanbao.io.impl.xlsUtil.fixConfig;

import java.io.IOException;

import com.tuhanbao.io.base.Constants;
import com.tuhanbao.io.impl.ProjectInfo;
import com.tuhanbao.io.impl.codeUtil.Xls2CodeUtil;
import com.tuhanbao.io.impl.xlsUtil.fixConfig.Xls2Config;

/**
 * @author tuhanbao
 *
 */
/**
 * @author tuhanbao
 *
 */
/**
 * @author tuhanbao
 *
 */
public class Xls2LanguageResource extends Xls2Config
{
    public static final String ERRORCODE_URL = "com/threetorch/service/base/constant/ErrorCode.java";
    
    
    public String[] getConfigStrAndCode(ProjectInfo project, String[][] arrays) throws IOException
    {
    	return getLanguageResource(project, arrays);
    }
    
    private String[] getLanguageResource(ProjectInfo project, String[][] arrays)
    {
        StringBuilder sb = new StringBuilder();
        StringBuilder configStr = new StringBuilder();
        
        sb.append("package " + project.getConstantsPath() + ";").append(Constants.ENTER);
        sb.append(Constants.ENTER);
        sb.append("public class LanguageResource").append(Constants.BLANK);
        sb.append(Constants.LEFT_BRACE).append(Constants.ENTER);
        
        for (String[] array : arrays)
        {
            if (Xls2CodeUtil.isEmptyLine(array)) continue;
            
            String name = Xls2CodeUtil.getString(array, 0);
            String value = Xls2CodeUtil.getString(array, 1);

            if (name == null || value == null) continue;
            
            name = name.toUpperCase();
			sb.append(Xls2CodeUtil.GAP1).append("public static final String ").append(name).append(" = \"")
                .append(name).append("\";").append(Constants.ENTER).append(Constants.ENTER);
			configStr.append(name).append(Constants.EQUAL).append(value).append(Constants.ENTER);
        }
        sb.append(Constants.RIGHT_BRACE);
        
        return new String[]{sb.toString(), configStr.toString()};
    }
}
