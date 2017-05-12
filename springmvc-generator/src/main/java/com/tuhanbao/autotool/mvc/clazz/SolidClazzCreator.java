package com.tuhanbao.autotool.mvc.clazz;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.tuhanbao.Constants;
import com.tuhanbao.autotool.mvc.J2EEProjectInfo;
import com.tuhanbao.autotool.mvc.J2EETable;
import com.tuhanbao.autotool.mvc.ModuleManager;
import com.tuhanbao.io.impl.classUtil.ClassInfo;
import com.tuhanbao.io.impl.tableUtil.DBType;
import com.tuhanbao.io.objutil.FileUtil;
import com.tuhanbao.io.objutil.StringUtil;
import com.tuhanbao.io.txt.util.TxtUtil;
import com.tuhanbao.util.db.conn.DBSrc;
import com.tuhanbao.util.db.table.CacheType;
import com.tuhanbao.util.exception.MyException;
import com.tuhanbao.util.util.clazz.ClazzUtil;

public class SolidClazzCreator extends ClazzCreator {
	private static final String gap = "    ";
	private static final String gap2 = gap + gap;
	private static final String gap3 = gap2 + gap;
	private static final String gap4 = gap3 + gap;
	
	private static String ORACLE_CUT_PAGE = "";
	private static String MYSQL_CUT_PAGE = "";
	
	private Map<String, String> REPLACE_MAP = new HashMap<String, String>();

	private Map<String, String> BASE_MAP = new HashMap<String, String>();
	
	static {
		StringBuilder sb = new StringBuilder();
		sb.append("  <sql id=\"CutPagePrefix\" >").append(Constants.ENTER);
		sb.append("    <if test=\"page != null\" >").append(Constants.ENTER);
		sb.append("      select * from ( select row_.*, rownum rownum_ from ( ").append(Constants.ENTER);
		sb.append("    </if>").append(Constants.ENTER);
		sb.append("  </sql>").append(Constants.ENTER);
		sb.append("  <sql id=\"CutPageSuffix\" >").append(Constants.ENTER);
		sb.append("    <if test=\"page != null\" >").append(Constants.ENTER);
		sb.append("      <![CDATA[ ) row_ ) where rownum_ > #{page.begin} and rownum_ <= #{page.end} ]]>").append(Constants.ENTER);
		sb.append("    </if>").append(Constants.ENTER);
		sb.append("  </sql>").append(Constants.ENTER);
		ORACLE_CUT_PAGE = sb.toString();
		
		sb = new StringBuilder();
		sb.append("  <sql id=\"CutPagePrefix\" ></sql>").append(Constants.ENTER);
		sb.append("  <sql id=\"CutPageSuffix\" >").append(Constants.ENTER);
		sb.append("    <if test=\"page != null\" >").append(Constants.ENTER);
		sb.append("      <![CDATA[ limit #{page.begin}, #{page.numPerPage} ]]>").append(Constants.ENTER);
		sb.append("    </if>").append(Constants.ENTER);
		sb.append("  </sql>").append(Constants.ENTER);
		MYSQL_CUT_PAGE = sb.toString();
	}
	
	public SolidClazzCreator(J2EEProjectInfo project) {
		super(project);
		
		BASE_MAP.put("{config}", project.getConfigPath());
		BASE_MAP.put("{impl}", project.getImplPath());
		BASE_MAP.put("{res}", project.getResUrl());
		BASE_MAP.put("{controller}", project.getControllerPath(null));
		BASE_MAP.put("{projectName}", project.getProjectName());
		BASE_MAP.put("{projectHead}", project.getPackageHead());
		REPLACE_MAP.put("{db}", getSpringDBXml());
		REPLACE_MAP.put("{serverManager}", gap + "<bean class=\"" + project.getPackageHead() + ".impl." + project.getProjectName() + ".ServerManager\"></bean>");
		REPLACE_MAP.putAll(BASE_MAP);
	}

	public ClassInfo toClazz(J2EETable table) {
		return null;
	}
	
	public void writeSolidFiles(List<J2EETable> tables, List<SolidObject> solidObjects) {
		for (Map.Entry<String, DBSrc> entry : ModuleManager.getAllModules().entrySet()) {
			String module = entry.getKey();
			DBType dbType = entry.getValue().getDbType();
	        writeMapper(module);
	        writeMapperXml(module, dbType);
	        writeServiceImpl(module, dbType);
		}
		writeServerManager(tables);
		writeDBConfigProperties();
		
		try {
		    writeFiles(solidObjects);
		} catch (IOException e) {
            throw MyException.getMyException(e);
        }
	}
	
