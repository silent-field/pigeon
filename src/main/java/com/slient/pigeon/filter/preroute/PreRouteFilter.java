package com.slient.pigeon.filter.preroute;

import com.slient.pigeon.consts.PigeonFilterConstants;
import com.slient.pigeon.filter.AbstractFilter;
import com.slient.pigeon.filter.FilterContext;

/**
 * @Description:
 * @Author: gy
 * @Date: 2021/9/13
 */
public class PreRouteFilter extends AbstractFilter {
    @Override
    public int order() {
        return 100;
    }

    @Override
    public String type() {
        return PigeonFilterConstants.FILTER_TYPE_PRE;
    }

    @Override
    public void run(FilterContext context) {
        context.setStartTime(System.currentTimeMillis());
    }
}
