package com.slient.pigeon.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.google.common.hash.Hashing;
import com.slient.pigeon.config.AppConfig;
import com.slient.pigeon.http.ServiceInstance;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Author: gy
 * @Date: 2021/9/1
 */
@Slf4j
public class NacosInitializer {
    private final static Pattern pattern = Pattern.compile("\n|\r\n");

    public static void initConfig(String nacosAddr, String appName, String appGroup, String logDir) throws NacosException {
        System.setProperty("nacos.logging.path", logDir);

        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, nacosAddr);

        ConfigService configService = NacosFactory.createConfigService(properties);
        String content = configService.getConfig(appName, appGroup, 5000);
        log.info("init load nacos config: {}", content);

        AppConfig.refreshNacosConfig(getConfigsFromNacosContent(content));

        // 注册监听
        configService.addListener(appName, appGroup, new Listener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                log.info("receive nacos config: {}", configInfo);
                AppConfig.refreshNacosConfig(getConfigsFromNacosContent(configInfo));
            }

            @Override
            public Executor getExecutor() {
                return null;
            }
        });
    }

    private static Map<String, String> getConfigsFromNacosContent(String content) {
        if (StringUtils.isBlank(content)) {
            return null;
        }

        String[] lines = content.split(pattern.toString());

        Map<String, String> configTmp = new HashMap<>(lines.length);
        for (String line : lines) {
            line = line.trim();
            /** #开头的都认为是注释，忽略 */
            if (line.startsWith("#")) {
                continue;
            }
            /** 以key=value这样的形式作为键值对配置 */
            int index = line.indexOf("=");
            if (index < 0) {
                continue;
            }
            String key = line.substring(0, index).trim();
            String value = line.substring(index + 1).trim();
            if (StringUtils.isBlank(key)) {
                continue;
            }
            configTmp.put(key, value);
        }
        return configTmp;
    }

    private static NamingService namingService;

    public static void register(String nacosAddr, String appName, String appGroup, String ip, int port) throws NacosException {
        if (StringUtils.isBlank(appName)) {
            log.error("nacos discovery can not find appName");
            return;
        }
        if (StringUtils.isBlank(appGroup)) {
            log.error("nacos discovery can not find appGroup");
            return;
        }
        if (StringUtils.isBlank(ip)) {
            log.error("nacos discovery can not find ip");
            return;
        }
        if (port <= 0) {
            log.error("nacos discovery can not find port");
            return;
        }
        namingService = NamingFactory.createNamingService(nacosAddr);
        namingService.registerInstance(appName, appGroup, ip, port);
    }

    public static ServiceInstance chooseInstance(String serviceName, String groupName) {
        try {
            Instance instance = namingService.selectOneHealthyInstance(serviceName, groupName, true);

            log.debug("chooseInstance(serviceName:%s,groupName:%s) selected instance = {}", instance);

            return instance == null ? null : new ServiceInstance(serviceName, instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            log.error(String.format("chooseInstance(serviceName:%s,groupName:%s) occur exception", serviceName, groupName), e);
            return null;
        }
    }

    public static ServiceInstance chooseInstanceWithConsistentHash(String serviceName, String groupName, long key, long slotNum) {
        try {
            List<Instance> instances = namingService.selectInstances(serviceName, groupName, true, true);

            if (CollectionUtils.isEmpty(instances)) {
                return null;
            }

            int nodeCount = instances.size();

            long mask = key % slotNum;
            int selectedIndex = Hashing.consistentHash(mask, nodeCount); // 使用Guava的一致性哈希算法

            Instance instance = instances.get(selectedIndex);
            log.info("key = {} ,选中的instance = {}", key, instance);
            return new ServiceInstance(serviceName, instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            log.error("NacosInstanceSelector chooseInstanceWithConsistentHash 发生异常", e);
            return null;
        }
    }
}
