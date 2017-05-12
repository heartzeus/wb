package com.tuhanbao.util.db.table.dbtype;

import com.tuhanbao.io.impl.tableUtil.DataType;

public interface DBDataType {
	DataType getDataType();
	
	String name();

	boolean isBlob();
}
