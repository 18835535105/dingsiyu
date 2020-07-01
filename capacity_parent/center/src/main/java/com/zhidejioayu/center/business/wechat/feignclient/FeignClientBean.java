package com.zhidejioayu.center.business.wechat.feignclient;

import com.zhidejioayu.center.business.wechat.feignclient.qy.BaseQyFeignClient;
import com.zhidejioayu.center.business.wechat.feignclient.smallapp.BaseSmallAppFeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wuchenxi
 * @date: 2020/6/29 12:34:34
 */
@Configuration
public class FeignClientBean {

    @Resource
    private BaseQyFeignClient server1QyAuthFeignClient;

    @Resource
    private BaseSmallAppFeignClient Server1SmallAppAuthFeignClient;

    /**
     * 企业微信请求不同服务的feignClient
     *
     * @return key：服务名
     * value：服务对应的feignClient实例
     */
    @Bean("qyServerFeignClient")
    public Map<String, BaseQyFeignClient> qyServerFeignClient() {
        Map<String, BaseQyFeignClient> map = new HashMap<>(16);
        map.put("server1", server1QyAuthFeignClient);
        return map;
    }

    /**
     * 微信小程序请求不同服务的feignClient
     *
     * @return key：服务名
     * value：服务对应的feignClient实例
     */
    @Bean("smallAppServerFeignClient")
    public Map<String, BaseSmallAppFeignClient> smallAppServerFeignClient() {
        Map<String, BaseSmallAppFeignClient> map = new HashMap<>(16);
        map.put("server1", Server1SmallAppAuthFeignClient);
        return map;
    }
}
