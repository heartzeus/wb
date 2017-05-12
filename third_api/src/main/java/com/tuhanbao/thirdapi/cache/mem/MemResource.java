package com.tuhanbao.thirdapi.cache.mem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tuhanbao.base.ServiceBean;
import com.tuhanbao.thirdapi.cache.CacheResource;
import com.tuhanbao.thirdapi.cache.ICacheKey;
import com.tuhanbao.util.IResource;
import com.tuhanbao.util.db.table.Table;
import com.tuhanbao.util.exception.MyException;

public final class MemResource extends IResource implements CacheResource {
    
    private Map<ICacheKey, Map<String, Object>> CACHE;

    private Map<Table, Map<Object, ServiceBean>> TABLE_CACHE;
    
    protected MemResource(MemCacheManager manager) {
        super(manager);
        CACHE = new HashMap<ICacheKey, Map<String, Object>>();
        TABLE_CACHE = new HashMap<Table, Map<Object, ServiceBean>>();
    }

    @Override
    public int set(ICacheKey ck, String key, Object value) {
        Map<String, Object> cache = getCacheByCacheKey(ck);
        if (ck.isExpire()) {
            cache.put(key, new ExpireObject(ck.getExpireTime(), value));
        }
        else {
            cache.put(key, value);
        }
        return 1;
    }
    
    private Map<String, Object> getCacheByCacheKey(ICacheKey ck) {
        if (!CACHE.containsKey(ck)) {
            CACHE.put(ck, new HashMap<String, Object>());
        }
        return CACHE.get(ck);
    }

    @Override
    public void release() {
    }

    @Override
    public int setex(ICacheKey ck, String key, int seconds, Object value) {
        if (ck.isExpire()) {
            Map<String, Object> cache = getCacheByCacheKey(ck);
            cache.put(key, new ExpireObject(seconds, value));
            return 1;
        }
        throw new MyException("you cannot expire a value for a not expire CacheKey");
    }

    @Override
    public int del(ICacheKey ck, String key) {
        Map<String, Object> cache = getCacheByCacheKey(ck);
        return cache.remove(key) == null ? 0 : 1;
    }

    @Override
    public Object get(ICacheKey ck, String key) {
        Map<String, Object> cache = getCacheByCacheKey(ck);
        if (ck.isExpire()) {
            ExpireObject eo = (ExpireObject)cache.get(key);
            if (eo != null && eo.isExpire()) {
                cache.remove(key);
                return null;
            }
            else {
                if (eo == null) return null;
                else return eo.getObject();
            }
        }
        else {
            return cache.get(key);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(ICacheKey ck, String key, Class<T> clazz) {
        Object result = get(ck, key);
        if (result == null) return null;
        return (T)result;
    }

    @Override
    public boolean exists(ICacheKey ck, String key) {
        return get(ck, key) != null;
    }

    @Override
    public int len(ICacheKey ck) {
        if (ck.isExpire()) {
            throw new MyException("you cannot get length for a expire CacheKey");
        }
        else {
            Map<String, Object> cache = getCacheByCacheKey(ck);
            return cache.size();
        }
    }

    @Override
    public ServiceBean get(Table table, Object pkValue) throws ClassNotFoundException {
        Map<Object, ServiceBean> cache = getCacheByTable(table);
        return cache.get(pkValue.toString());
    }
    
    @Override
    public List<ServiceBean> get(Table table) throws ClassNotFoundException {
        Collection<ServiceBean> values = getCacheByTable(table).values();
        List<ServiceBean> list = new ArrayList<ServiceBean>(values.size());
        list.addAll(values);
        return list;
    }
    
    private Map<Object, ServiceBean> getCacheByTable(Table table) {
        if (!TABLE_CACHE.containsKey(table)) {
            TABLE_CACHE.put(table, new HashMap<Object, ServiceBean>());
        }
        return TABLE_CACHE.get(table);
    }

    @Override
    public void save(ServiceBean bean) {
        Table table = bean.getTable();
        getCacheByTable(table).put(bean.getKeyValue().toString(), bean);
    }

    @Override
    public boolean hasCacheTable(Table table) {
        return TABLE_CACHE.containsKey(table);
    }

    @Override
    public int len(Table table) {
        if (hasCacheTable(table)) {
            return getCacheByTable(table).size();
        }
        return 0;
    }

    @Override
    public void del(Table table, Object pkValue) {
        getCacheByTable(table).remove(pkValue.toString());
    }

    @Override
    public void destroy() {
        this.release();
    }
    
    protected void checkResource() throws Exception {
    }

    @Override
    public void recreateResource() {
    }

}
