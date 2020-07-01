package com.zhidejioayu.center.business.util;

import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejioayu.center.business.wechat.feignclient.qy.BaseQyFeignClient;
import com.zhidejioayu.center.business.wechat.feignclient.smallapp.BaseSmallAppFeignClient;
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

    private static Map<String, BaseQyFeignClient> qyFeignClientStatic;

    private static Map<String, BaseSmallAppFeignClient> smallAppFeignClientStatic;

    @Resource
    private Map<String, BaseSmallAppFeignClient> smallAppServerFeignClient;

    @Resource
    private Map<String, BaseQyFeignClient> qyServerFeignClient;

    @PostConstruct
    public void init() {
        qyFeignClientStatic = this.qyServerFeignClient;
        smallAppFeignClientStatic = this.smallAppServerFeignClient;
    }

    /**
     * 通过教师openId获取企业微信指定服务的feignClient
     *
     * @param openId
     * @return
     */
    public static BaseQyFeignClient getBaseQyFeignClient(String openId) {
       ServerConfig serverConfig = UserInfoUtil.getServerInfoByTeacherOpenid(openId);
       return qyFeignClientStatic.get(serverConfig.getServerName());
    }

    /**
     * 通过学生openId微信小程序指定服务的feignClient
     *
     * @param openId
     * @return
     */
    public static BaseSmallAppFeignClient getBaseSmallAppFeignClient(String openId) {
        ServerConfig serverConfig = UserInfoUtil.getServerInfoByStudentOpenid(openId);
        return smallAppFeignClientStatic.get(serverConfig.getServerName());
    }
}
