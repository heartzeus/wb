package com.tuhanbao.thirdapi.cache;

import java.util.List;

import com.tuhanbao.base.ServiceBean;
import com.tuhanbao.thirdapi.cache.mem.MemCacheManager;
import com.tuhanbao.thirdapi.cache.redis.RedisResourceManager;
import com.tuhanbao.util.config.ConfigManager;
import com.tuhanbao.util.db.table.Table;
import com.tuhanbao.util.exception.MyException;

public class CacheManager {
    private static final String REDIS_KEY = "redis";

    private static CacheResourceFactory getCacheResourceFactory() {
        if (ConfigManager.getConfig(REDIS_KEY) != null) {
            return RedisResourceManager.getInstance();
        }
        else {
            return MemCacheManager.getInstance();
        }
    }

    /**
     * 向缓存中设置对象
     * 
     * @param key
     * @param value
     * @return
     */
    public static int set(ICacheKey ck, String key, Object value) {
        CacheResource cacheResource = getCacheResourceFactory().getCacheResource();
        int result = cacheResource.set(ck, key, value);
        cacheResource.release();
        return result;
    }

    /**
     * 向缓存中设置对象，而且规定期限
     * 
     * redis不支持对map中的key设置期限，所以此方法需要自行生成唯一key
     * 
     * @param key
     * @param value
     * @param time seconds
     * @return
     */
    public static int setex(ICacheKey ck, String key, int seconds, Object value) {
        CacheResource cacheResource = getCacheResourceFactory().getCacheResource();
        int result = cacheResource.setex(ck, key, seconds, value);
        cacheResource.release();
        return result;
    }

    /**
     * 删除缓存中得对象，根据key
     * 
     * @param key
     * @return
     */
    public static int delete(ICacheKey ck, String key) {
        // 返回的是删除的条数
        CacheResource cacheResource = getCacheResourceFactory().getCacheResource();
        int result = cacheResource.del(ck, key);
        cacheResource.release();
        return result;
    }

    /**
     * 根据key 获取内容
     * 
     * @param key
     * @return
     */
    public static Object get(ICacheKey ck, String key) {
        CacheResource cacheResource = getCacheResourceFactory().getCacheResource();
        Object result = cacheResource.get(ck, key);
        cacheResource.release();
        return result;
    }

    /**
     * 2016年10月24日
     * 
     * @author liuhanhui
     * @param key
     * @return
     */
    public static boolean isExist(ICacheKey ck, String key) {
        CacheResource cacheResource = getCacheResourceFactory().getCacheResource();
        boolean result = cacheResource.exists(ck, key);
        cacheResource.release();
        return result;
    }

    /**
     * 根据key 获取内容并转换成对象
     * 
     * @param key
     * @return
     */
    public static <T> T get(ICacheKey ck, String key, Class<T> clazz) {
        CacheResource cacheResource = getCacheResourceFactory().getCacheResource();
        T result = cacheResource.get(ck, key, clazz);
        cacheResource.release();
        return result;
    }

    /*
     * servicebean的操作
     */
    public static void save(List<ServiceBean> beans) {
        if (beans == null || beans.isEmpty()) return;

        CacheResource cacheResource = getCacheResourceFactory().getCacheResource();
        for (ServiceBean bean : beans) {
            cacheResource.save(bean);
        }
        cacheResource.release();
    }

    public static boolean hasCacheTable(Table table) {
        CacheResource cacheResource = getCacheResourceFactory().getCacheResource();
        boolean result = cacheResource.hasCacheTable(table);
        cacheResource.release();
        return result;
    }

    public static void save(ServiceBean bean) {
        if (bean == null) return;

        CacheResource cacheResource = getCacheResourceFactory().getCacheResource();
        cacheResource.save(bean);
        cacheResource.release();
    }

    /**
     * 只支持按主键进行保存，如果不满足条件，redis不会进行处理。返回false
     * 
     * @param bean
     * @param filter
     */
    public static void saveSelective(ServiceBean bean) {
        if (bean == null) return;
        Table table = bean.getTable();
        ServiceBean oldValue = get(table, bean.getKeyValue());
        if (oldValue == null) save(bean);
        else {
            oldValue.sync(bean);
            save(oldValue);
        }
    }

