package com.slient.pigeon.filter.post;

import com.slient.pigeon.consts.PigeonFilterConstants;
import com.slient.pigeon.filter.AbstractFilter;
import com.slient.pigeon.filter.FilterContext;
import com.slient.pigeon.http.HttpCode;
import com.slient.pigeon.http.ServiceInstance;
import com.slient.pigeon.http.async.PigeonAsyncHttpClient;
import org.apache.http.protocol.HTTP;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

/**
 * @Description:
 * @Author: gy
 * @Date: 2021/9/13
 */
public class PostFilter extends AbstractFilter {
    @Override
    public int order() {
        return 300;
    }

    @Override
    public String type() {
        return PigeonFilterConstants.FILTER_TYPE_POST;
    }

    @Override
    public void run(FilterContext context) {
        if (context.getThrowable() != null) {
            return;
        }

        ServiceInstance instance = context.getSelectedServiceInstance();
        BoundRequestBuilder builder = PigeonAsyncHttpClient.client()
                .preparePost("http://" + instance.getIp() + ":" + instance.getPort() + context.getGatewayRequest().getUri());

        builder.setHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8");
        builder.setRequestTimeout(5000);
        builder.setBody(context.getGatewayRequest().getBodyStr());

        try {
            ListenableFuture<Response> future = builder.execute();
            String response = future.get().getResponseBody();
            context.setResponse(HttpCode.OK_STATUS, response);
        } catch (Exception e) {
            throw new RuntimeException(String.format("post(%s) occur exception.", instance), e);
        }
    }
}
