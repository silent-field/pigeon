package com.slient.pigeon.consts;

import com.slient.pigeon.context.PigeonContext;
import io.netty.util.AttributeKey;

/**
 * @author gy
 * @version 1.0
 * @date 2021/3/17.
 * @description:
 */
public class PigeonConstants {
    /**默认端口*/
    public final static int HTTP_PORT = 8899;

    /**默认http content最大64M*/
    public final static int MAX_CONTENT_LENGTH = 64 * 1024 * 1024;

    public final static int IDLE = 65000;

    public final static AttributeKey<PigeonContext> ATTR_PIGEON_CONTEXT = AttributeKey.newInstance("pigeon_context");

    public final static String HEADER_SERVICE_NAME = "upstream_service_name";
}
