package com.tuhanbao;

import java.io.File;

import com.thoughtworks.xstream.XStream;

/**
 * Goal which touches a timestamp file.
 * 
 * @goal generator
 * @phase process-sources
 */
public class ControllerGenerator {
	/**
	 * 输出路径
	 * 
	 * @parameter expression="${project.build.sourceDirectory}"
	 *            default-value="${project.version}"
	 * @required
	 */
	private String outDirectory;

	private String projectName;

	private String modelDirectory;

	private HHNZPackageUtil packageUtil = null;

	public void execute() {
		// if(modelDirectory !=null){ //没有配置默认的model文件夹
		// for (String url : StringUtil.string2Array(modelDirectory,
		// Constants.COMMA)) {
		// File file = new File(url);
		// for (File f : file.listFiles()) {
		// if (f.isDirectory() && MODEL.equals(f.getName())) {
		// for (File beanJava : f.listFiles()) {
		// String fileName = beanJava.getName();
		// if (beanJava.isFile() && name2.endsWith(".java")){
		// String modelName = fileName.substring(0, fileName.length() - 5);
		// }
		// }
		// }
		// }
		// }
		//
		// }else{
		//
		// }

		// 遍历工程下的所有文件夹，如果以model开头，根据所有文件夹下的bean生成一下类
		// Service 接口 CRUD接口（增加，修改，删除，查询（all),分页查询
		// Service实现接口
		// Controller

		// File sourceDirs = new File(this.outDirectory);
		// File[] files = sourceDirs.list();
		// for (int i = 0; i < files.length; i++) {
		// System.err.println(files[i].getName());
		// }
		// System.err.println(this.outDirectory);
		// //读取所有的Mapper,Model,写出ctroller
		// // File f = outputDirectory;
		// //
		// // if (!f.exists()) {
		// // f.mkdirs();
		// // }
		//
		// File touch = new File(f, "touch.txt");
		//
		// FileWriter w = null;
		// try {
		// w = new FileWriter(touch);
		//
		// w.write("touch.txt");
		// } catch (IOException e) {
		// throw new MojoExecutionException("Error creating file " + touch, e);
		// } finally {
		// if (w != null) {
		// try {
		// w.close();
		// } catch (IOException e) {
		// // ignore
		// }
		// }
		// }
	}

	public static void main(String[] args) {
		ControllerGenerator s = new ControllerGenerator();
		File xmlFile = new File("D:\\controller.xml");
		if (xmlFile.exists()) {
			XStream xStream = new XStream();
			xStream.autodetectAnnotations(true);
			xStream.alias("config", Config.class);
			xStream.alias("packs", Pack.class);
			xStream.alias("bean", Bean.class);
			Config parse = (Config)xStream.fromXML(xmlFile);
			String outPath = parse.getOutDir();

			// FileReader fr = new FileReader(confPath);
			// BufferedReader br = new BufferedReader(fr);
			// String line = "";
			// while((line = br.readLine()) != null){
			// sb.append(line + enter);
			// }
			// br.close();
			// fr.close();
			System.out.println(parse);

		} else {
			System.out.print("ddf");
		}
	}

	private void createContollerClass(String outUrl) {
		// TxtUtil.write(outUrl, packageUtil.getControllerTemplate())
	}

	private void createServiceIntefaceClass(String name) {
		String packageUrl = "com/hhnz/api/" + projectName + "/service";
		String modelUrl = "com/hhnz/api/" + projectName + "/model";
		String serviceUrl = outDirectory + packageUrl;
		String fileName = "I" + name + "Service";
		StringBuilder sb = new StringBuilder();
		sb.append("package ").append(packageUrl.replace("/", ".")).append(
				Constants.ENTER);
		sb.append(Constants.ENTER);
		sb.append("import ").append(modelUrl.replace("/", ".")).append(".")
				.append(name).append(Constants.SEMICOLON).append(
						Constants.ENTER);
		sb.append("import java").append(modelUrl.replace("/", ".")).append(".")
				.append(name).append(Constants.SEMICOLON).append(
						Constants.ENTER);
	}
}
