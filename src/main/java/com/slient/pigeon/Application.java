package com.slient.pigeon;

import com.slient.pigeon.config.AppConfig;
import com.slient.pigeon.consts.PigeonConstants;
import com.slient.pigeon.filter.FilterHolder;
import com.slient.pigeon.nacos.NacosInitializer;
import com.slient.pigeon.netty.PigeonServer;
import com.slient.pigeon.utils.FailUtils;
import com.slient.pigeon.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @Description:
 * @Author: gy
 * @Date: 2021/8/30
 */
@Slf4j
public class Application {
    private static int PORT = PigeonConstants.HTTP_PORT;

    public static void main(String[] args) throws Exception {
        log.info("Pigeon Application Start...");
        parseArgs(args);

        log.info("init config...");
        AppConfig.init();

        log.info("init nacos config...");
        initNacosConfig();

        log.info("init filter...");
        FilterHolder.instance();

        log.info("init metrics...");
        // TODO

        log.info("init gateway server...");
        new PigeonServer().startServer(PORT);

        // ---------- 到此所有准备就绪，注册到nacos
        log.info("register to nacos...");
        registerToNacos(PORT);

        log.info("Pigeon Application started successfully in port {}", PORT);
    }

    private static void initNacosConfig() {
        try {
            NacosInitializer.initConfig(AppConfig.getNacosAddr(), AppConfig.getAppName(), AppConfig.getNacosAppGroup(), AppConfig.getNacosLogDir());
        } catch (Exception e) {
            log.error("init nacos config failed.", e);
        }
    }

    private static void registerToNacos(int port) {
        try {
            NacosInitializer.register(AppConfig.getNacosAddr(), AppConfig.getAppName(), AppConfig.getNacosAppGroup(), IpUtils.getIp(), port);
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (Exception e) {
            log.error("register to nacos failed...", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析命令行参数
     * @param args
     */
    private static void parseArgs(String[] args) {
        if (ArrayUtils.isNotEmpty(args)) {
            for (int i = 0; i < args.length; i++) {
                String paramPair = args[i];

                if (paramPair.startsWith("-httpPort")) {
                    String paramValue = paramPair.split("=")[1];
                    if (paramValue.matches("[0-9]*")) {
                        PORT = Integer.parseInt(paramValue);
                    } else {
                        FailUtils.fail("httpPort must be a positive integer.");
                        break;
                    }

                    if (PORT > 65535) {
                        FailUtils.fail("httpPort must be less than 65536.");
                        break;
                    }
                }
            }
        }
    }
}
