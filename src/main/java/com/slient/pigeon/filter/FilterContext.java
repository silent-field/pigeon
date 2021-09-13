package com.slient.pigeon.filter;

import com.slient.pigeon.context.PigeonContext;
import com.slient.pigeon.http.PigeonHttpRequest;
import com.slient.pigeon.http.ServiceInstance;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.Asserts;

/**
 * @Description:
 * @Author: gy
 * @Date: 2021/8/31
 */
@Slf4j
public class FilterContext {
    @Getter
    private PigeonContext ctx;

    @Getter
    private PigeonHttpRequest gatewayRequest;

    @Getter
    @Setter
    private ServiceInstance selectedServiceInstance;

    public FilterContext(PigeonContext ctx) {
        Asserts.notNull(ctx, "PigeonContext cannot be null");
        Asserts.notNull(ctx.getOriginFullHttpRequest(), "PigeonContext.HttpRequest cannot be null");
        Asserts.notNull(ctx.getClientIp(), "PigeonContext.ClientIp cannot be null");
        Asserts.check(ctx.getClientPort() > 0, "PigeonContext.ClientPort illegal");
        this.ctx = ctx;
        constructPigeonHttpRequest();
    }

    private void constructPigeonHttpRequest() {
        gatewayRequest = new PigeonHttpRequest(ctx.getOriginFullHttpRequest());
    }

    @Setter
    private Long startTime;

    @Getter
    @Setter
    private Throwable throwable;

    @Getter
    @Setter
    private String requestUrl;

    public void setResponse(HttpResponseStatus status, String content) {
        ByteBuf byteBuf = Unpooled.copiedBuffer(content, CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(this.getCtx().getOriginFullHttpRequest().protocolVersion(),
                status, byteBuf);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        this.ctx.setOriginHttpResponse(response);
    }
}
