package com.slient.pigeon.netty.handler;

import com.slient.pigeon.consts.PigeonConstants;
import com.slient.pigeon.context.PigeonContext;
import com.slient.pigeon.filter.PigeonTask;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @Description:
 * @Author: gy
 * @Date: 2021/8/30
 */
@Slf4j
@ChannelHandler.Sharable
public class PigeonHttpServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
            log.info("FullHttpRequest is : " + fullHttpRequest);

            String cookieStr = fullHttpRequest.headers().get(HttpHeaderNames.COOKIE);
            Set<Cookie> cookies = null;
            if (cookieStr != null) {
                cookies = ServerCookieDecoder.STRICT.decode(cookieStr);
            }

            PigeonContext pigeonContext = ctx.channel().attr(PigeonConstants.ATTR_PIGEON_CONTEXT).get();
            pigeonContext.setOriginFullHttpRequest(fullHttpRequest);
            pigeonContext.setOriginHttpResponse(null);
            pigeonContext.setIsKeepAlive(HttpUtil.isKeepAlive(fullHttpRequest));
            pigeonContext.setCookies(cookies);

            //执行 Pigeon Filter
            PigeonTask.work(pigeonContext);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        PigeonContext pigeonContext = ctx.channel().attr(PigeonConstants.ATTR_PIGEON_CONTEXT).get();
        pigeonContext.setFrontendChannel(ctx.channel());

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        PigeonContext pigeonContext = ctx.channel().attr(PigeonConstants.ATTR_PIGEON_CONTEXT).get();
        if (null != pigeonContext) {
            pigeonContext.releaseReferenceCounted();
        }

        super.channelInactive(ctx);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("pigeon netty server caught exception", cause);
        PigeonContext pigeonContext = ctx.channel().attr(PigeonConstants.ATTR_PIGEON_CONTEXT).get();
        if (null != pigeonContext) {
            pigeonContext.releaseReferenceCounted();
        }
        ctx.close();
        super.exceptionCaught(ctx, cause);
    }
}
