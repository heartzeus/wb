package com.tuhanbao.autotool.mvc.clazz;

import com.tuhanbao.autotool.mvc.J2EEProjectInfo;
import com.tuhanbao.autotool.mvc.J2EETable;
import com.tuhanbao.io.base.Constants;
import com.tuhanbao.io.impl.classUtil.ClassInfo;
import com.tuhanbao.io.impl.classUtil.IEnumType;
import com.tuhanbao.io.impl.classUtil.MethodInfo;
import com.tuhanbao.io.impl.classUtil.PackageEnum;
import com.tuhanbao.io.impl.codeUtil.Xls2CodeUtil;
import com.tuhanbao.io.impl.tableUtil.DataType;
import com.tuhanbao.io.impl.tableUtil.ImportColumn;
import com.tuhanbao.io.impl.tableUtil.Relation;
import com.tuhanbao.io.objutil.StringUtil;
import com.tuhanbao.util.db.table.data.BooleanValue;
import com.tuhanbao.util.util.clazz.ClazzUtil;

public class MOClazzCreator extends ClazzCreator {
    private static final String MO_CLASS = "com.tuhanbao.base.MetaObject";
    private static final String SERVICE_BEAN_CLASS = "com.tuhanbao.base.ServiceBean";
    private static final String MOCLASS_SUFFIX = "MO";
    
	public MOClazzCreator(J2EEProjectInfo project) {
		super(project);
	}

	public ClassInfo toClazz(J2EETable table) {
		ClassInfo classInfo = new ClassInfo();
        String modelName = table.getModelName();
        String tableName = table.getTableName();
        String className = modelName + MOCLASS_SUFFIX;
        classInfo.setName(className + " extends ServiceBean");
        classInfo.setPackageInfo(this.project.getServiceBeanPath(table.getModule()));
        classInfo.addImportInfo(SERVICE_BEAN_CLASS);
        classInfo.addImportInfo(this.project.getConstantsPath() + ".TableConstants");
        classInfo.addImportInfo(MO_CLASS);
        
        MethodInfo method = new MethodInfo();
        method.setName(className);
        method.setPe(PackageEnum.PROTECTED);
        method.setMethodBody("this(new MetaObject(TableConstants." + tableName + ".TABLE));");
        classInfo.addMethodInfo(method);
        
        method = new MethodInfo();
        method.setArgs("MetaObject mo");
        method.setName(className);
        method.setPe(PackageEnum.PROTECTED);
        method.setMethodBody("super(mo);");
        classInfo.addMethodInfo(method);
        
        boolean hasByteBuffer = false;

        for (ImportColumn col : table.getColumns())
        {
    		String importInfo = ClazzCreator.getImportFullPath(col.getDataType());
    		if (importInfo != null) classInfo.addImportInfo(importInfo);
        	
            String returnMethodType = col.getDataType().getDIYValue();
            classInfo.addImportInfo("com.tuhanbao.util.db.table.data." + returnMethodType);
            
            IEnumType enumInfo = col.getEnumInfo();
            if (enumInfo != null) {
            	String enumClassInfo = enumInfo.getFullClassName(this.project);
            	classInfo.addImportInfo(enumClassInfo);
            }
            method = new MethodInfo();
			method.setName(getGetName(col));
            method.setPe(PackageEnum.PUBLIC);
            method.setType(getColReturnType(col));
            StringBuilder methodBody = new StringBuilder();
            String methodType = col.getDataType().getDIYValue();
            methodBody.append(methodType).append(" value = (").append(methodType).append(")getValue(TableConstants.").append(tableName).append(".").append(col.getName()).append(");").append(Constants.ENTER);
            methodBody.append(Xls2CodeUtil.GAP2).append("if (value == null) return ").append(getDefaultValue(col, classInfo)).append(";").append(Constants.ENTER);
            if (enumInfo == null) methodBody.append(Xls2CodeUtil.GAP2).append("else return value.getValue();");
            else {
            	String enumName = enumInfo.getClassName();
            	methodBody.append(Xls2CodeUtil.GAP2).append("else return ").append(enumName).append(".get").append(enumName).append("(value.getValue());");
            }
            method.setMethodBody(methodBody.toString());
            classInfo.addMethodInfo(method);
            
            method = new MethodInfo();
            method.setName(getSetName(col));
            method.setPe(PackageEnum.PUBLIC);
            method.setType(Constants.VOID);
            method.setArgs(getColArgType(col) + " value");
            if (enumInfo == null) method.setMethodBody("setValue(TableConstants." + tableName + "." + col.getName() + ", " + methodType + ".valueOf(value));");
            else method.setMethodBody("setValue(TableConstants." + tableName + "." + col.getName() + ", " + methodType + ".valueOf(value.value));");
            classInfo.addMethodInfo(method);
            
            if (col.getFkColumn() != null)
            {
                ImportColumn fk = col.getFkColumn();
                String fkTableName = fk.getTable().getName();
        		if (fkTableName.startsWith("T_") || fkTableName.startsWith("I_")) {
        			fkTableName = fkTableName.substring(2);
        		}
                String fkModelName = ClazzCreator.getClassName(fkTableName);
                
                String name = getColClassName(col);
                
                Relation fkRT = col.getFkRT();
					
                boolean isSingle = fkRT == Relation.One2One || fkRT == Relation.N2One;
				classInfo.addMethodInfo(getGetMethod(col, fkModelName, name, classInfo, isSingle, false));
                classInfo.addMethodInfo(getRemoveMethod(col, fkModelName, name, classInfo, isSingle, false));
                classInfo.addMethodInfo(getSetMethod(col, fkModelName, name, classInfo, isSingle, false));
                
                classInfo.addImportInfo("java.util.List");
            }
            
            if (col.getDataType() == DataType.BYTEARRAY) hasByteBuffer = true;
        }
        
        for (ImportColumn col : table.getFKColumns()) {
            String fkTableName = col.getTable().getName();
    		if (fkTableName.startsWith("T_") || fkTableName.startsWith("I_")) {
    			fkTableName = fkTableName.substring(2);
    		}
            String fkModelName = ClazzCreator.getClassName(fkTableName);
            
            Relation fkRT = col.getFkRT();
            boolean isFkMySelf = col.getTable() == table.getTable();
            boolean isSingle = fkRT == Relation.One2One || fkRT == Relation.One2N;
			classInfo.addMethodInfo(getGetMethod(col, fkModelName, fkModelName, classInfo, isSingle, isFkMySelf));
            classInfo.addMethodInfo(getRemoveMethod(col, fkModelName, fkModelName, classInfo, isSingle, isFkMySelf));
            classInfo.addMethodInfo(getSetMethod(col, fkModelName, fkModelName, classInfo, isSingle, isFkMySelf));
            classInfo.addImportInfo("java.util.List");
        }
        
        if (hasByteBuffer)
        {
            classInfo.addImportInfo("com.tuhanbao.util.ByteBuffer");
        }
        
        return classInfo;
	}
	
