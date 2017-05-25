package com.tuhanbao.util.db;

import com.tuhanbao.base.dataservice.IDataGroup;
import com.tuhanbao.io.impl.tableUtil.DataType;

public interface IField {
    
    String getName();

    IDataGroup<?> getDataGroup();
    
    String getNameWithDataGroup();

    DataType getDataType();
}
