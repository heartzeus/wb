package com.tuhanbao.autotool.mvc.clazz;

import com.tuhanbao.Constants;
import com.tuhanbao.autotool.mvc.J2EEProjectInfo;
import com.tuhanbao.autotool.mvc.J2EETable;
import com.tuhanbao.io.impl.classUtil.ClassInfo;
import com.tuhanbao.io.objutil.StringUtil;
import com.tuhanbao.util.util.clazz.ClazzUtil;

public class ServiceClazzCreator extends ClazzCreator {
	public ServiceClazzCreator(J2EEProjectInfo project) {
		super(project);
	}

	public ClassInfo toClazz(J2EETable table) {
		ClassInfo clazz = new ClassInfo();
		String modelName = table.getModelName();
		clazz.setName(table.getServiceName() + " extends ServiceImpl<" + modelName + "> implements " + table.getIServiceName());
		String module = table.getModule();
		clazz.setPackageInfo(project.getServicePath(module));
		clazz.addImportInfo("org.springframework.stereotype.Service");
		clazz.addImportInfo("org.springframework.transaction.annotation.Transactional");
		//固定的serviceImpl
		if(StringUtil.isEmpty(module)){
			clazz.addImportInfo(project.getServicePath(null) + Constants.STOP_EN + "ServiceImpl");
		}else{
			clazz.addImportInfo(project.getServicePath(null) + Constants.STOP_EN + module + Constants.STOP_EN + "ServiceImpl");
		}
		
		clazz.addImportInfo(project.getServiceBeanPath(module) + Constants.STOP_EN + modelName);
		clazz.addImportInfo(project.getIServicePath(module) + Constants.STOP_EN + table.getIServiceName());
		
		clazz.addAnnotation("@Service(\"" + ClazzUtil.firstCharLowerCase(modelName) + "Service\")");
		clazz.addAnnotation("@Transactional" + getTransactionManagerName(module));
		return clazz;
	}
	
	private static String getTransactionManagerName(String module) {
		if (StringUtil.isEmpty(module)) {
			return Constants.EMPTY;
		}
		String s = "TransactionManager";
		return "(\"" + module + s + "\")";
	}
}
