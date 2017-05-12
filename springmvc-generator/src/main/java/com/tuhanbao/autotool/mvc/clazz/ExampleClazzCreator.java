package com.tuhanbao.autotool.mvc.clazz;

import com.tuhanbao.Constants;
import com.tuhanbao.autotool.mvc.J2EEProjectInfo;
import com.tuhanbao.autotool.mvc.J2EETable;
import com.tuhanbao.io.impl.classUtil.ClassInfo;
import com.tuhanbao.io.impl.classUtil.MethodInfo;
import com.tuhanbao.io.impl.classUtil.PackageEnum;
import com.tuhanbao.io.impl.classUtil.VarInfo;
import com.tuhanbao.io.impl.tableUtil.DataType;
import com.tuhanbao.io.impl.tableUtil.ImportColumn;

@Deprecated
public class ExampleClazzCreator extends ClazzCreator {
	public ExampleClazzCreator(J2EEProjectInfo project) {
		super(project);
	}

	public ClassInfo toClazz(J2EETable table) {
		ClassInfo clazz = new ClassInfo();
		clazz.setName(table.getModelName() + "Example");
		clazz.setPackageInfo(project.getServiceBeanPath(table.getModule()));
		clazz.addImportInfo("java.util.ArrayList");
		clazz.addImportInfo("java.util.List");
		clazz.addImportInfo("com.hhnz.mapper.GeneratedCriteria");
		
		VarInfo varInfo = new VarInfo();
		varInfo.setPe(PackageEnum.PROTECTED);
		varInfo.setType("String");
		varInfo.setName("orderByClause");
		clazz.addVarInfo(varInfo);
		
		varInfo = new VarInfo();
		varInfo.setPe(PackageEnum.PROTECTED);
		varInfo.setType("boolean");
		varInfo.setName("distinct");
		clazz.addVarInfo(varInfo);

		varInfo = new VarInfo();
		varInfo.setPe(PackageEnum.PROTECTED);
		varInfo.setType("List<Criteria>");
		varInfo.setName("oredCriteria");
		clazz.addVarInfo(varInfo);
		
		MethodInfo methodInfo = new MethodInfo();
		methodInfo.setPe(PackageEnum.PUBLIC);
		methodInfo.setName(table.getExampleName());
		methodInfo.setMethodBody("oredCriteria = new ArrayList<Criteria>();");
		clazz.addMethodInfo(methodInfo);

		methodInfo = new MethodInfo();
		methodInfo.setPe(PackageEnum.PUBLIC);
		methodInfo.setType(Constants.VOID);
		methodInfo.setName("setOrderByClause");
		methodInfo.setArgs("String orderByClause");
		methodInfo.setMethodBody("this.orderByClause = orderByClause;");
		clazz.addMethodInfo(methodInfo);

		methodInfo = new MethodInfo();
		methodInfo.setPe(PackageEnum.PUBLIC);
		methodInfo.setType("String");
		methodInfo.setName("getOrderByClause");
		methodInfo.setMethodBody("return orderByClause;");
		clazz.addMethodInfo(methodInfo);
		
		methodInfo = new MethodInfo();
		methodInfo.setPe(PackageEnum.PUBLIC);
		methodInfo.setType(Constants.VOID);
		methodInfo.setName("setDistinct");
		methodInfo.setArgs("boolean distinct");
		methodInfo.setMethodBody("this.distinct = distinct;");
		clazz.addMethodInfo(methodInfo);

		methodInfo = new MethodInfo();
		methodInfo.setPe(PackageEnum.PUBLIC);
		methodInfo.setType("boolean");
		methodInfo.setName("isDistinct");
		methodInfo.setMethodBody("return distinct;");
		clazz.addMethodInfo(methodInfo);
		
		methodInfo = new MethodInfo();
		methodInfo.setPe(PackageEnum.PUBLIC);
		methodInfo.setType("List<Criteria>");
		methodInfo.setName("getOredCriteria");
		methodInfo.setMethodBody("return oredCriteria;");
		clazz.addMethodInfo(methodInfo);
		
		methodInfo = new MethodInfo();
		methodInfo.setPe(PackageEnum.PUBLIC);
		methodInfo.setType(Constants.VOID);
		methodInfo.setName("or");
		methodInfo.setArgs("Criteria criteria");
		methodInfo.setMethodBody("oredCriteria.add(criteria);");
		clazz.addMethodInfo(methodInfo);

		methodInfo = new MethodInfo();
		methodInfo.setPe(PackageEnum.PUBLIC);
		methodInfo.setType(Constants.CRITERIA);
		methodInfo.setName("or");
		StringBuilder methodBody = new StringBuilder();
		methodBody.append("Criteria criteria = createCriteriaInternal();").append(Constants.ENTER).append(Constants.TAB_SPACE).append(Constants.TAB_SPACE);
		methodBody.append("oredCriteria.add(criteria);").append(Constants.ENTER).append(Constants.TAB_SPACE).append(Constants.TAB_SPACE);
		methodBody.append("return criteria;");
		methodInfo.setMethodBody(methodBody.toString());
		clazz.addMethodInfo(methodInfo);

		methodInfo = new MethodInfo();
		methodInfo.setPe(PackageEnum.PUBLIC);
		methodInfo.setType(Constants.CRITERIA);
		methodInfo.setName("createCriteria");
		methodBody = new StringBuilder();
		methodBody.append("Criteria criteria = createCriteriaInternal();").append(Constants.ENTER).append(Constants.TAB_SPACE).append(Constants.TAB_SPACE);
		methodBody.append("if (oredCriteria.size() == 0) {").append(Constants.ENTER).append(Constants.TAB_SPACE).append(Constants.TAB_SPACE);
		methodBody.append(Constants.TAB_SPACE).append("oredCriteria.add(criteria);").append(Constants.ENTER).append(Constants.TAB_SPACE).append(Constants.TAB_SPACE);
		methodBody.append("}").append(Constants.ENTER).append(Constants.TAB_SPACE).append(Constants.TAB_SPACE);
		methodBody.append("return criteria;");
		methodInfo.setMethodBody(methodBody.toString());
		clazz.addMethodInfo(methodInfo);

		methodInfo = new MethodInfo();
		methodInfo.setPe(PackageEnum.PROTECTED);
		methodInfo.setType("Criteria");
		methodInfo.setName("createCriteriaInternal");
		methodBody = new StringBuilder();
		methodBody.append("Criteria criteria = new Criteria();").append(Constants.ENTER).append(Constants.TAB_SPACE).append(Constants.TAB_SPACE);
		methodBody.append("return criteria;");
		methodInfo.setMethodBody(methodBody.toString());
		clazz.addMethodInfo(methodInfo);

		methodInfo = new MethodInfo();
		methodInfo.setPe(PackageEnum.PUBLIC);
		methodInfo.setType(Constants.VOID);
		methodInfo.setName("clear");
		methodBody = new StringBuilder();
		methodBody.append("oredCriteria.clear();").append(Constants.ENTER).append(Constants.TAB_SPACE).append(Constants.TAB_SPACE);
		methodBody.append("orderByClause = null;").append(Constants.ENTER).append(Constants.TAB_SPACE).append(Constants.TAB_SPACE);
		methodBody.append("distinct = false;");
		methodInfo.setMethodBody(methodBody.toString());
		clazz.addMethodInfo(methodInfo);
		
		if (table.needCutPage()) {
			varInfo = new VarInfo();
			varInfo.setPe(PackageEnum.PROTECTED);
			varInfo.setType("Page");
			varInfo.setName("page");
			clazz.addVarInfo(varInfo);
			clazz.addImportInfo(project.getPagePath());
			
			methodInfo = new MethodInfo();
			methodInfo.setPe(PackageEnum.PUBLIC);
			methodInfo.setName("setPage");
			methodInfo.setType("void");
			methodInfo.setArgs("Page page");
			methodInfo.setMethodBody("this.page=page;");
			clazz.addMethodInfo(methodInfo);
			
			methodInfo = new MethodInfo();
			methodInfo.setPe(PackageEnum.PUBLIC);
			methodInfo.setName("getPage");
			methodInfo.setType("Page");
			methodInfo.setMethodBody("return page;");
			clazz.addMethodInfo(methodInfo);
		}
		
		clazz.addInnerClass(getCriteriaClazz(clazz, table, project));
		return clazz;
	}
	
