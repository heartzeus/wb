package com.tuhanbao.util.config;

public interface ConfigRefreshListener
{
    void refresh();
    
    String getKey();
}
