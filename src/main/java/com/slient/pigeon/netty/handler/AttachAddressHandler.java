package com.slient.pigeon.netty.handler;

import com.slient.pigeon.consts.PigeonConstants;
import com.slient.pigeon.context.PigeonContext;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.net.*;

/**
 * @Description:
 * @Author: gy
 * @Date: 2021/9/2
 */
@Slf4j
@ChannelHandler.Sharable
public final class AttachAddressHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress remoteSocketAddress = ctx.channel().remoteAddress();
        InetSocketAddress remoteInetSocketAddress = inetSocketAddress(remoteSocketAddress);
        String remoteHost = getHostAddress(remoteInetSocketAddress);
        int remotePort = remoteInetSocketAddress.getPort();

//        SocketAddress localSocketAddress = ctx.channel().localAddress();
//        InetSocketAddress localInetSocketAddress = inetSocketAddress(localSocketAddress);
//        String localHost = getHostAddress(localInetSocketAddress);
//        int localPort = localInetSocketAddress.getPort();

        PigeonContext pigeonContext = new PigeonContext();
        pigeonContext.setRemoteAddress(remoteSocketAddress);
        pigeonContext.setRemoteInetAddress(remoteInetSocketAddress);
        pigeonContext.setRemoteHost(remoteHost);
        pigeonContext.setRemotePort(remotePort);

//        pigeonContext.setLocalAddress(localSocketAddress);
//        pigeonContext.setLocalInetAddress(localInetSocketAddress);
//        pigeonContext.setLocalHost(localHost);
//        pigeonContext.setLocalPort(localPort);

        ctx.channel().attr(PigeonConstants.ATTR_PIGEON_CONTEXT).setIfAbsent(pigeonContext);

        log.info("channelActive... remoteSocketAddress : {}, remoteInetSocketAddress : {}, remoteHost : {}, remotePort : {}",
                remoteSocketAddress, remoteInetSocketAddress, remoteHost, remotePort);
        super.channelActive(ctx);
    }

    private InetSocketAddress inetSocketAddress(SocketAddress remoteAddress) {
        if (null != remoteAddress && InetSocketAddress.class.isAssignableFrom(remoteAddress.getClass())) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) remoteAddress;
            if (inetSocketAddress.getAddress() != null) {
                return inetSocketAddress;
            }
        }
        return null;
    }

    private String getHostAddress(InetSocketAddress socketAddress) {
        if (socketAddress == null) {
            return null;
        }

        InetAddress address = socketAddress.getAddress();
        if (address instanceof Inet6Address) {
            try {
                return InetAddress.getByAddress(address.getAddress()).getHostAddress();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        } else if (address instanceof Inet4Address) {
            return address.getHostAddress();
        } else {
            return null;
        }
    }
}
