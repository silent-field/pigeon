package com.slient.pigeon.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description: 在收到 IdleStateEvent 时关闭连接
 * @Author: gy
 * @Date: 2021/9/3
 */
@ChannelHandler.Sharable
public class CloseOnIdleStateHandler extends ChannelInboundHandlerAdapter {
    public static final AtomicLong counter = new AtomicLong(0L);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);

        if (evt instanceof IdleStateEvent) {
            counter.incrementAndGet();
            ctx.close();
        }
    }
}
