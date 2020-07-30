package com.zhidejioayu.center.business.feignclient.util;

import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejioayu.center.business.feignclient.qy.BaseQyFeignClient;
import com.zhidejioayu.center.business.feignclient.smallapp.BaseSmallAppFeignClient;
import com.zhidejioayu.center.business.feignclient.student.BaseStudentFeignClient;
import com.zhidejioayu.center.business.feignclient.teacher.BaseTeacherInfoFeignClient;
import com.zhidejioayu.center.business.feignclient.teacher.BaseTeacherPayCardFeignClient;
import com.zhidejioayu.center.business.feignclient.wxrobot.BaseWxRobotFeignClient;
import com.zhidejioayu.center.business.util.ServerConfigUtil;
import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

/**
 * @author: wuchenxi
 * @date: 2020/6/30 11:08:08
 */
@Component
@Import(FeignClientsConfiguration.class)
public class FeignClientUtil {

    private static Feign.Builder builder;

    /**
     * 请求地址前缀
     */
    private static final String PREFIX = "http://";

    /**
     * 学生服务请求前缀
     */
    private static final String STUDENT_CONTEXT_PATH = "/ec";

    /**
     * 教师服务请求前缀
     */
    private static final String TEACHER_CONTEXT_PATH = "/teacher";

    public FeignClientUtil(Decoder feignDecoder,
                           Encoder feignEncoder,
                           Client client,
                           Contract feignContract) {
        builder = Feign.builder()
                .requestInterceptor(requestTemplate -> requestTemplate.header("feign", "true"))
                .client(client)
                .encoder(feignEncoder)
                .decoder(feignDecoder)
                .contract(feignContract);
    }

    /**
     * 获取教师feignClient
     *
     * @param serverName
     * @return
     */
    public static BaseTeacherInfoFeignClient getTeacherInfoFeignClient(String serverName) {
        return builder.target(BaseTeacherInfoFeignClient.class, PREFIX + "teacher" + serverName.replace("server", "") + TEACHER_CONTEXT_PATH);
    }

    /**
     * 获取企业微信feignClient
     *
     * @param serverName
     * @return
     */
    public static BaseQyFeignClient getQyFeignClient(String serverName) {
        return builder.target(BaseQyFeignClient.class, PREFIX + serverName + STUDENT_CONTEXT_PATH);
    }

    /**
     * 获取指定服务的小程序feignClient
     *
     * @param serverName
     * @return
     */
    public static BaseSmallAppFeignClient getSmallAppFeignClient(String serverName) {
        return builder.target(BaseSmallAppFeignClient.class, PREFIX + serverName+ STUDENT_CONTEXT_PATH);
    }

    /**
     * 获取指定服务的学生feignClient
     *
     * @param serverName
     * @return
     */
    public static BaseStudentFeignClient getStudentFeignClient(String serverName) {
        return builder.target(BaseStudentFeignClient.class, PREFIX + serverName+ STUDENT_CONTEXT_PATH);
    }

    /**
     * 获取指定服务的学生feignClient
     *
     * @param serverName
     * @return
     */
    public static BaseTeacherPayCardFeignClient getPayCardFeignClient(String serverName) {
        return builder.target(BaseTeacherPayCardFeignClient.class, PREFIX + serverName+ TEACHER_CONTEXT_PATH);
    }

    /**
     * 获取指定服务的微信机器人feignClient
     *
     * @param serverName
     * @return
     */
    public static BaseWxRobotFeignClient getWxRobotFeignClient(String serverName) {
        return builder.target(BaseWxRobotFeignClient.class, PREFIX + serverName+ STUDENT_CONTEXT_PATH);
    }

    /**
     * 通过教师openId获取企业微信指定服务的feignClient
     *
     * @param openId
     * @return
     */
    public static BaseQyFeignClient getBaseQyFeignClient(String openId) {
        ServerConfig serverConfig = ServerConfigUtil.getServerInfoByTeacherOpenid(openId);
        return getQyFeignClient(serverConfig.getServerName());
    }

    /**
     * 通过学生openId微信小程序指定服务的feignClient
     *
     * @param openId
     * @return
     */
    public static BaseSmallAppFeignClient getBaseSmallAppFeignClient(String openId) {
        ServerConfig serverConfig = ServerConfigUtil.getServerInfoByStudentOpenid(openId);
        return getSmallAppFeignClient(serverConfig.getServerName());
    }

    /**
     * 根据教师uuid获取教师信息
     *
     * @param uuid
     * @return
     */
    public static BaseTeacherInfoFeignClient getBaseTeacherInfoFeignClient(String uuid) {
        ServerConfig serverConfig = ServerConfigUtil.getByUuid(uuid);
        return getTeacherInfoFeignClient(serverConfig.getServerName());
    }

    /**
     * 根据教师openId获取教师信息
     *
     * @param openId
     * @return
     */
    public static BaseTeacherInfoFeignClient getBaseTeacherInfoFeignClientByOpenId(String openId) {
        ServerConfig serverConfig = ServerConfigUtil.getServerInfoByTeacherOpenid(openId);
        return getTeacherInfoFeignClient(serverConfig.getServerName());
    }


    /**
     * 根据学生uuid获取学生信息
     *
     * @param uuid
     * @return
     */
    public static BaseStudentFeignClient getBaseStudentFeignClientByUuid(String uuid) {
        ServerConfig serverConfig = ServerConfigUtil.getByUuid(uuid);
        return getStudentFeignClient(serverConfig.getServerName());
    }

    /**
     * 根据学生uuid获取学生信息
     *
     * @param uuid
     * @return
     */
    public static BaseTeacherPayCardFeignClient getTeacherPayCardFeignClientByUuid(String uuid) {
        ServerConfig serverConfig = ServerConfigUtil.getByUuid(uuid);
        return getPayCardFeignClient(serverConfig.getServerName());
    }



}