	private ClassInfo getCriteriaClazz(ClassInfo parentClazz, J2EETable table, J2EEProjectInfo project) {
		ClassInfo clazz = new ClassInfo();
		clazz.setStatic(true);
		clazz.setName(Constants.CRITERIA + " extends GeneratedCriteria");
		
		//构造方法
		MethodInfo methodInfo = new MethodInfo();
		methodInfo.setPe(PackageEnum.PUBLIC);
		methodInfo.setName(Constants.CRITERIA);
		methodInfo.setMethodBody("super();");
		clazz.addMethodInfo(methodInfo);
		StringBuilder methodBody;
		for (ImportColumn col : table.getColumns()) {
			String colName = col.getName();
			DataType dataType = col.getDataType();
			String type = dataType.getName();
			String varName = ClazzCreator.getVarName(col.getName());
			
			//这三个需要加import
			addImportByDataType(parentClazz, dataType);
			
			methodInfo = new MethodInfo();
			methodInfo.setPe(PackageEnum.PUBLIC);
			methodInfo.setType(Constants.CRITERIA);
			methodInfo.setName("and" + getClassName(colName) + "IsNull");
			methodBody = new StringBuilder();
			methodBody.append("addCriterion(\"").append(colName).append(" is null\");").append(Constants.ENTER).append(Constants.TAB_SPACE).append(Constants.TAB_SPACE);
			methodBody.append("return this;");
			methodInfo.setMethodBody(methodBody.toString());
			clazz.addMethodInfo(methodInfo);

			methodInfo = new MethodInfo();
			methodInfo.setPe(PackageEnum.PUBLIC);
			methodInfo.setType(Constants.CRITERIA);
			methodInfo.setName("and" + getClassName(colName) + "IsNotNull");
			methodBody = new StringBuilder();
			methodBody.append("addCriterion(\"").append(colName).append(" is not null\");").append(Constants.ENTER).append(Constants.TAB_SPACE).append(Constants.TAB_SPACE);
			methodBody.append("return this;");
			methodInfo.setMethodBody(methodBody.toString());
			clazz.addMethodInfo(methodInfo);

			methodInfo = new MethodInfo();
			methodInfo.setPe(PackageEnum.PUBLIC);
			methodInfo.setType(Constants.CRITERIA);
			methodInfo.setName("and" + getClassName(colName) + "EqualTo");
			methodInfo.setArgs(type + " value");
			methodBody = new StringBuilder();
			methodBody.append("addCriterion(\"").append(colName).append(" =\", value, \"")
					.append(varName).append("\");").append(Constants.ENTER).append(Constants.TAB_SPACE).append(Constants.TAB_SPACE);
			methodBody.append("return this;");
			methodInfo.setMethodBody(methodBody.toString());
			clazz.addMethodInfo(methodInfo);

			methodInfo = new MethodInfo();
			methodInfo.setPe(PackageEnum.PUBLIC);
			methodInfo.setType(Constants.CRITERIA);
			methodInfo.setName("and" + getClassName(colName) + "NotEqualTo");
			methodInfo.setArgs(type + " value");
			methodBody = new StringBuilder();
			methodBody.append("addCriterion(\"").append(colName).append(" <>\", value, \"")
				.append(varName).append("\");").append(Constants.ENTER).append(Constants.TAB_SPACE).append(Constants.TAB_SPACE);
			methodBody.append("return this;");
			methodInfo.setMethodBody(methodBody.toString());
			clazz.addMethodInfo(methodInfo);

			methodInfo = new MethodInfo();
			methodInfo.setPe(PackageEnum.PUBLIC);
			methodInfo.setType(Constants.CRITERIA);
			methodInfo.setName("and" + getClassName(colName) + "GreaterThan");
			methodInfo.setArgs(type + " value");
			methodBody = new StringBuilder();
			methodBody.append("addCriterion(\"").append(colName).append(" >\", value, \"")
				.append(varName).append("\");").append(Constants.ENTER).append(Constants.TAB_SPACE).append(Constants.TAB_SPACE);
			methodBody.append("return this;");
			methodInfo.setMethodBody(methodBody.toString());
			clazz.addMethodInfo(methodInfo);

			methodInfo = new MethodInfo();
			methodInfo.setPe(PackageEnum.PUBLIC);
			methodInfo.setType(Constants.CRITERIA);
			methodInfo.setName("and" + getClassName(colName) + "GreaterThanOrEqualTo");
			methodInfo.setArgs(type + " value");
			methodBody = new StringBuilder();
			methodBody.append("addCriterion(\"").append(colName).append(" >=\", value, \"")
				.append(varName).append("\");").append(Constants.ENTER).append(Constants.TAB_SPACE).append(Constants.TAB_SPACE);
			methodBody.append("return this;");
			methodInfo.setMethodBody(methodBody.toString());
			clazz.addMethodInfo(methodInfo);

			methodInfo = new MethodInfo();
			methodInfo.setPe(PackageEnum.PUBLIC);
			methodInfo.setType(Constants.CRITERIA);
			methodInfo.setName("and" + getClassName(colName) + "LessThan");
			methodInfo.setArgs(type + " value");
			methodBody = new StringBuilder();
			methodBody.append("addCriterion(\"").append(colName).append(" <\", value, \"")
				.append(varName).append("\");").append(Constants.ENTER).append(Constants.TAB_SPACE).append(Constants.TAB_SPACE);
			methodBody.append("return this;");
			methodInfo.setMethodBody(methodBody.toString());
			clazz.addMethodInfo(methodInfo);
			
			methodInfo = new MethodInfo();
			methodInfo.setPe(PackageEnum.PUBLIC);
			methodInfo.setType(Constants.CRITERIA);
			methodInfo.setName("and" + getClassName(colName) + "LessThanOrEqualTo");
			methodInfo.setArgs(type + " value");
			methodBody = new StringBuilder();
			methodBody.append("addCriterion(\"").append(colName).append(" <=\", value, \"")
				.append(varName).append("\");").append(Constants.ENTER).append(Constants.TAB_SPACE).append(Constants.TAB_SPACE);
			methodBody.append("return this;");
			methodInfo.setMethodBody(methodBody.toString());
			clazz.addMethodInfo(methodInfo);
			
			//如果是String，就有
			if ("String".equals(type)) {
				methodInfo = new MethodInfo();
				methodInfo.setPe(PackageEnum.PUBLIC);
				methodInfo.setType(Constants.CRITERIA);
				methodInfo.setName("and" + getClassName(colName) + "Like");
				methodInfo.setArgs(type + " value");
				methodBody = new StringBuilder();
				methodBody.append("addCriterion(\"").append(colName).append(" like\", value, \"")
					.append(varName).append("\");").append(Constants.ENTER).append(Constants.TAB_SPACE).append(Constants.TAB_SPACE);
				methodBody.append("return this;");
				methodInfo.setMethodBody(methodBody.toString());
				clazz.addMethodInfo(methodInfo);
				
				methodInfo = new MethodInfo();
				methodInfo.setPe(PackageEnum.PUBLIC);
				methodInfo.setType(Constants.CRITERIA);
				methodInfo.setName("and" + getClassName(colName) + "NotLike");
				methodInfo.setArgs(type + " value");
				methodBody = new StringBuilder();
				methodBody.append("addCriterion(\"").append(colName).append(" not like\", value, \"")
					.append(varName).append("\");").append(Constants.ENTER).append(Constants.TAB_SPACE).append(Constants.TAB_SPACE);
				methodBody.append("return this;");
				methodInfo.setMethodBody(methodBody.toString());
				clazz.addMethodInfo(methodInfo);
			}
			
			methodInfo = new MethodInfo();
			methodInfo.setPe(PackageEnum.PUBLIC);
			methodInfo.setType(Constants.CRITERIA);
			methodInfo.setName("and" + getClassName(colName) + "In");
			methodInfo.setArgs("List<" + dataType.getBigName() + "> values");
			methodBody = new StringBuilder();
			methodBody.append("addCriterion(\"").append(colName).append(" in\", values, \"")
				.append(varName).append("\");").append(Constants.ENTER).append(Constants.TAB_SPACE).append(Constants.TAB_SPACE);
			methodBody.append("return this;");
			methodInfo.setMethodBody(methodBody.toString());
			clazz.addMethodInfo(methodInfo);

			methodInfo = new MethodInfo();
			methodInfo.setPe(PackageEnum.PUBLIC);
			methodInfo.setType(Constants.CRITERIA);
			methodInfo.setName("and" + getClassName(colName) + "NotIn");
			methodInfo.setArgs("List<" + dataType.getBigName() + "> values");
			methodBody = new StringBuilder();
			methodBody.append("addCriterion(\"").append(colName).append(" not in\", values, \"")
				.append(varName).append("\");").append(Constants.ENTER).append(Constants.TAB_SPACE).append(Constants.TAB_SPACE);
			methodBody.append("return this;");
			methodInfo.setMethodBody(methodBody.toString());
			clazz.addMethodInfo(methodInfo);
			
			methodInfo = new MethodInfo();
			methodInfo.setPe(PackageEnum.PUBLIC);
			methodInfo.setType(Constants.CRITERIA);
			methodInfo.setName("and" + getClassName(colName) + "Between");
			methodInfo.setArgs(type + " value1, " + type + " value2");
			methodBody = new StringBuilder();
			methodBody.append("addCriterion(\"").append(colName).append(" between\", value1, value2, \"")
				.append(varName).append("\");").append(Constants.ENTER).append(Constants.TAB_SPACE).append(Constants.TAB_SPACE);
			methodBody.append("return this;");
			methodInfo.setMethodBody(methodBody.toString());
			clazz.addMethodInfo(methodInfo);
			
			methodInfo = new MethodInfo();
			methodInfo.setPe(PackageEnum.PUBLIC);
			methodInfo.setType(Constants.CRITERIA);
			methodInfo.setName("and" + getClassName(colName) + "NotBetween");
			methodInfo.setArgs(type + " value1, " + type + " value2");
			methodBody = new StringBuilder();
			methodBody.append("addCriterion(\"").append(colName).append(" not between\", value1, value2, \"")
				.append(varName).append("\");").append(Constants.ENTER).append(Constants.TAB_SPACE).append(Constants.TAB_SPACE);
			methodBody.append("return this;");
			methodInfo.setMethodBody(methodBody.toString());
			clazz.addMethodInfo(methodInfo);
		}
		return clazz;
	}
}
