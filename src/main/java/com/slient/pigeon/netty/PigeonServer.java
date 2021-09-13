package com.slient.pigeon.netty;

import com.slient.pigeon.config.AppConfig;
import com.slient.pigeon.consts.PigeonConstants;
import com.slient.pigeon.netty.handler.AttachAddressHandler;
import com.slient.pigeon.netty.handler.CloseOnIdleStateHandler;
import com.slient.pigeon.netty.handler.CommonMetricsInboundHandler;
import com.slient.pigeon.netty.handler.PigeonHttpServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: gy
 * @Date: 2021/8/30
 */
@Slf4j
public class PigeonServer {
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;

    public void startServer(int noSSLPort) throws InterruptedException {
        int bossGroupSize = AppConfig.get("pigeon.server.boss.group.size", 2);
        int workerGroupSize = AppConfig.get("pigeon.server.worker.group.size", Runtime.getRuntime().availableProcessors());

        bossGroup = new NioEventLoopGroup(bossGroupSize, new DefaultThreadFactory("Pigeon-Http-Boss", true));
        workerGroup = new NioEventLoopGroup(workerGroupSize, new DefaultThreadFactory("Pigeon-Http-Worker", true));

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, AppConfig.get("pigeon.server.so.reuseaddr", Boolean.TRUE))
                .option(ChannelOption.SO_BACKLOG, AppConfig.get("pigeon.server.so.backlog", 128))
                .childOption(ChannelOption.SO_RCVBUF, AppConfig.get("pigeon.server.so.rcvbuf", 10485760))
                .childOption(ChannelOption.SO_SNDBUF, AppConfig.get("pigeon.server.so.sndbuf", 10485760))
                .childOption(ChannelOption.SO_KEEPALIVE, AppConfig.get("pigeon.server.so.keepalive", Boolean.TRUE))
                .childOption(ChannelOption.TCP_NODELAY, AppConfig.get("pigeon.server.tcp.nodelay", Boolean.TRUE))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new HttpResponseEncoder());
                        // 经过HttpRequestDecoder会得到N个对象HttpRequest,first HttpChunk,second HttpChunk,....HttpChunkTrailer
                        pipeline.addLast(new HttpRequestDecoder());
                        // 把HttpRequestDecoder得到的N个对象合并为一个完整的http请求对象
                        pipeline.addLast(new HttpObjectAggregator(PigeonConstants.MAX_CONTENT_LENGTH));
                        pipeline.addLast();
                    }
                });
    }

    private ChannelInitializer initChildHandler() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                addTimeoutHandler(pipeline);
                addCommonMetrics(pipeline);
                addTcpRelatedHandlers(pipeline);
                addHttpHandler(pipeline);
                addPigeonHandler(pipeline);
            }
        };
    }

    private final CloseOnIdleStateHandler closeOnIdleStateHandler = new CloseOnIdleStateHandler();

    private void addTimeoutHandler(ChannelPipeline pipeline) {
        pipeline.addLast("idle", new IdleStateHandler(0, 0,
                AppConfig.get("pigeon.server.idle", PigeonConstants.IDLE), TimeUnit.MILLISECONDS));
        pipeline.addLast("idle.timeout", closeOnIdleStateHandler);
    }

    private final CommonMetricsInboundHandler commonMetricsInboundHandler = new CommonMetricsInboundHandler();

    private void addCommonMetrics(ChannelPipeline pipeline) {
        pipeline.addLast("common-metrics", commonMetricsInboundHandler);
        pipeline.addLast("pigeon-server-logging", new LoggingHandler(LogLevel.INFO));
    }

    private final AttachAddressHandler attachAddressHandler = new AttachAddressHandler();

    private void addTcpRelatedHandlers(ChannelPipeline pipeline) {
        pipeline.addLast("attach-address", attachAddressHandler);
    }

    private void addHttpHandler(ChannelPipeline pipeline) {
        // 经过HttpRequestDecoder会得到N个对象HttpRequest,first HttpChunk,second HttpChunk,....HttpChunkTrailer
        pipeline.addLast("decoder", new HttpRequestDecoder());
        // 把HttpRequestDecoder得到的N个对象合并为一个完整的http请求对象
        pipeline.addLast("aggregator", new HttpObjectAggregator(AppConfig.get("pigeon.server.max.content.length", PigeonConstants.MAX_CONTENT_LENGTH)));
        pipeline.addLast("encoder", new HttpResponseEncoder());
    }

    private void addPigeonHandler(ChannelPipeline pipeline) {
        pipeline.addLast("pigeon-filter", new PigeonHttpServerHandler());
    }
}
