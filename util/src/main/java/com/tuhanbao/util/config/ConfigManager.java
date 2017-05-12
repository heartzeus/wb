package com.tuhanbao.util.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.tuhanbao.io.base.Constants;
import com.tuhanbao.io.base.KeyValueBean;
import com.tuhanbao.io.objutil.FileUtil;
import com.tuhanbao.io.objutil.StringUtil;

/**
 * 配置文件Manager本身也有一个配置文件
 * 
 * @author tuhanbao
 *
 */
public final class ConfigManager implements ConfigRefreshListener {
    private static final String BASE_CONFIG = "base";

    private static final String DEBUG = "_debug";

    private static final Map<String, Config> CONFIG_MAP = new HashMap<String, Config>();

    private static final Map<String, Config> DEBUG_CONFIG_MAP = new HashMap<String, Config>();

    private static final Map<String, List<ConfigRefreshListener>> listeners = new HashMap<String, List<ConfigRefreshListener>>();

    private static boolean IS_DEBUG;
    
    private static boolean IS_MAINTAINING;

    private static String CONFIG_PATH;
    
    private static boolean hasInit = false;
    
    private ConfigManager() {
    }

    public static synchronized void init(File f) {
        if (!hasInit) {
            // jar模式
            if (f == null) f = new File(Constants.CONFIG_ROOT);
            CONFIG_PATH = f.getAbsolutePath();
    
            List<File> files = new ArrayList<File>();
            addConfigFiles(files, f);
    
            if (files.isEmpty()) return;
            for (File configFile : files) {
                String name = configFile.getName();
                String key = name.substring(0, name.indexOf(Constants.STOP_EN));
    
                if (key.endsWith(DEBUG)) {
                    DEBUG_CONFIG_MAP.put(key.substring(0, key.length() - DEBUG.length()), new Config(configFile.getAbsolutePath()));
                }
                else {
                    CONFIG_MAP.put(key, new Config(configFile.getAbsolutePath()));
                }
            }
    
            refreshBaseConfig();
            
            addListener(new ConfigManager());
            hasInit = true;
        }
    }

//    public static void loadAllListeners(String packageName) {
//        // 注册所有的ConfigRefreshListener
//        try {
//            List<Class<?>> classes = ClazzUtil.getAllClasses(packageName, new IClassFilter() {
//                @Override
//                public boolean filterClass(Class<?> clazz) {
//                    return clazz.isAssignableFrom(ConfigRefreshListener.class);
//                }
//            });
//
//            for (Class<?> clazz : classes) {
//                ConfigRefreshListener item = (ConfigRefreshListener)clazz.newInstance();
//                addListener(item);
//            }
//        }
//        catch (IOException | InstantiationException | IllegalAccessException e) {
//            LogManager.error(e);
//        }
//    }

    private static void addConfigFiles(List<File> files, File f) {
        for (File file : f.listFiles()) {
            if (file.isDirectory()) {
                addConfigFiles(files, file);
            }
            else {
                if (file.getName().endsWith(".properties")) {
                    files.add(file);
                }
            }
        }
    }

    public static synchronized Config getConfig(String key) {
        Config config = null;
        //base.cfg没有debug之分
        if (isDebug() && !BASE_CONFIG.equals(key)) {
            config = DEBUG_CONFIG_MAP.get(key);
        }
        if (config == null) return CONFIG_MAP.get(key);
        else return config;
    }

    public static void addListener(ConfigRefreshListener listener) {
        if (listener == null) return;
        
        String key = listener.getKey();
        List<ConfigRefreshListener> value = listeners.get(key);
        if (value == null) {
            //一般最多两个 listener
            value = new ArrayList<ConfigRefreshListener>(2); 
            listeners.put(key, value);
        }
        value.add(listener);
    }

    public static void removeListener(String key) {
        listeners.remove(key);
    }

    public static synchronized Config getBaseConfig() {
        return getConfig(BASE_CONFIG);
    }

    public static synchronized void refreshConfig(String key) {
        String propertiesPath = getPropertiesPath(key);
        if (!StringUtil.isEmpty(propertiesPath)) {
            CONFIG_MAP.put(key, new Config(propertiesPath));
        }
        String debugPropertiesPath = getPropertiesPath(key, true);
        if (!StringUtil.isEmpty(debugPropertiesPath) && !BASE_CONFIG.equals(key)) {
            DEBUG_CONFIG_MAP.put(key, new Config(debugPropertiesPath));
        }
        configRefreshed(key);
    }

    public static synchronized void refreshConfig(String key, String content) {
        if (StringUtil.isEmpty(content)) {
            refreshConfig(key);
            return;
        }
        
        KeyValueBean bean = new KeyValueBean(content, Constants.ENTER, Constants.EQUAL);
        
        Config config = CONFIG_MAP.get(key);
        if (config != null) {
            config.addProperties(bean);
        }
        
        if (!BASE_CONFIG.equals(key)) {
            config = DEBUG_CONFIG_MAP.get(key);
            if (config != null) {
                config.addProperties(bean);
            }
        }
        
        configRefreshed(key);
    }

    public static String getPropertiesPath(String key, boolean isDebug) {
        File root = new File(CONFIG_PATH);
        File childFile = FileUtil.getChildFile(key + (isDebug ? DEBUG : Constants.EMPTY) + ".properties", root);
        if (childFile == null) return Constants.EMPTY;
        return childFile.getPath();
    }

    public static String getPropertiesPath(String key) {
        return getPropertiesPath(key, false);
    }

    /**
     * base.cfg是不会刷新的
     */
    public static synchronized void refreshAllConfig() {
        for (Entry<String, Config> entry : CONFIG_MAP.entrySet()) {
            String key = entry.getKey();
            if (BASE_CONFIG.equals(key)) {
                continue;
            }
            refreshConfig(key);
        }
    }

    private static void configRefreshed(String key) {
        if (listeners.containsKey(key)) {
            for (ConfigRefreshListener item : listeners.get(key)) {
                item.refresh();
            }
        }
    }

    public static boolean isDebug() {
        return IS_DEBUG;
    }

    public static void setDebug(boolean value) {
        IS_DEBUG = value;
    }

    public static boolean isMaintaining() {
        return IS_MAINTAINING;
    }
    
    public static void setMaintaining(boolean value) {
        IS_MAINTAINING = value;
    }

    public static String[] getAdminTelephone() {
        return StringUtil.string2Array(getBaseConfig().getString(BaseConfigConstants.ADMIN_MOBILE));
    }

    @Override
    public void refresh() {
        refreshBaseConfig();
    }
    
    public static void refreshBaseConfig() {
        Config baseConfig = getBaseConfig();
        if (baseConfig != null) {
            if (baseConfig.containsKey(BaseConfigConstants.IS_DEBUG)) {
                boolean temp = baseConfig.getBoolean(BaseConfigConstants.IS_DEBUG);
                if (IS_DEBUG != temp) {
                    IS_DEBUG = temp;
                    refreshAllConfig();
                }
            }
            
            if (baseConfig.containsKey(BaseConfigConstants.IS_MAINTAINING)) {
                IS_MAINTAINING = baseConfig.getBoolean(BaseConfigConstants.IS_MAINTAINING);
            }
        }
    }

    @Override
    public String getKey() {
        return BASE_CONFIG;
    }
}
