package com.tuhanbao.thirdapi.cache;

public interface ICacheKey {
    boolean isExpire();
    
    int getExpireTime();

    String getName();
}
