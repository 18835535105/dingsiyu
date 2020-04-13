package com.zhidejiaoyu.aliyuntranslate.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.zhidejiaoyu.aliyuncommon.constant.AliyunInfoConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 机器翻译端配置
 *
 * @author: wuchenxi
 * @date: 2020/3/6 14:59:59
 */
@Configuration
public class TranslateClientConfig {

    @Bean(name = "translateClient")
    public DefaultAcsClient initDefaultAcsClient() {
        String regionId = "cn-hangzhou";
        DefaultProfile profile = DefaultProfile.getProfile(regionId, AliyunInfoConstant.accessKeyId, AliyunInfoConstant.accessKeySecret);
        return new DefaultAcsClient(profile);
    }
}
