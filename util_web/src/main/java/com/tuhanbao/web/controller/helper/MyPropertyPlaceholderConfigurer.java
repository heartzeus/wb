package com.tuhanbao.web.controller.helper;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.tuhanbao.io.base.Constants;
import com.tuhanbao.io.objutil.FileUtil;
import com.tuhanbao.io.objutil.StringUtil;
import com.tuhanbao.util.config.ConfigManager;
import com.tuhanbao.util.log.LogManager;
import com.tuhanbao.web.ServerManager;

public class MyPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
    public MyPropertyPlaceholderConfigurer() {
        super();
        start();
    }

    public void setProperties(String properties) {
        String[] keys = StringUtil.string2Array(properties);
        Resource[] resources = new Resource[keys.length];
        boolean isDebug = ConfigManager.isDebug();
        int i = 0;
        for (String key : keys) {
            String url = null;
            if (isDebug) {
                url = ConfigManager.getPropertiesPath(key, true);
            }
            //两种情况都会进入if语句：1.debug模式但是找不到debug配置文件，2正常模式
            if (StringUtil.isEmpty(url)) {
                url = ConfigManager.getPropertiesPath(key);
            }
            resources[i] = new ClassPathResource(getSimplePath(url));
        }
        super.setLocations(resources);
    }

    private String getSimplePath(String url) {
        if (StringUtil.isEmpty(url)) {
            return url;
        }
        return url.substring(url.indexOf("init" + File.separator + "config"));
    }

    /**
     * 服务器启动
     */
    public static void start() {
        // web模式
        File f = null;
        try {
            f = new ClassPathResource(Constants.CONFIG_URL, ServerManager.class.getClassLoader()).getFile();
        }
        catch (IOException e) {
            LogManager.error(e);
            System.exit(0);
        }
        // 启动LogManager
        ConfigManager.init(f);
        Constants.CONFIG_ROOT = FileUtil.appendPath(f.getParentFile().getParent(), Constants.CONFIG_URL);
        LogManager.init(f.getParentFile().getParent() + Constants.FILE_SEP + "log4j.properties");
        
    }
}
