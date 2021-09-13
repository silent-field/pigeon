package com.slient.pigeon.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description: 记录一些指标，例如连接总数，连接总失效数，总异常数
 * @Author: gy
 * @Date: 2021/9/3
 */
@ChannelHandler.Sharable
public class CommonMetricsInboundHandler extends ChannelInboundHandlerAdapter {
    public final static AtomicLong totalConnections = new AtomicLong(0L);
    public final static AtomicLong totalActiveConnections = new AtomicLong(0L);
    public final static AtomicLong connectionClosed = new AtomicLong(0L);
    public final static AtomicLong connectionErrors = new AtomicLong(0L);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        totalConnections.incrementAndGet();
        totalActiveConnections.incrementAndGet();
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        connectionClosed.incrementAndGet();
        totalActiveConnections.decrementAndGet();
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        connectionErrors.incrementAndGet();
        super.exceptionCaught(ctx, cause);
    }
}
