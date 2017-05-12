package com.tuhanbao.web.controller.authority;

public interface IPermission {

    long getId();

    String getUrl();
    
    long getParentId();
    
    boolean isMenu();
    
    String getName();
    
    int getSort();
    
    String getOtherInfo();
}