	public void writeFiles(List<SolidObject> list) throws IOException {
	    for (SolidObject so : list) {
	        writeFile(so);
	    }
	}
	
	private void writeFile(SolidObject so) throws IOException {
        String targetUrl = replace(so.getTargetUrl(), BASE_MAP);
        String key = so.getKey();
        String url = FileUtil.appendPath(project.getRootPath(), 
                FileUtil.appendPath(targetUrl.replace(".", Constants.FILE_SEP), key));
        String oldTxt = TxtUtil.read(new File(FileUtil.appendPath(Constants.CONFIG_ROOT, "bak" + so.getUrl(), key)));
        FileUtil.writeFile(url, replace(oldTxt, REPLACE_MAP), so.getOverwriteStrategy());
    }
	
	private static String replace(String s, Map<String, String> map) {
	    for (Entry<String, String> entry : map.entrySet()) {
	        s = s.replace(entry.getKey(), entry.getValue());
	    }
	    return s;
	}
	
	private void writeMapperXml(String module, DBType dbType) {
		try {
			String mapperPath = this.project.getMapperPath(module);
			String mapperName = getMapperName(module);
			String url = FileUtil.appendPath(project.getSrcPath(), 
					FileUtil.appendPath(mapperPath.replace(".", Constants.FILE_SEP), mapperName + ".xml"));
			String oldTxt = TxtUtil.read(new File(FileUtil.appendPath(Constants.CONFIG_ROOT, "bak", "Mapper.xml")));
			
			String mapper = mapperPath + Constants.STOP_EN + mapperName;
			String newTxt = oldTxt.replace("{mapper}", mapper).replace("{mapper.name}", mapperName);
			
			if (dbType == DBType.ORACLE) {
				newTxt = newTxt.replace("{cutpage}", ORACLE_CUT_PAGE);
				newTxt = newTxt.replace("{mysql_insert}", "");
			}
			else {
				newTxt = newTxt.replace("{cutpage}", MYSQL_CUT_PAGE);
				newTxt = newTxt.replace("{mysql_insert}", "useGeneratedKeys=\"true\" keyProperty=\"keyValue\" ");
			}
			
			TxtUtil.write(url, newTxt);
			
		} catch (IOException e) {
			throw MyException.getMyException(e);
		}
	}
	
	private void writeMapper(String module) {
		try {
			String mapperPath = this.project.getMapperPath(module);
			String mapperName = getMapperName(module);
			
			String url = FileUtil.appendPath(project.getSrcPath(), 
					FileUtil.appendPath(mapperPath.replace(".", Constants.FILE_SEP), mapperName + ".java"));
			
			String oldTxt = TxtUtil.read(new File(FileUtil.appendPath(Constants.CONFIG_ROOT, "bak", "Mapper.java")));
			String newTxt = oldTxt.replace("{package}", mapperPath).replace("{mapper.name}", mapperName);
			
			TxtUtil.write(url, newTxt);
			
		} catch (IOException e) {
			throw MyException.getMyException(e);
		}
	}

	private String getMapperName(String module) {
		String mapperName = null;
		int lastIndex = module.lastIndexOf(".");
		if (lastIndex != - 1) {
			mapperName = ClazzUtil.getClassName(module.substring(lastIndex + 1));
		}
		else {
			mapperName = ClazzUtil.getClassName(module);
		}
		mapperName += "Mapper";
		return mapperName;
	}
	
	
	private void writeServiceImpl(String module, DBType dbType) {
		try {
			String servicePath = this.project.getServicePath(module);
			String mapperName = this.getMapperName(module);
			String url = FileUtil.appendPath(project.getSrcPath(), 
					FileUtil.appendPath(servicePath.replace(".", Constants.FILE_SEP), "ServiceImpl.java"));
			
			String mapper = this.project.getMapperPath(module) + Constants.STOP_EN + mapperName;
			String tableConstants = this.project.getConstantsPath() + Constants.STOP_EN + "TableConstants";
			
			String oldTxt = TxtUtil.read(new File(FileUtil.appendPath(Constants.CONFIG_ROOT, "bak", "ServiceImpl.java")));
			String newTxt = oldTxt.replace("{package}", project.getServicePath(module));
			newTxt = newTxt.replace("{tableConstants}", tableConstants);
			newTxt = newTxt.replace("{mapper}", mapper).replace("{mapper.name}", mapperName);
			
			TxtUtil.write(url, newTxt);
			
		} catch (IOException e) {
			throw MyException.getMyException(e);
		}
	}
	
