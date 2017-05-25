package com.tuhanbao.thirdapi.cache.mem;

import com.tuhanbao.thirdapi.cache.ICacheResource;
import com.tuhanbao.thirdapi.cache.CacheResourceFactory;
import com.tuhanbao.util.IResource;
import com.tuhanbao.util.IResourceManager;
import com.tuhanbao.util.exception.MyException;

public class MemCacheManager extends IResourceManager implements CacheResourceFactory {
    
    private static final MemCacheManager INSTANCE = new MemCacheManager();
    
    private MemResource RESOURCE;
    
    private MemCacheManager() {
        super(0, 0, -1, false);
        RESOURCE = new MemResource(this);
    }
    
    public static final MemCacheManager getInstance() {
        return INSTANCE;
    }

    @Override
    public ICacheResource getCacheResource() {
        return RESOURCE;
    }
    
    public synchronized IResource getResource() throws Exception {
        return RESOURCE;
    }

    @Override
    protected IResource createResource() throws Exception {
        throw new MyException("can not invoke this method!");
    }
}
