package com.tuhanbao.web.filter;

import com.tuhanbao.io.base.Constants;
import com.tuhanbao.util.db.table.Column;

public class OrderColumn
{
    private Column col;
    
    private boolean isDesc = false;
    
    public OrderColumn(Column col, boolean isDesc) {
    	this.col = col;
    	this.isDesc = isDesc;
    }
    
    @Override
    public String toString()
    {
        return col.getNameWithTable() + Constants.BLANK + (isDesc ? "desc" : "asc");
    }
}
