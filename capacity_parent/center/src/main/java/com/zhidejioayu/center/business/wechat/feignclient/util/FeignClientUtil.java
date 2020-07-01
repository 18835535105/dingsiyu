package com.zhidejioayu.center.business.wechat.feignclient.util;

import com.zhidejioayu.center.business.wechat.feignclient.qy.BaseQyFeignClient;
import com.zhidejioayu.center.business.wechat.feignclient.smallapp.BaseSmallAppFeignClient;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

/**
 * @author: wuchenxi
 * @date: 2020/6/30 11:08:08
 */
@Component
public class FeignClientUtil {

    private static Map<String, BaseSmallAppFeignClient> smallAppServerFeignClientStatic;

    private static Map<String, BaseQyFeignClient> qyServerFeignClientStatic;

    @Resource
    private Map<String, BaseSmallAppFeignClient> smallAppServerFeignClient;

    @Resource
    private Map<String, BaseQyFeignClient> qyServerFeignClient;

    @PostConstruct
    public void init() {
        smallAppServerFeignClientStatic = this.smallAppServerFeignClient;
        qyServerFeignClientStatic = this.qyServerFeignClient;
    }

    /**
     * 获取指定服务的企业微信feignClient
     *
     * @param serverName
     * @return
     */
    public static BaseQyFeignClient getQyFeignClient(String serverName) {
        return qyServerFeignClientStatic.get(serverName);
    }

    /**
     * 获取指定服务的小程序feignClient
     *
     * @param serverName
     * @return
     */
    public static BaseSmallAppFeignClient getSmallAppFeignClient(String serverName) {
        return smallAppServerFeignClientStatic.get(serverName);
    }

}
