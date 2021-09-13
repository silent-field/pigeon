package com.slient.pigeon.context;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Set;

@Slf4j
@Data
public class PigeonContext {

    private volatile Channel frontendChannel;
//    private volatile Channel backendChannel;

    private volatile FullHttpRequest originFullHttpRequest;
    private volatile HttpResponse originHttpResponse;
    private Boolean isKeepAlive = true;
    private String clientIp;
    private int clientPort;
    private Set<Cookie> cookies;

    private SocketAddress remoteAddress;
    private InetSocketAddress remoteInetAddress;
    private String remoteHost;
    private Integer remotePort;

//    private SocketAddress localAddress;
//    private InetSocketAddress localInetAddress;
//    private String localHost;
//    private Integer localPort;

    public synchronized void releaseReferenceCounted() {
        if (originFullHttpRequest != null) {
            while ((originFullHttpRequest).refCnt() != 0) {
                ReferenceCountUtil.release(originFullHttpRequest);
            }
            originFullHttpRequest = null;
        }

        if (originHttpResponse != null && originHttpResponse instanceof ReferenceCounted) {
            while (((ReferenceCounted) originHttpResponse).refCnt() != 0) {
                ReferenceCountUtil.release(originHttpResponse);
            }
            originHttpResponse = null;
        }
    }

    public void responseToClient() {
        try {

            log.info("responseToClient : {}", originHttpResponse);
            if (frontendChannel != null && frontendChannel.isActive() && frontendChannel.isWritable()) {
                ChannelFuture future = frontendChannel.writeAndFlush(originHttpResponse);
                log.info("write response to the client.");
                if (!isKeepAlive) {
                    future.addListener(ChannelFutureListener.CLOSE);
                } else {
                    future.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) {
                            releaseReferenceCounted();
                        }
                    });
                }
            } else {
                log.error("client channel is already closed, can't response to client");
            }
        } catch (Exception e) {
            log.error("close client channel failed, channel info: ", e);
        }
    }

}
