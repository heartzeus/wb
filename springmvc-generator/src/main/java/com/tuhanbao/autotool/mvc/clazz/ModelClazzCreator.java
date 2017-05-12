package com.tuhanbao.autotool.mvc.clazz;

import com.tuhanbao.autotool.mvc.J2EEProjectInfo;
import com.tuhanbao.autotool.mvc.J2EETable;
import com.tuhanbao.io.impl.classUtil.ClassInfo;
import com.tuhanbao.io.impl.classUtil.MethodInfo;
import com.tuhanbao.io.impl.classUtil.PackageEnum;

public class ModelClazzCreator extends ClazzCreator {
    private static final String MOCLASS_SUFFIX = "MO";
	
	public ModelClazzCreator(J2EEProjectInfo project) {
		super(project);
	}

	public ClassInfo toClazz(J2EETable table) {
		ClassInfo classInfo = new ClassInfo();
        String modelName = table.getModelName();
        String moClassName = modelName + MOCLASS_SUFFIX;
        classInfo.setName(modelName + " extends " + moClassName);
        classInfo.setPackageInfo(this.project.getServiceBeanPath(table.getModule()));
        classInfo.addImportInfo(this.project.getServiceBeanPath(table.getModule()) + "." + moClassName);
        MethodInfo method = new MethodInfo();
        method.setName(modelName);
        method.setPe(PackageEnum.PUBLIC);
        classInfo.addMethodInfo(method);
        
        return classInfo;
	}
}
