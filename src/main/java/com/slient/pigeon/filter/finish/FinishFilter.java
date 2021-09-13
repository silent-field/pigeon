package com.slient.pigeon.filter.finish;

import com.slient.pigeon.consts.PigeonFilterConstants;
import com.slient.pigeon.filter.AbstractFilter;
import com.slient.pigeon.filter.FilterContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @Description:
 * @Author: gy
 * @Date: 2021/9/13
 */
public class FinishFilter extends AbstractFilter {
    @Override
    public int order() {
        return 500;
    }

    @Override
    public String type() {
        return PigeonFilterConstants.FILTER_TYPE_FINISH;
    }

    @Override
    public void run(FilterContext context) {
        FullHttpResponse fullHttpResponse = (FullHttpResponse) context.getCtx().getOriginHttpResponse();
        if (fullHttpResponse == null) {
            context.setResponse(HttpResponseStatus.OK, "response is null");
        }

        context.getCtx().responseToClient();
    }
}
