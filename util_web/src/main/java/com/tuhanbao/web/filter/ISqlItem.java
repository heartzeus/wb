package com.tuhanbao.web.filter;

import java.util.Collection;

public interface ISqlItem {
	public Collection<Object> listValue();

	public boolean isList();

	public String getSql();
	
}
