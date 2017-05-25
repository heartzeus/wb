package com.tuhanbao.web.filter;

/**
 * 不需要特定转换的column
 * 用于自定义过滤条件
 * 
 * @author Administrator
 *
 */
public class FilterField implements IFilterField {
    private String sqlName;
    
    public FilterField(String sqlName) {
        this.sqlName = sqlName;
    }
    
    public String getSqlName() {
        return this.sqlName;
    }
}
