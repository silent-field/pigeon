package com.slient.pigeon.http;

import com.google.common.collect.ImmutableMap;
import com.slient.pigeon.consts.PigeonConstants;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: gy
 * @Date: 2021/8/30
 */
@Slf4j
public class PigeonHttpRequest {
    private FullHttpRequest httpRequest;

    private Map<String, List<String>> parameterMap;

    private QueryStringDecoder queryStringDecoder;

    @Getter
    private String serviceName;

    public PigeonHttpRequest(FullHttpRequest httpRequest) {
        this.httpRequest = httpRequest;
        queryStringDecoder = new QueryStringDecoder(httpRequest.uri());
        serviceName = getHeader(PigeonConstants.HEADER_SERVICE_NAME);
    }

    public String getHeader(String name) {
        return httpRequest.headers().get(name);
    }

    public Map<String, String> getHeaders() {
        return ImmutableMap.copyOf(httpRequest.headers());
    }

    public String getContentType() {
        return getHeader(HttpHeaders.Names.CONTENT_TYPE);
    }

    public String getMethod() {
        return httpRequest.method().name().toUpperCase();
    }

    public Map<String, List<String>> getParameterMap() {
        initParams();
        return parameterMap;
    }

    public String getParameter(String name) {
        initParams();
        List<String> list = getParameters(name);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public List<String> getParameters(String name) {
        initParams();
        return parameterMap.get(name);
    }

    private void initParams() {
        if (parameterMap == null) {
            parameterMap = new HashMap<>();
            // url传参
            Map<String, List<String>> strDecoder = queryStringDecoder.parameters();
            if (strDecoder != null) {
                parameterMap.putAll(strDecoder);
            }

            // form 传参
            if (HttpMethod.POST.name().equalsIgnoreCase(getMethod())) {
                if (HttpHeaders.Values.APPLICATION_X_WWW_FORM_URLENCODED.equalsIgnoreCase(getContentType())) {
                    Map<String, List<String>> postMap = getParameterMapWithBody();
                    parameterMap.putAll(postMap);
                }
            }
        }
    }

    private Map<String, List<String>> getParameterMapWithBody() {
        Map<String, List<String>> result = new HashMap<>();
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(
                new DefaultHttpDataFactory(false), httpRequest);

        List<InterfaceHttpData> dataList = decoder.getBodyHttpDatas();
        for (InterfaceHttpData data : dataList) {
            if (data != null) {
                try {
                    if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                        Attribute attribute = (Attribute) data;
                        List<String> strList = result.computeIfAbsent(data.getName(), k -> new ArrayList<>());
                        strList.add(attribute.getValue());
                    }
                } catch (IOException e) {
                }
            }
        }

        return result;
    }

    public String getPath() {
        return queryStringDecoder.path();
    }

    public String getUri() {
        return queryStringDecoder.uri();
    }

    public String getBodyStr() {
        return httpRequest.content().toString(CharsetUtil.UTF_8);
    }
}
