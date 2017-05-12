package com.tuhanbao.autotool.mvc;

import java.util.HashMap;
import java.util.Map;

import com.tuhanbao.io.base.Constants;
import com.tuhanbao.io.objutil.StringUtil;
import com.tuhanbao.util.db.conn.DBSrc;

public class ModuleManager {
	private static final Map<String, DBSrc> MODULES = new HashMap<String, DBSrc>();

	private static final Map<String, DBSrc> DEBUG_MODULES = new HashMap<String, DBSrc>();
	
	public static void addModule(String module, DBSrc src) {
		if (StringUtil.isEmpty(module)) module = Constants.EMPTY;
		MODULES.put(module, src);
	}

	public static void addDebugModule(String module, DBSrc src) {
		if (StringUtil.isEmpty(module)) module = Constants.EMPTY;
		DEBUG_MODULES.put(module, src);
	}
	
	public static DBSrc getDBSrc(String module) {
		if (StringUtil.isEmpty(module)) module = Constants.EMPTY;
		return MODULES.get(module);
	}
	
	public static DBSrc getDebugDBSrc(String module) {
		if (StringUtil.isEmpty(module)) module = Constants.EMPTY;
		return DEBUG_MODULES.get(module);
	}

	public static String getModule(DBSrc src) {
		for (Map.Entry<String, DBSrc> entry : MODULES.entrySet()) {
			if (entry.getValue() == src) return entry.getKey();
		}
		return null;
	}

	public static String getDebugModule(DBSrc src) {
		for (Map.Entry<String, DBSrc> entry : DEBUG_MODULES.entrySet()) {
			if (entry.getValue() == src) return entry.getKey();
		}
		return null;
	}

	public static Map<String, DBSrc> getAllModules() {
		return MODULES;
	}

	public static Map<String, DBSrc> getAllDebugModules() {
		return DEBUG_MODULES;
	}
}

