package com.zhidejiaoyu.student.business.wechat.publicaccount.constant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 微信公众号配置常量
 *
 * @author: wuchenxi
 * @date: 2020/4/28 10:29:29
 */
@Slf4j
@Configuration
public class ConfigConstant {

    public static String APP_ID;

    public static String SECRET;

    @Value("${gongzhonghao.appId}")
    public void setAppId(String appId) {
        APP_ID = appId;
    }

    @Value("${gongzhonghao.secret}")
    public void setSecret(String secret) {
        SECRET = secret;
    }
}
