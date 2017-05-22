package com.tuhanbao.io.impl.xlsUtil.fixConfig;

import java.util.ArrayList;
import java.util.List;

import com.tuhanbao.io.base.Constants;
import com.tuhanbao.io.impl.ProjectInfo;
import com.tuhanbao.io.impl.classUtil.ClassInfo;
import com.tuhanbao.io.impl.classUtil.NotesInfo;
import com.tuhanbao.io.impl.classUtil.PackageEnum;
import com.tuhanbao.io.impl.classUtil.VarInfo;
import com.tuhanbao.io.impl.codeUtil.Xls2CodeUtil;
import com.tuhanbao.io.impl.xlsUtil.Xls2Code;

public class Xls2Constants implements Xls2Code
{
    public List<ClassInfo> getClassInfos(ProjectInfo project, String[][] arrays)
    { 
    	List<ClassInfo> classInfos = new ArrayList<ClassInfo>();
    	ClassInfo clazzInfo = new ClassInfo();
    	clazzInfo.setPackageInfo(project.getConstantsUrl());
    	clazzInfo.setName("ConfigConstants");
        for (String[] array : arrays)
        {
            if (Xls2CodeUtil.isEmptyLine(array)) continue;
            
            String name = Xls2CodeUtil.getString(array, 0);
            String value = Xls2CodeUtil.getString(array, 1);
            if (name == null || value == null) continue;
            String desc = Xls2CodeUtil.getString(array, 2);
            
            VarInfo varInfo = new VarInfo();
            if (desc != null && !desc.isEmpty())
            {
            	NotesInfo notesInfo = new NotesInfo(1);
            	notesInfo.addNote(desc);
            	varInfo.setNote(notesInfo);
            }
            varInfo.setFinal(true);
            varInfo.setStatic(true);
            varInfo.setPe(PackageEnum.PUBLIC);
            varInfo.setName(name.toUpperCase());
            if (value.matches("^[-]{0,1}[0-9]+$"))
            {
            	varInfo.setType("int");
            	varInfo.setValue(value);
            }
            else
            {
            	varInfo.setType("String");
            	varInfo.setValue("\"" + value + "\"");
            }
            clazzInfo.addVarInfo(varInfo);
        }
        classInfos.add(clazzInfo);
        return classInfos;
    }

    
    public String getConstantsClassInfoClient(String[][] arrays)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("public class Constants").append(Constants.BLANK);
        sb.append(Constants.LEFT_BRACE).append(Constants.ENTER);
        for (String[] array : arrays)
        {
            if (Xls2CodeUtil.isEmptyLine(array)) continue;
            
            String name = Xls2CodeUtil.getString(array, 0);
            String value = Xls2CodeUtil.getString(array, 1);
            if (name == null || value == null) continue;
            String desc = Xls2CodeUtil.getString(array, 2);
            if (desc != null && !desc.isEmpty())
            {
                sb.append(Constants.GAP1).append("//").append(desc).append(Constants.ENTER);
            }
            if (value.matches("^[-]{0,1}[0-9]+$"))
            {
                sb.append(Constants.GAP1).append("public const int ").append(name.toUpperCase()).append(" = ")
                    .append(value).append(";").append(Constants.ENTER).append(Constants.ENTER);
            }
            else
            {
                sb.append(Constants.GAP1).append("public const string ").append(name.toUpperCase()).append(" = \"")
                    .append(value).append("\";").append(Constants.ENTER).append(Constants.ENTER);
            }
        }
        
        sb.append(Constants.RIGHT_BRACE);
        return sb.toString();
    }
}
