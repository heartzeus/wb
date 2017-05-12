package com.tuhanbao.autotool.mvc.web;

import java.io.IOException;

import com.tuhanbao.autotool.mvc.J2EEProjectInfo;
import com.tuhanbao.autotool.mvc.J2EETable;
import com.tuhanbao.io.txt.util.TxtUtil;

import com.tuhanbao.util.util.clazz.ClazzUtil;

public class WebManager {
	
	public static void createWebFile(J2EEProjectInfo project, J2EETable table) throws IOException{
		
		//生成表的jsp
		TxtUtil.write(project.getJSPUrl(ClazzUtil.firstCharLowerCase(table.getModelName()))+".jsp", new JspCreator().getJspFile(table));
		//生成枚举类型下拉框jsp
		TxtUtil.write(project.getJSPUrl(ClazzUtil.firstCharLowerCase(table.getModelName()))+"Select.jsp", new JspCreator().getSelectJspFile(table));
		
		//生成表的js
		TxtUtil.write(project.getJSUrl(ClazzUtil.firstCharLowerCase(table.getModelName()))+".js", new JsCreator().getJsFile(table));
		//生成枚举类型下拉框js
		TxtUtil.write(project.getJSUrl(ClazzUtil.firstCharLowerCase(table.getModelName()))+"Select.js", new JsCreator().getSelectJsFile());
	}
}
