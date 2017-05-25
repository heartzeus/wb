package com.tuhanbao.thirdapi.cache;

import com.tuhanbao.base.dataservice.IDataGroup;
import com.tuhanbao.util.db.IField;

public interface ICacheKey extends IDataGroup<IField> {
    boolean isExpire();
    
    int getExpireTime();
}
