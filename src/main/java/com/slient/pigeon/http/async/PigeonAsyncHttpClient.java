package com.slient.pigeon.http.async;

import com.slient.pigeon.config.AppConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Dsl;

@Slf4j
public class PigeonAsyncHttpClient {
    private PigeonAsyncHttpClient() {
        init();
    }

    private static class InstanceHolder {
        private static final PigeonAsyncHttpClient instance = new PigeonAsyncHttpClient();
    }

    public static AsyncHttpClient client() {
        return PigeonAsyncHttpClient.InstanceHolder.instance.getAsyncHttpClient();
    }

    @Getter
    private AsyncHttpClient asyncHttpClient;

    private synchronized void init() {
        if (null != asyncHttpClient) {
            return;
        }
        DefaultAsyncHttpClientConfig.Builder asyncHttpClientBuilder = Dsl.config();
        asyncHttpClientBuilder.setStrict302Handling(false);
        asyncHttpClientBuilder.setConnectionTtl(AppConfig.get("http_connect_ttl", 15000));
        asyncHttpClientBuilder.setConnectTimeout(AppConfig.get("http_connect_timeout", 15000));
        asyncHttpClientBuilder.setMaxConnections(AppConfig.get("http_max_connect", 5000));
        asyncHttpClientBuilder.setMaxConnectionsPerHost(AppConfig.get("http_max_connect_per_host", 460));
        asyncHttpClient = Dsl.asyncHttpClient(asyncHttpClientBuilder);
    }
}