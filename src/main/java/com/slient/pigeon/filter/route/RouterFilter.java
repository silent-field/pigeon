package com.slient.pigeon.filter.route;

import com.slient.pigeon.config.AppConfig;
import com.slient.pigeon.consts.PigeonFilterConstants;
import com.slient.pigeon.filter.AbstractFilter;
import com.slient.pigeon.filter.FilterContext;
import com.slient.pigeon.http.ServiceInstance;
import com.slient.pigeon.nacos.NacosInitializer;

/**
 * @Description:
 * @Author: gy
 * @Date: 2021/9/13
 */
public class RouterFilter extends AbstractFilter {
    @Override
    public int order() {
        return 200;
    }

    @Override
    public String type() {
        return PigeonFilterConstants.FILTER_TYPE_ROUTE;
    }

    @Override
    public void run(FilterContext context) {
        // 暂时使用nacos作为注册中心
        String serviceName = context.getGatewayRequest().getServiceName();
        ServiceInstance serviceInstance = NacosInitializer.chooseInstance(serviceName, AppConfig.getNacosAppGroup());
        if (serviceInstance == null) {
            throw new RuntimeException("no instance found for " + serviceName);
        }

        context.setSelectedServiceInstance(serviceInstance);
    }
}
