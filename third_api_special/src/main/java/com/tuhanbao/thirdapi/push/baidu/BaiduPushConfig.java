/**
 * 
 */
package com.tuhanbao.thirdapi.push.baidu;

import com.tuhanbao.util.config.Config;
import com.tuhanbao.util.config.ConfigManager;
import com.tuhanbao.util.config.ConfigRefreshListener;
import com.tuhanbao.util.log.LogManager;

/**
 * 2016年10月25日
 * 
 * @author liuhanhui
 */
public class BaiduPushConfig implements ConfigRefreshListener {

    public static String APIKEY;
    public static String SECRETKEY;

    private static final String KEY_APIKEY = "apiKey";
    private static final String KEY_SECRETKEY = "secretKey";
    private static final String KEY = "baidupush";
    static {
        init();
    }

    private static void init() {
        Config config = ConfigManager.getConfig(KEY);
        if (config == null) {
            LogManager.warn("no config file for : " + KEY);
            return;
        }
        
        APIKEY = config.getString(KEY_APIKEY);
        SECRETKEY = config.getString(KEY_SECRETKEY);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tuhanbao.util.config.ConfigRefreshListener#refresh()
     */
    @Override
    public void refresh() {
        init();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tuhanbao.util.config.ConfigRefreshListener#getKey()
     */
    @Override
    public String getKey() {
        return KEY;
    }
}
