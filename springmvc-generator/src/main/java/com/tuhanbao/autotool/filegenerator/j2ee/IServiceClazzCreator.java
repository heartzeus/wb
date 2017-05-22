package com.tuhanbao.autotool.mvc.clazz;

import com.tuhanbao.Constants;
import com.tuhanbao.autotool.mvc.J2EEProjectInfo;
import com.tuhanbao.autotool.mvc.J2EETable;
import com.tuhanbao.io.impl.classUtil.ClassInfo;

public class IServiceClazzCreator extends ClazzCreator {
	public IServiceClazzCreator(J2EEProjectInfo project) {
		super(project);
	}

	public ClassInfo toClazz(J2EETable table) {
		ClassInfo clazz = new ClassInfo();
		clazz.setInterface(true);
		String modelName = table.getModelName();
		clazz.setName(table.getIServiceName() + " extends IService<" + modelName + ">");
		clazz.setPackageInfo(project.getIServicePath(table.getModule()));
		String modelPath = project.getServiceBeanPath(table.getModule());
		clazz.addImportInfo(modelPath + Constants.STOP_EN + modelName);
		clazz.addImportInfo(Constants.ISERVICE_NAME);
		
		//TODO 外键关联
		return clazz;
	}
}
