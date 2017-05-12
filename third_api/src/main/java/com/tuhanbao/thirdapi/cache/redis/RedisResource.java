package com.tuhanbao.thirdapi.cache.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONObject;
import com.tuhanbao.base.ServiceBean;
import com.tuhanbao.thirdapi.cache.CacheResource;
import com.tuhanbao.thirdapi.cache.ICacheKey;
import com.tuhanbao.util.IResource;
import com.tuhanbao.util.db.table.Table;
import com.tuhanbao.util.exception.MyException;
import com.tuhanbao.util.util.json.JsonUtil;

import redis.clients.jedis.Jedis;

public class RedisResource extends IResource implements CacheResource {

    private Jedis jedis;

    private static final String OK = "OK";

    public RedisResource(RedisResourceManager manager, Jedis jedis) {
        super(manager);
        this.jedis = jedis;
    }

    @Override
    public int set(ICacheKey ck, String key, Object value) {
        Object jsonObject = JsonUtil.toJSON(value);
        if (ck.isExpire()) {
            return setex(ck, key, ck.getExpireTime(), jsonObject.toString());
        }
        else {
            return (int)jedis.hset(ck.getName(), key, jsonObject.toString()).longValue();
        }
    }

    @Override
    public void release() {
        jedis.close();
        super.release();
    }

    @Override
    public int setex(ICacheKey ck, String key, int seconds, Object value) {
        Object jsonObject = JsonUtil.toJSON(value);
        if (ck.isExpire()) {
            String result = jedis.setex(getKey(ck, key), seconds, jsonObject.toString());
            return isSuccess(result) ? 1 : -1;
        }
        throw new MyException("you cannot expire a value for a not expire ICacheKey");
    }

    @Override
    public int del(ICacheKey ck, String key) {
        if (ck.isExpire()) {
            return (int)jedis.del(getKey(ck, key)).longValue();
        }
        else {
            return (int)jedis.hdel(ck.getName(), key).longValue();
        }
    }

    @Override
    public String get(ICacheKey ck, String key) {
        if (ck.isExpire()) {
            return jedis.get(getKey(ck, key));
        }
        else {
            return jedis.hget(ck.getName(), key);
        }
    }

    @Override
    public <T> T get(ICacheKey ck, String key, Class<T> clazz) {
        Object result = get(ck, key);
        if (result == null) return null;
        return JsonUtil.getBean(JSONObject.parseObject(result.toString()), clazz);
    }

    @Override
    public boolean exists(ICacheKey ck, String key) {
        if (ck.isExpire()) {
            return jedis.exists(getKey(ck, key));
        }
        else {
            return jedis.hexists(ck.getName(), key);
        }
    }

    @Override
    public int len(ICacheKey ck) {
        if (ck.isExpire()) {
            throw new MyException("you cannot get length for a expire ICacheKey");
        }
        else {
            return (int)jedis.hlen(ck.getName()).longValue();
        }
    }

    @Override
    public ServiceBean get(Table table, Object pkValue) throws ClassNotFoundException {
        String result = jedis.hget(table.getName(), pkValue.toString());
        return (ServiceBean)JsonUtil.getBean(JSONObject.parseObject(result), Class.forName(table.getModelName()));
    }

    @Override
    public List<ServiceBean> get(Table table) throws ClassNotFoundException {
        Map<String, String> result = jedis.hgetAll(table.getName());
        List<ServiceBean> list = new ArrayList<ServiceBean>(result.size());
        for (Entry<String, String> entry : result.entrySet()) {
            ServiceBean serviceBean = (ServiceBean)JsonUtil.getBean(JSONObject.parseObject(entry.getValue()), Class.forName(table.getModelName()));
            list.add(serviceBean);
        }
        return list;
    }

    @Override
    public void save(ServiceBean bean) {
        Table table = bean.getTable();
        jedis.hset(table.getName(), bean.getKeyValue().toString(), bean.toJson().toJSONString());
    }

    @Override
    public boolean hasCacheTable(Table table) {
        return jedis.exists(table.getName());
    }

    @Override
    public int len(Table table) {
        return (int)jedis.hlen(table.getName()).longValue();
    }

    @Override
    public void del(Table table, Object pkValue) {
        jedis.hdel(table.getName(), pkValue.toString());
    }

    private static boolean isSuccess(String result) {
        return OK.equals(result);
    }

    private static final String getKey(ICacheKey ck, String key) {
        return ck + "_" + key;
    }

    @Override
    public void destroy() {
        this.release();
    }

    @Override
    public void recreateResource() throws Exception {
        this.jedis = ((RedisResourceManager)manager).jedisPool.getResource();
    }

}
