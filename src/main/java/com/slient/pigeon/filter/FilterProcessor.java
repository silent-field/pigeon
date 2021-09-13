package com.slient.pigeon.filter;

import com.slient.pigeon.consts.PigeonFilterConstants;
import com.slient.pigeon.context.PigeonContext;

import java.util.Collection;

/**
 * @Description:
 * @Author: gy
 * @Date: 2021/8/30
 */
public class FilterProcessor {
    private FilterProcessor() {
    }

    private static class InstanceHolder {
        private static final FilterProcessor instance = new FilterProcessor();
    }

    public static FilterProcessor instance() {
        return FilterProcessor.InstanceHolder.instance;
    }

    public void pre(FilterContext context) {
        processFilters(PigeonFilterConstants.FILTER_TYPE_PRE, context);
    }

    public void route(FilterContext context) {
        processFilters(PigeonFilterConstants.FILTER_TYPE_ROUTE, context);
    }

    public void post(FilterContext context) {
        processFilters(PigeonFilterConstants.FILTER_TYPE_POST, context);
    }

    public void error(FilterContext context) {
        processFilters(PigeonFilterConstants.FILTER_TYPE_ERROR, context);
    }

    public void finish(FilterContext context) {
        processFilters(PigeonFilterConstants.FILTER_TYPE_FINISH, context);
    }

    public Object processFilters(String type, FilterContext context) {
        boolean bResult = false;
        Collection<IFilter> list = FilterHolder.instance().getByType(type);
        if (list != null) {
            for (IFilter filter : list) {
                Object result = processFilter(filter,context);
                if (result != null && result instanceof Boolean) {
                    bResult |= ((Boolean) result);
                }
            }
        }
        return bResult;
    }

    public Object processFilter(IFilter filter, FilterContext context) {
        if (filter.enable()) {
            try {
                filter.run(context);
            } catch (Throwable e) {
                throw e;
            }
        }
        return null;
    }

    public void start(PigeonContext pigeonContext) {
        FilterContext filterContext = new FilterContext(pigeonContext);


    }
}
