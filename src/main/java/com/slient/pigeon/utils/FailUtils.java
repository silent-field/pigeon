package com.slient.pigeon.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @Author: gy
 * @Date: 2021/8/30
 */
@Slf4j
public class FailUtils {
    public static void fail(String message) {
        log.error("Error: {}", message);

        System.exit(-1);
    }
}
