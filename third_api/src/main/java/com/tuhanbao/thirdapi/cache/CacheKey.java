package com.tuhanbao.thirdapi.cache;

public enum CacheKey implements ICacheKey {
    TOKEN(60 * 60 * 24 * 7), 
    
    //短信验证码
    AUTHCODE(1800),
    
    OTHER;
    
    //seconds
    private int expireTime;
    
    private CacheKey() {
        this(-1);
    }
    
    private CacheKey(int expireTime) {
        this.expireTime = expireTime;
    }
    
    public boolean isExpire() {
        return this.expireTime > 0;
    }
    
    public int getExpireTime() {
        return this.expireTime;
    }

    @Override
    public String getName() {
        return name();
    }
    
}
