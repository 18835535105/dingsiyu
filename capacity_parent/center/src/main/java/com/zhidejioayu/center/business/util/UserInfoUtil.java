package com.zhidejioayu.center.business.util;

import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.center.ServerConfigMapper;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 中台服务器人员工具类
 *
 * @author: wuchenxi
 * @date: 2020/6/30 18:29:29
 */
@Component
public class UserInfoUtil {

    private static ServerConfigMapper serverConfigMapperStatic;

    @Resource
    private ServerConfigMapper serverConfigMapper;

    @PostConstruct
    public void init() {
        serverConfigMapperStatic = this.serverConfigMapper;
    }

    /**
     * 通过学生openid查询所属服务器信息
     *
     * @param openid
     * @return
     */
    public static ServerConfig getServerInfoByStudentOpenid(String openid) {
        ServerConfig serverConfig = serverConfigMapperStatic.selectStudentServerByOpenid(openid);
        if (serverConfig == null) {
            throw new ServiceException(400, "中台服务器为查询到openid=" + openid + "的学生或者校管信息！");
        }
        return serverConfig;
    }
}
