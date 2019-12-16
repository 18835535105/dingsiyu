package com.zhidejiaoyu.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ServiceInfoUtil implements ApplicationListener<WebServerInitializedEvent> {
    private static int serverPort;

    public static int getPort() {
        return serverPort;
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        ServiceInfoUtil.serverPort = event.getWebServer().getPort();
        log.info("Get WebServer port {}", serverPort);
    }
}
