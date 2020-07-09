package com.zhidejioayu.center.business.feignclient.util;

import com.zhidejioayu.center.business.feignclient.qy.BaseQyFeignClient;
import com.zhidejioayu.center.business.feignclient.smallapp.BaseSmallAppFeignClient;
import com.zhidejioayu.center.business.feignclient.student.BaseStudentFeignClient;
import com.zhidejioayu.center.business.feignclient.wxrobot.BaseWxRobotFeignClient;
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

    private static Map<String, BaseStudentFeignClient> studentServerFeignClientStatic;

    private static Map<String, BaseWxRobotFeignClient> wxRobotServerFeignClientStatic;

    @Resource
    private Map<String, BaseSmallAppFeignClient> smallAppServerFeignClient;

    @Resource
    private Map<String, BaseQyFeignClient> qyServerFeignClient;

    @Resource
    private Map<String, BaseStudentFeignClient> studentServerFeignClient;

    @Resource
    private Map<String, BaseWxRobotFeignClient> wxRobotServerFeignClient;

    @PostConstruct
    public void init() {
        smallAppServerFeignClientStatic = this.smallAppServerFeignClient;
        qyServerFeignClientStatic = this.qyServerFeignClient;
        studentServerFeignClientStatic = this.studentServerFeignClient;
        wxRobotServerFeignClientStatic = this.wxRobotServerFeignClient;
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

    /**
     * 获取指定服务的学生feignClient
     *
     * @param serverName
     * @return
     */
    public static BaseStudentFeignClient getStudentFeignClient(String serverName) {
        return studentServerFeignClientStatic.get(serverName);
    }

    /**
     * 获取指定服务的微信机器人feignClient
     *
     * @param serverName
     * @return
     */
    public static BaseWxRobotFeignClient getWxRobotFeignClient(String serverName) {
        return wxRobotServerFeignClientStatic.get(serverName);
    }



}
