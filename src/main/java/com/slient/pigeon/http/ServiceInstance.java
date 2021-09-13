package com.slient.pigeon.http;

import lombok.Getter;

/**
 * @Description:
 * @Author: gy
 * @Date: 2021/9/13
 */
public class ServiceInstance extends Endpoint {
    @Getter
    private String serviceName;

    public ServiceInstance(String serviceName, String ip, int port) {
        super(ip, port);
        this.serviceName = serviceName;
    }
}
