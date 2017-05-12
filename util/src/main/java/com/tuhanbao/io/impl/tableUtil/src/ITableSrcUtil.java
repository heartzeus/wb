package com.tuhanbao.io.impl.tableUtil.src;

import java.util.List;

import com.tuhanbao.io.impl.tableUtil.DBType;
import com.tuhanbao.io.impl.tableUtil.ImportTable;
import com.tuhanbao.util.db.conn.DBSrc;

public interface ITableSrcUtil {
    List<ImportTable> getTables(DBSrc src) throws Exception;
    
    ImportTable getTable(DBSrc src, String tableName) throws Exception;
    
    ImportTable getTable(String tableName, String[][] arrays, DBType dbType) throws Exception; 
}
