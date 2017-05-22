package com.tuhanbao.autotool.mvc.clazz;

import com.tuhanbao.autotool.mvc.J2EEProjectInfo;
import com.tuhanbao.autotool.mvc.J2EETable;
import com.tuhanbao.io.impl.classUtil.ClassInfo;
import com.tuhanbao.io.impl.tableUtil.ColumnEntry;
import com.tuhanbao.io.impl.tableUtil.DataType;
import com.tuhanbao.io.impl.tableUtil.ImportColumn;
import com.tuhanbao.io.impl.tableUtil.Relation;
import com.tuhanbao.io.objutil.StringUtil;

public abstract class ClazzCreator {
    protected static final String gap = "    ";
    protected static final String gap2 = gap + gap;
	
	protected J2EEProjectInfo project;
	
	public ClazzCreator(J2EEProjectInfo project) {
		this.project = project;
	}
	
	public abstract ClassInfo toClazz(J2EETable table);
	
    private static String str2ClassName(String s)
    {
        if (s == null) return s;
        s = s.toLowerCase();
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
    
	public static String getClassName(String name) {
		String[] names = StringUtil.string2Array(name, "_");
		//将names转换成驼峰
		StringBuilder sb = new StringBuilder();
		for (String n : names) {
			sb.append(ClazzCreator.str2ClassName(n));
		}
		return sb.toString();
	}
	
	public static String getVarName(String name) {
		String[] names = StringUtil.string2Array(name, "_");
		//将names转换成驼峰
		StringBuilder sb = new StringBuilder();
		for (String n : names) {
			if (!StringUtil.isEmpty(n))
				sb.append(ClazzCreator.str2ClassName(n));
		}
		String s = sb.toString();
		return s.substring(0, 1).toLowerCase() + s.substring(1);
	}
	
	public static String getSimpleVarName(String name) {
		String[] names = StringUtil.string2Array(name, "_");
		//将names转换成驼峰
		StringBuilder sb = new StringBuilder();
		for (String n : names) {
			if (!StringUtil.isEmpty(n))
			 sb.append(n.substring(0, 1).toUpperCase());
		}
		return sb.toString();
	}
	
    public static String firstCharUpper(String s)
    {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
	
	public static String getDataTypeFullPath(DataType dataType) {
		if (dataType == DataType.BIGDEECIMAL) return "java.math.BigDecimal";
        else if (dataType == DataType.DATE) return "java.util.Date";
        else return "java.lang." + dataType.getBigName();
	}
	
	public static String getImportFullPath(DataType dataType) {
		if (dataType == DataType.BIGDEECIMAL) return "java.math.BigDecimal";
        else if (dataType == DataType.DATE) return "java.util.Date";
        else return null;
	}

	protected static void addImportByDataType(ClassInfo clazz, DataType dataType) {
		String importInfo = getImportFullPath(dataType);
		if (importInfo != null) clazz.addImportInfo(importInfo);
	}
	
	public static String getMethodSuffix(ColumnEntry[] entrys) {
		if (entrys.length == 1) {
			return firstCharUpper(getVarName(entrys[0].getCol().getName()));
		}
		else {
			int length = entrys.length;
			ImportColumn col = entrys[0].getCol();
			String name = getSimpleVarName(col.getName());
			for (int i = 1; i < length ; i++) {
				col = entrys[i].getCol();
				String varName = getSimpleVarName(col.getName());
				name += "And" + varName;
			}
			return name;
		}
	}
	
	public static boolean isResultList(ColumnEntry[] entrys) {
		for (ColumnEntry entry : entrys) {
			Relation relation = entry.getRelation();
			if (relation == Relation.One2One || relation == Relation.N2One) {
				return false;
			}
		}
		return true;
	}
}
