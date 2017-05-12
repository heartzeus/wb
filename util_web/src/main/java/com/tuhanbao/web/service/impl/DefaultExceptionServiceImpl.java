package com.tuhanbao.web.service.impl;

import org.springframework.stereotype.Service;

import com.tuhanbao.web.service.IExceptionService;

/**
 * Created by dell on 2016/6/16.
 */
@Service
public class DefaultExceptionServiceImpl implements IExceptionService {
    @Override
    public String findExceptionMsg(String errClassName) {
    	
        return "未知的错误信息"+errClassName;
    }
}
