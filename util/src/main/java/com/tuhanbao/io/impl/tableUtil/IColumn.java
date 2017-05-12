package com.tuhanbao.io.impl.tableUtil;

public interface IColumn {

	String getName();

	DataType getDataType();

	long getLength();

	String getDefaultValue();

	String getComment();
	
	boolean isPK();

}