	private static String getDefaultValue(ImportColumn col, ClassInfo classInfo) {
	    String defaultValue = col.getDefaultValue();
	    DataType dt = col.getDataType();
	    IEnumType iet = col.getEnumInfo();
	    //获取默认值
	    if (StringUtil.isEmpty(defaultValue)) {
	        if (iet != null) return "null";
	        if (dt == DataType.BOOLEAN) return "false";
	        if (dt == DataType.LONG || dt == DataType.FLOAT || dt == DataType.DOUBLE
	                || DataType.INT.getName().equals(dt.getName())) return "0";
	        if (dt == DataType.BIGDEECIMAL) return "BigDecimal.ZERO";
	        return "null";
	    }
	    else {
	        if (iet != null) {
	            String enumName = iet.getClassName();
                return enumName + ".get" + enumName + "(" + defaultValue + ")";
	        }
	        if (dt == DataType.BOOLEAN) return BooleanValue.valueOf(defaultValue).toString();
            if (dt == DataType.BIGDEECIMAL) return "new BigDecimal(" + defaultValue + ")";
            if (dt == DataType.LONG) {
                defaultValue = defaultValue.toUpperCase();
                if (!defaultValue.endsWith("L")) defaultValue += "L";
                return defaultValue;
            }
            if (DataType.STRING.getName().equals(dt.getName())) return "\"" + defaultValue + "\"";
            if (dt == DataType.DATE) {
                classInfo.addImportInfo("com.tuhanbao.io.objutil.TimeUtil");
                return "new Date(TimeUtil.getTime(\"" + defaultValue + "\"))";
            }
            return defaultValue;
	    }
	}

