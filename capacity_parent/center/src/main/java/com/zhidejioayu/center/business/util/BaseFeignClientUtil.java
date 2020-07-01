package com.zhidejioayu.center.business.util;

import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejioayu.center.business.wechat.feignclient.qy.BaseQyFeignClient;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

/**
 * @author: wuchenxi
 * @date: 2020/7/1 10:02:02
 */
@Component
public class BaseFeignClientUtil {

    private static Map<String, BaseQyFeignClient> qyServerFeignClientStatic;

    @Resource
    private Map<String, BaseQyFeignClient> qyServerFeignClient;

    @PostConstruct
    public void init() {
        qyServerFeignClientStatic = this.qyServerFeignClient;
    }

    /**
     * 通过教师openId获取企业微信指定服务的feignClient
     *
     * @param openId
     * @return
     */
    public static BaseQyFeignClient getBaseQyFeignClient(String openId) {
       ServerConfig serverConfig = UserInfoUtil.getServerInfoByTeacherOpenid(openId);
       return qyServerFeignClientStatic.get(serverConfig.getServerName());
    }
}
