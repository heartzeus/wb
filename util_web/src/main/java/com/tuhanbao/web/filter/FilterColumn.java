package com.tuhanbao.web.filter;

/**
 * 不需要特定转换的column
 * @author Administrator
 *
 */
public class FilterColumn implements IFilterColumn {
    private String sqlName;
    
    public FilterColumn(String sqlName) {
        this.sqlName = sqlName;
    }
    
    public String getSqlName() {
        return this.sqlName;
    }
}
