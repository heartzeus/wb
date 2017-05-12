package com.tuhanbao.autotool.mvc;

import java.util.HashMap;
import java.util.Map;

public class J2EETableManager {
	private static final Map<String, J2EETable> TABLES = new HashMap<String, J2EETable>();
	
	public static void addTable(J2EETable t) {
		if (t == null) return;
		TABLES.put(t.getName(), t);
	}
	
	public static J2EETable getTable(String name) {
		return TABLES.get(name);
	}

	public static Map<String, J2EETable> getAllTables() {
		return TABLES;
	}
}
