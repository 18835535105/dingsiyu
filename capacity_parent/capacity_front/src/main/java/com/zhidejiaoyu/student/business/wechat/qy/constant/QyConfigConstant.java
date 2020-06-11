package com.zhidejiaoyu.student.business.wechat.qy.constant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 企业微信配置常量
 *
 * @author: wuchenxi
 * @date: 2020/4/28 10:29:29
 */
@Slf4j
@Configuration
public class QyConfigConstant {

    public static String APP_ID;

    public static String SECRET;

    @Value("${qywx.appId}")
    public void setAppId(String appId) {
        APP_ID = appId;
    }

    @Value("${qywx.secret}")
    public void setSecret(String secret) {
        SECRET = secret;
    }
}
