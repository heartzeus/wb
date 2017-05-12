package com.tuhanbao.web.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.tuhanbao.base.ServiceBean;
import com.tuhanbao.util.db.table.data.DataValue;
import com.tuhanbao.web.filter.Filter;
import com.tuhanbao.web.filter.SelectorFilter;

public interface IService<T extends ServiceBean>
{
	void add(T record);

    int delete(Filter filter);
    
    int deleteById(Object pkValue);

    int update(T record, Filter filter);

    int update(T record);
    
	int updateSelective(T record, Filter filter);

	int updateSelective(T record);

    int count(SelectorFilter selectorFilter);
    
	T selectById(Object pkValue);

    List<T> select(Filter filter);
    
    void delete(Collection<T> list);

    void deleteAllRelative(Collection<T> list);

    List<Map<String, Object>> excuteSql(String sql, DataValue... args);
}