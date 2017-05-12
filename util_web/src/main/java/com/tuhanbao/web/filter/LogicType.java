package com.tuhanbao.web.filter;

import java.util.Collection;

public enum LogicType implements ISqlItem {
	
	AND(1, " and "),
	OR(2, " or ");
    
    public int value;
    
    private String name;
    
	private LogicType(int value, String name) {
	    this.value = value;
		this.name = name;
	}
    
    /**
     * isList = true时请重写
     * @return
     */
    public Collection<Object> listValue() {
        return null;
    }
    

    public boolean isList() {
        return false;
    }

    public String getSql() {
        return name;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public static LogicType getLogicType(int value) {
        for (LogicType lt : LogicType.values()) {
            if (lt.value == value) return lt;
        }
        return null;
    }

}
