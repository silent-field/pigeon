package com.slient.pigeon.filter.error;

import com.slient.pigeon.consts.PigeonFilterConstants;
import com.slient.pigeon.filter.AbstractFilter;
import com.slient.pigeon.filter.FilterContext;
import com.slient.pigeon.http.HttpCode;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @Author: gy
 * @Date: 2021/9/13
 */
@Slf4j
public class ExceptionFilter extends AbstractFilter {
    @Override
    public int order() {
        return 400;
    }

    @Override
    public String type() {
        return PigeonFilterConstants.FILTER_TYPE_ERROR;
    }

    @Override
    public void run(FilterContext context) {
        log.error("", context.getThrowable());

        // 构造500 resp
        context.setResponse(HttpCode.ERROR_STATUS, "inner server error");
    }
}
