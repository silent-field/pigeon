package com.slient.pigeon.filter;

import com.slient.pigeon.context.PigeonContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @Author: gy
 * @Date: 2021/8/30
 */
@Slf4j
public class PigeonTask {
    public static Boolean work(PigeonContext pigeonContext) {
        FilterContext context = new FilterContext(pigeonContext);
        try {
            preRoute(context);
            route(context);
        } catch (Throwable e) {
            context.setThrowable(e);
            error(context);
        } finally {
            try {
                post(context);
            } catch (Throwable e) {
                context.setThrowable(e);
                error(context);
            } finally {
                finish(context);
            }
        }
        return true;
    }

    public static void preRoute(FilterContext context) {
        FilterProcessor.instance().pre(context);
    }

    public static void route(FilterContext context) {
        FilterProcessor.instance().route(context);
    }

    public static void post(FilterContext context) {
        FilterProcessor.instance().post(context);
    }

    public static void error(FilterContext context) {
        FilterProcessor.instance().error(context);
    }

    public static void finish(FilterContext context) {
        FilterProcessor.instance().finish(context);
    }
}
