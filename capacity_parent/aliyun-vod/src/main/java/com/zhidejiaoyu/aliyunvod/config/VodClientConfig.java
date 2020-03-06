package com.zhidejiaoyu.aliyunvod.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.zhidejiaoyu.aliyuncommon.constant.AliyunInfoConstant;
import com.zhidejiaoyu.aliyunvod.common.AliyunInfoConst;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 视频点播客户端配置
 *
 * @author: wuchenxi
 * @date: 2020/3/6 14:59:59
 */
@Configuration
public class VodClientConfig {

    @Bean(name = "acsClient")
    public DefaultAcsClient initDefaultAcsClient() {
        DefaultProfile profile = DefaultProfile.getProfile(AliyunInfoConst.REGION_ID, AliyunInfoConstant.accessKeyId, AliyunInfoConstant.accessKeySecret);
        return new DefaultAcsClient(profile);
    }
}
