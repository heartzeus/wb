package com.tuhanbao.web.filter;

import java.util.Collection;

public class SqlItem implements ISqlItem{
	private String name;
	
	private boolean isList = false;
	
	public SqlItem(String name) {
		this.name = name;
	}
	
	/**
	 * isList = true时请重写
	 * @return
	 */
	public Collection<Object> listValue() {
		return null;
	}
	

	public boolean isList() {
		return isList;
	}

	public String getSql() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