	private void writeDBConfigProperties() {
		try {
			String url = FileUtil.appendPath(project.getRootPath(), project.getConfigPath(), "db.properties");
			
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, DBSrc> entry : ModuleManager.getAllModules().entrySet()) {
				String module = entry.getKey();
				DBSrc src = entry.getValue();
				sb.append(getModuleStr1("db_url", module)).append("=").append(src.getUrl()).append(Constants.ENTER);
				sb.append(getModuleStr1("db_user", module)).append("=").append(src.getUser()).append(Constants.ENTER);
				sb.append(getModuleStr1("db_password", module)).append("=").append(src.getPassword()).append(Constants.ENTER);
				sb.append(Constants.ENTER);
			}
			TxtUtil.write(url, sb.toString());
			
			Map<String, DBSrc> allDebugModules = ModuleManager.getAllDebugModules();
			if (!allDebugModules.isEmpty()) {
				url = FileUtil.appendPath(project.getRootPath(), project.getConfigPath(), "debug", "db_debug.properties");
				
				sb = new StringBuilder();
				for (Map.Entry<String, DBSrc> entry : allDebugModules.entrySet()) {
					String module = entry.getKey();
					DBSrc src = entry.getValue();
					sb.append(getModuleStr1("db_url", module)).append("=").append(src.getUrl()).append(Constants.ENTER);
					sb.append(getModuleStr1("db_user", module)).append("=").append(src.getUser()).append(Constants.ENTER);
					sb.append(getModuleStr1("db_password", module)).append("=").append(src.getPassword()).append(Constants.ENTER);
					sb.append(Constants.ENTER);
				}
				TxtUtil.write(url, sb.toString());
			}
		} catch (IOException e) {
			throw MyException.getMyException(e);
		}
	}
	
	private String getSpringDBXml() {
		
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, DBSrc> entry : ModuleManager.getAllModules().entrySet()) {
			String module = entry.getKey();
			String mapper = this.project.getMapperPath(module);
			
			String srcName = getModuleStr1("ds", module);
			String sqlSessionName = getModuleStr2("SqlSessionFactory", module);
			String tmName = getModuleStr2("TransactionManager", module);
			sb.append(gap).append("<bean id=\"").append(srcName).append("\" class=\"com.alibaba.druid.pool.DruidDataSource\">").append(Constants.ENTER);
			sb.append(gap2).append("<property name=\"url\" value=\"${").append(getModuleStr1("db_url", module)).append("}\"></property>").append(Constants.ENTER);
			sb.append(gap2).append("<property name=\"username\" value=\"${").append(getModuleStr1("db_user", module)).append("}\"></property>").append(Constants.ENTER);
			sb.append(gap2).append("<property name=\"password\" value=\"${").append(getModuleStr1("db_password", module)).append("}\"></property>").append(Constants.ENTER);
			sb.append(gap).append("</bean>").append(Constants.ENTER);
			
			sb.append(gap).append("<bean id=\"").append(sqlSessionName).append("\" class=\"org.mybatis.spring.SqlSessionFactoryBean\">").append(Constants.ENTER);
			sb.append(gap2).append("<property name=\"dataSource\" ref=\"").append(srcName).append("\" />").append(Constants.ENTER);
			sb.append(gap2).append("<property name=\"configurationProperties\">").append(Constants.ENTER);
			sb.append(gap2).append("<props>").append(Constants.ENTER);
			sb.append(gap3).append("<prop key=\"jdbcTypeForNull\">NULL</prop>").append(Constants.ENTER);
			sb.append(gap2).append("</props>").append(Constants.ENTER);
			sb.append(gap2).append("</property>").append(Constants.ENTER);
			sb.append(gap).append("</bean>").append(Constants.ENTER);
				
			sb.append(gap).append("<bean class=\"org.mybatis.spring.mapper.MapperScannerConfigurer\">").append(Constants.ENTER);
			sb.append(gap2).append("<property name=\"basePackage\" value=\"").append(mapper).append("\" />").append(Constants.ENTER);
			sb.append(gap2).append("<property name=\"sqlSessionFactoryBeanName\" value=\"").append(sqlSessionName).append("\"></property>").append(Constants.ENTER);
			sb.append(gap).append("</bean>").append(Constants.ENTER);
			
			sb.append(gap).append("<bean id=\"").append(tmName).append("\" class=\"org.springframework.jdbc.datasource.DataSourceTransactionManager\">").append(Constants.ENTER);
			sb.append(gap2).append("<property name=\"dataSource\" ref=\"").append(srcName).append("\"></property>").append(Constants.ENTER);
			sb.append(gap).append("</bean>").append(Constants.ENTER);
			sb.append(gap).append("<tx:annotation-driven transaction-manager=\"").append(tmName).append("\" />").append(Constants.ENTER);
		}
		return sb.toString();
	}
	
	private void writeServerManager(List<J2EETable> tables) {
		try {
			String url = FileUtil.appendPath(project.getSrcPath(), project.getImplPath().replace(".", Constants.FILE_SEP), "ServerManager.java");
			
			String oldTxt = TxtUtil.read(new File(FileUtil.appendPath(Constants.CONFIG_ROOT, "bak", "ServerManager.java")));
			StringBuilder mapper = new StringBuilder();
			StringBuilder importMapper = new StringBuilder();
			StringBuilder redis = new StringBuilder();
			
			List<String> modules = new ArrayList<String>();
			for (J2EETable table : tables) {
				if (table.getCacheType() == CacheType.CACHE_ALL) {
					String module = table.getModule();
					if (!modules.contains(module)) modules.add(module);
					String mapperName = getMapperName(module);
					
					redis.append(gap3).append("if (!CacheManager.hasCacheTable(TableConstants.").append(table.getName()).append(".TABLE)) {").append(Constants.ENTER);
					redis.append(gap4).append("CacheManager.save(").append(ClazzUtil.firstCharLowerCase(mapperName)).append(".select(new SelectorFilter(SelectorFactory.getTablesSelector(TableConstants.")
							.append(table.getName()).append(".TABLE))));").append(Constants.ENTER);
					redis.append(gap3).append("}").append(Constants.ENTER);
				}
			}
			
			for (String module : modules) {
				String mapperPath = this.project.getMapperPath(module);
				String mapperName = getMapperName(module);
				importMapper.append("import ").append(mapperPath).append(".").append(mapperName).append(";").append(Constants.ENTER);
				mapper.append(gap).append("@Autowired").append(Constants.ENTER);
				mapper.append(gap).append("private ").append(mapperName).append("<ServiceBean>").append(Constants.BLANK)
					.append(ClazzUtil.firstCharLowerCase(mapperName)).append(";").append(Constants.ENTER);
			}
			
			if (!modules.isEmpty()) {
				importMapper.append("import com.tuhanbao.web.filter.SelectorFactory;").append(Constants.ENTER);
				importMapper.append("import com.tuhanbao.web.filter.SelectorFilter;").append(Constants.ENTER);
				importMapper.append("import com.tuhanbao.base.ServiceBean;").append(Constants.ENTER);
				importMapper.append("import com.tuhanbao.thirdapi.cache.CacheManager;").append(Constants.ENTER);
				importMapper.append("import com.hhnz.api.{projectName}.constants.TableConstants;").append(Constants.ENTER);
			}
			String newTxt = oldTxt.replace("{mapperImport}", importMapper.toString());
			newTxt = newTxt.replace("{mapper}", mapper.toString());
			newTxt = newTxt.replace("{redis}", redis.toString());
			newTxt = newTxt.replace("{projectName}", project.getProjectName());
			newTxt = newTxt.replace("{projectHead}", project.getPackageHead());
			
			TxtUtil.write(url, newTxt);
		} catch (IOException e) {
			throw MyException.getMyException(e);
		}
	}
	
	private static String getModuleStr1(String s, String module) {
		if (StringUtil.isEmpty(module)) {
			return s;
		}
		else return s + "_" + module;
	}
	
	private static String getModuleStr2(String s, String module) {
		if (StringUtil.isEmpty(module)) {
			return ClazzUtil.firstCharLowerCase(s);
		}
		else return module + s;
	}
}
