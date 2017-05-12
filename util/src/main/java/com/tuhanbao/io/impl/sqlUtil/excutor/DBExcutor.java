package com.tuhanbao.io.impl.sqlUtil.excutor;

import java.io.IOException;
import java.util.List;

import com.tuhanbao.io.impl.tableUtil.ImportTable;
import com.tuhanbao.io.impl.tableUtil.XlsTable;
import com.tuhanbao.util.db.conn.DBSrc;

public abstract class DBExcutor
{
    public abstract String getSql(List<XlsTable> tables) throws IOException;
    
    //因为需要对比老数据库，所以这里需要传入dbSrc
    public abstract String getSql(ImportTable table, DBSrc dbSrc) throws IOException;
}
