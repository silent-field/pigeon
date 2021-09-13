package com.slient.pigeon.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Description: 包含本地配置以及nacos配置
 * @Author: gy
 * @Date: 2021/9/1
 */
@Slf4j
public class AppConfig {
    private static final String LOCAL_CONFIG_FILE = "application.properties";

    private volatile static Properties localConfig;
    private volatile static Map<String, String> nacosConfig = new HashMap<>();

    private static String nacosAddr;
    private static String appName = "pigeon";
    /** 使用appGroup来区分环境，而不是namespace */
    private static String nacosAppGroup;
    private static String nacosLogDir;

    public static void init() {
        localConfig = loadProperties(LOCAL_CONFIG_FILE);
        nacosAddr = localConfig.getProperty("nacos.addr");
        nacosAppGroup = localConfig.getProperty("nacos.application.group");
        nacosLogDir = localConfig.getProperty("nacos.logging.path", "/data/service/nacos/log/nacos-client");
    }

    public static void refreshNacosConfig(Map<String, String> nacosConfigNew) {
        if (nacosConfigNew == null) {
            nacosConfig = new HashMap<>();
        } else {
            nacosConfig = nacosConfigNew;
        }
    }

    private static Properties loadProperties(String propertiesFile) {
        Properties properties = new Properties();
        InputStream is = null;
        try {
            is = AppConfig.class.getClassLoader().getResourceAsStream(propertiesFile);
            properties.load(is);
        } catch (Throwable e) {
            log.error("loadLocalProperties failed.", e);
            throw new RuntimeException(e);
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("loadLocalProperties close InputStream failed.", e);
                    throw new RuntimeException(e);
                }
            }
        }
        return properties;
    }

    public static String getNacosAddr() {
        return nacosAddr;
    }

    public static String getAppName() {
        return appName;
    }

    public static String getNacosAppGroup() {
        return nacosAppGroup;
    }

    public static String getNacosLogDir() {
        return nacosLogDir;
    }

    // ----------------
    public static String get(String key) {
        return nacosConfig.get(key);
    }

    public static String get(String key, String defaultVal) {
        String value = get(key);
        return null == value ? defaultVal : value;
    }

    public static boolean get(String key, boolean defaultVal) {
        String dVal = defaultVal ? "Y" : "N";
        String val = get(key, dVal).toUpperCase().trim();
        return "Y".equals(val);
    }

    public static int get(String key, int defaultVal) {
        String value = get(key);
        return null == value ? defaultVal : Integer.valueOf(value);
    }
}
