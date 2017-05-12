package com.tuhanbao.thirdapi.cache;

import java.util.List;

import com.tuhanbao.base.ServiceBean;
import com.tuhanbao.util.db.table.Table;

public interface CacheResource {

    int set(ICacheKey ck, String key, Object value);

    void release();

    int setex(ICacheKey ck, String key, int seconds, Object value);

    int del(ICacheKey ck, String key);

    Object get(ICacheKey ck, String key);

    boolean exists(ICacheKey ck, String key);

    int len(ICacheKey ck);
    
    ServiceBean get(Table table, Object pkValue) throws ClassNotFoundException;
    
    List<ServiceBean> get(Table table) throws ClassNotFoundException;

    void save(ServiceBean bean);

    int len(Table table);

    void del(Table table, Object pkValue);

    <T> T get(ICacheKey ck, String key, Class<T> clazz);

    boolean hasCacheTable(Table table);
}
