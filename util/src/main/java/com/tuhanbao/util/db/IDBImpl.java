package com.tuhanbao.util.db;

import java.sql.SQLException;

public interface IDBImpl
{
    void excute(String sql) throws SQLException;
}
