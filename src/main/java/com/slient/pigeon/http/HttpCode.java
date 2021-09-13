package com.slient.pigeon.http;

import io.netty.handler.codec.http.HttpResponseStatus;

public class HttpCode {
    public final static int OK = 200;
    public final static int ERROR = 500;

    public final static HttpResponseStatus OK_STATUS = HttpResponseStatus.valueOf(OK);
    public final static HttpResponseStatus ERROR_STATUS = HttpResponseStatus.valueOf(ERROR);
}