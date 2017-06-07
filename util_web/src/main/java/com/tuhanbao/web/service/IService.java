package com.tuhanbao.web.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.tuhanbao.base.dataservice.IDataService;
import com.tuhanbao.base.dataservice.ServiceBean;
import com.tuhanbao.util.db.table.data.DataValue;
import com.tuhanbao.web.filter.SelectorFilter;

public interface IService<T extends ServiceBean> extends IDataService<T>
{
    int deleteById(Object pkValue);

    int count(SelectorFilter selectorFilter);
    
	T selectById(Object pkValue);

    void deleteAllRelative(Collection<T> list);

    List<Map<String, Object>> excuteSql(String sql, DataValue... args);
}