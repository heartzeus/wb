package com.tuhanbao.thirdapi.aliyun.oss;

import com.tuhanbao.util.config.Config;
import com.tuhanbao.util.config.ConfigManager;
import com.tuhanbao.util.config.ConfigRefreshListener;

public final class OSSConfig implements ConfigRefreshListener {
    public static final String KEY = "oss";

    public static String BUCKET_NAME;
    public static String ENDPOINT;
    public static String ACCESS_KEY;
    public static String ACCESS_KEY_SECRET;
    public static int EXPIRATION_TIME;
    public static String ROOT_DIC;

    private static String BUCKET_NAME_STR = "bucket_name";
    private static String ENDPOINT_STR = "endpoint";
    private static String ACCESS_KEY_STR = "access_key";
    private static String ACCESS_KEY_SECRET_STR = "access_key_secret";
    private static String EXPIRATION_TIME_STR = "expiration_time";
    private static String ROOT_DIC_STR = "root_dic";

    static {
        ConfigManager.addListener(new OSSConfig());
        init();
    }

    private OSSConfig() {

    }

    public static void init() {
        Config config = ConfigManager.getConfig(KEY);
        BUCKET_NAME = config.getString(BUCKET_NAME_STR);
        ENDPOINT = config.getString(ENDPOINT_STR);
        ACCESS_KEY = config.getString(ACCESS_KEY_STR);
        ACCESS_KEY_SECRET = config.getString(ACCESS_KEY_SECRET_STR);
        EXPIRATION_TIME = config.getInt(EXPIRATION_TIME_STR);
        ROOT_DIC = config.getString(ROOT_DIC_STR);
    }

    public void refresh() {
        init();
    }

    public String getKey() {
        return KEY;
    }

}