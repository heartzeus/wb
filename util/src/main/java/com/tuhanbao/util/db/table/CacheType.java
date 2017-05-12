package com.tuhanbao.util.db.table;

public enum CacheType
{
    /**
     * 自动缓存
     */
    AUTO, 
    
    /**
     * 完全不进行缓存
     */
    NOT_CACHE, 
    
    /**
     * 强行缓存所有 
     */
    CACHE_ALL
}
