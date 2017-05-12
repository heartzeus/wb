package com.tuhanbao.io.impl.tableUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.tuhanbao.io.objutil.FileUtil;
import com.tuhanbao.io.txt.util.TxtUtil;

public class TableManager {
	private static final Map<String, ImportTable> TABLES = new HashMap<String, ImportTable>();
	
	public static void addTable(ImportTable t) {
		if (t == null) return;
		TABLES.put(t.getName(), t);
	}
	
	public static ImportTable getTable(String name) {
		return TABLES.get(name);
	}

	public static void generateTableConstants(String url, String fullPath) throws IOException {
		TxtUtil.write(FileUtil.appendPath(fullPath, "TableConstants.java"), 
				TableUtil.getConstantClassStr(url, TABLES.values()));
	}
}