    /**
     * 只支持
     * 
     * @param filter
     * @return
     */
    public static ServiceBean get(Table table, Object pkValue) {

        CacheResource cacheResource = getCacheResourceFactory().getCacheResource();
        try {
            return cacheResource.get(table, pkValue);
        }
        catch (ClassNotFoundException e) {
            throw MyException.getMyException(e);
        }
        finally {
            cacheResource.release();
        }
    }

    public static List<ServiceBean> get(Table table) {

        CacheResource cacheResource = getCacheResourceFactory().getCacheResource();
        try {
            return cacheResource.get(table);
        }
        catch (ClassNotFoundException e) {
            throw MyException.getMyException(e);
        }
        finally {
            cacheResource.release();
        }
    }

    public static int count(Table table) {
        CacheResource cacheResource = getCacheResourceFactory().getCacheResource();
        int result = cacheResource.len(table);
        cacheResource.release();
        return result;
    }

    public static void delete(Table table, Object pkValue) {
        CacheResource cacheResource = getCacheResourceFactory().getCacheResource();
        cacheResource.del(table, pkValue);
        cacheResource.release();
    }

    // public static void main(String args[]) {
    // ServerManager.start();
    // String key = "tubie";
    // set(ICacheKey.TOKEN, key, new Test(1, "123"));
    // System.out.println(get(ICacheKey.TOKEN, key, Test.class));
    // set(ICacheKey.TOKEN, key, "456");
    // System.out.println(get(ICacheKey.TOKEN, key));
    // delete(ICacheKey.TOKEN, key);
    // System.out.println(get(ICacheKey.TOKEN, key));
    // set(ICacheKey.TOKEN, key, "789");
    // System.out.println(get(ICacheKey.TOKEN, key));
    // REDIS_MANAGER = new RedisResourceManager(RedisConfig.RESOURCE_NUM);
    // // -----添加数据----------
    // Map<String, String> map = new HashMap<String, String>();
    // CacheResource CacheResource =
    // REDIS_MANAGER.CacheResourcePool.getResource();
    // map.put("id", "123");
    // map.put("ps", "22");
    // map.put("qq", "123456");
    // CrawlerClass cc = new CrawlerClass();
    // cc.setId(123L);
    // cc.setPs("psname");
    // save(cc);
    // CacheResource.hmset("tubie", map);
    // ServiceBean sb = get(cc.getTable(), 123);
    // // 取出user中的name，执行结果:[minxr]-->注意结果是一个泛型的List
    // // 第一个参数是存入redis中map对象的key，后面跟的是放入map中的对象的key，后面的key可以跟多个，是可变参数
    // List<String> rsmap = CacheResource.hmget("tubie", "name", "ps", "qq");
    // System.out.println(rsmap);
    // // 删除map中的某个键值
    //// CacheResource.hdel("tubie", "age");
    // CacheResource.hset("tubie", "qq3", "14563789");
    // CacheResource.hset("tubie", "qq2", "132456");
    // System.out.println(CacheResource.hmget("tubie", "age")); //
    // 因为删除了，所以返回的是null
    // System.out.println(CacheResource.hlen("tubie")); // 返回key为user的键中存放的值的个数2
    // System.out.println(CacheResource.exists("tubie"));// 是否存在key为user的记录
    // 返回true
    // System.out.println(CacheResource.hkeys("tubie"));// 返回map对象中的所有key
    // System.out.println(CacheResource.hvals("tubie"));// 返回map对象中的所有value
    // Iterator<String> iter = CacheResource.hkeys("tubie").iterator();
    // while (iter.hasNext()) {
    // String key = iter.next();
    // System.out.println(key + ":" + CacheResource.hmget("tubie", key));
    // }
    // }
    //
    // private static class Test {
    // public int getId() {
    // return id;
    // }
    //
    // public Test() {
    //
    // }
    //
    // public Test(int id, String name) {
    // super();
    // this.id = id;
    // this.name = name;
    // }
    //
    // public void setId(int id) {
    // this.id = id;
    // }
    //
    // public String getName() {
    // return name;
    // }
    //
    // public void setName(String name) {
    // this.name = name;
    // }
    //
    // private int id;
    //
    // private String name;
    //
    //
    // }
}