	private MethodInfo getGetMethod(ImportColumn col, String fkModelName, String name, ClassInfo classInfo, boolean isSingle, boolean fkIsMine) {
		MethodInfo method = new MethodInfo();
		String fkMySelfStr = fkIsMine ? ", true" : Constants.EMPTY;
		
		String methodName = "get" + name + (isSingle ? "" : "s");
		if (classInfo.constainsMethodName(methodName)) {
			methodName += "By" + getColClassName(col);
		}
		method.setName(methodName);
		method.setPe(PackageEnum.PUBLIC);
		StringBuilder methodBody = new StringBuilder();
		methodBody.append("List<? extends ServiceBean> result = this.getFKBean(TableConstants.");
		methodBody.append(col.getTable().name).append(".").append(col.getName()).append(fkMySelfStr + ");").append(Constants.ENTER);
		methodBody.append(gap2);
		if (isSingle) {
			method.setType(fkModelName);
			methodBody.append("return result == null || result.isEmpty() ? null : (" + fkModelName + ")result.get(0);");
		}
		else {
			method.addAnnotation("@SuppressWarnings(\"unchecked\")");
			method.setType("List<" + fkModelName + ">");
			methodBody.append("return (List<" + fkModelName + ">)result;");
		}
		method.setMethodBody(methodBody.toString());
		return method;
	}
	
	private MethodInfo getRemoveMethod(ImportColumn col, String fkModelName, String name, ClassInfo classInfo, boolean isSingle, boolean fkIsMine) {
		MethodInfo method = new MethodInfo();
		String fkMySelfStr = fkIsMine ? ", true" : Constants.EMPTY;
		String methodName = "remove" + name + (isSingle ? "" : "s");
		if (classInfo.constainsMethodName(methodName)) {
			methodName += "By" + getColClassName(col);
		}
		method.setName(methodName);
		method.setPe(PackageEnum.PUBLIC);
		StringBuilder methodBody = new StringBuilder();
		methodBody.append("List<? extends ServiceBean> result = this.removeFKBean(TableConstants.");
		methodBody.append(col.getTable().name).append(".").append(col.getName()).append(fkMySelfStr + ");").append(Constants.ENTER);
		methodBody.append(gap2);
		if (isSingle) {
			method.setType(fkModelName);
			methodBody.append("return result == null || result.isEmpty() ? null : (" + fkModelName + ")result.get(0);");
		}
		else {
			method.addAnnotation("@SuppressWarnings(\"unchecked\")");
			method.setType("List<" + fkModelName + ">");
			methodBody.append("return (List<" + fkModelName + ">)result;");
		}
		method.setMethodBody(methodBody.toString());
		return method;
	}
	
	private MethodInfo getSetMethod(ImportColumn col, String fkModelName, String name, ClassInfo classInfo, boolean isSingle, boolean fkIsMine) {
		MethodInfo method = new MethodInfo();
		String fkMySelfStr = fkIsMine ? ", true" : Constants.EMPTY;
		String methodName = "set" + name + (isSingle ? "" : "s");
		if (classInfo.constainsMethodName(methodName)) {
			methodName += "By" + getColClassName(col);
		}
		method.setName(methodName);
		method.setPe(PackageEnum.PUBLIC);
		method.setArgs(fkModelName + " value");
		method.setType("void");
		StringBuilder methodBody = new StringBuilder();
		if (isSingle) {
			method.setArgs(fkModelName + " value");
		}
		else {
			method.setArgs("List<" + fkModelName + ">" + " value");
		}
		
		methodBody.append("this.setFKBean(TableConstants." + col.getTable().name + "." + col.getName()
				+ ", value").append(fkMySelfStr + ");");
		method.setMethodBody(methodBody.toString());
		return method;
	}

	private String getGetName(ImportColumn col) {
		String className = ClazzUtil.getClassName(col.getName());
		if (col.getDataType() == DataType.BOOLEAN) {
			if (className.startsWith("Is")) {
				return ClazzUtil.firstCharLowerCase(className);
			}
		}
		return "get" + ClazzUtil.getClassName(col.getName());
	}

	private String getSetName(ImportColumn col) {
		String className = ClazzUtil.getClassName(col.getName());
//		if (col.getDataType() == DataType.BOOLEAN) {
//			if (className.startsWith("Is")) {
//				className = className.substring(2);
//			}
//		}
		return "set" + className;
	}

	private String getColClassName(ImportColumn col) {
		String name = col.getName().toLowerCase();
		//外键命名必须规范
		if (name.endsWith("_id")) name = name.substring(0, name.length() - 3);
		name = ClazzUtil.getClassName(name);
		return name;
	}
	
	private static String getColReturnType(ImportColumn col) {
		if (col.getEnumInfo() != null) {
			return col.getEnumInfo().getClassName();
		}
		return col.getDataType().getName();
	}

	private static String getColArgType(ImportColumn col) {
		if (col.getEnumInfo() != null) {
			return col.getEnumInfo().getClassName();
		}
		return col.getDataType().getBigName();
	}
}
