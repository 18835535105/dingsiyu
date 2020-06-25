package com.zhidejioayu.center.business.wechat.util;

import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.center.ServerConfigMapper;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 获取人员信息工具类
 *
 * @author: wuchenxi
 * @date: 2020/6/24 15:54:54
 */
@Component
public class CenterUserInfoUtil {

    @Resource
    private ServerConfigMapper serverConfigMapper;

    private static ServerConfigMapper staticServerConfigMapper;

    @PostConstruct
    public void init() {
        staticServerConfigMapper = this.serverConfigMapper;
    }

    /**
     * 通过openid获取校长或者教师服务器信息
     *
     * @param openid
     * @return
     */
    public static ServerConfig getTeacherServerConfigByOpenid(String openid) {
        ServerConfig serverConfig = staticServerConfigMapper.selectTeacherServerByOpenId(openid);
        if (serverConfig == null) {
            throw new ServiceException(400, "中台服务器为查询到openid=" + openid + "的教师或者校管信息！");
        }
        return serverConfig;
    }

    /**
     * 根据用户uuid查询用户所在服务器信息
     *
     * @param uuid
     * @return
     */
    public static ServerConfig getByUuid(String uuid) {
        ServerConfig serverConfig = staticServerConfigMapper.selectByUUID(uuid);
        if (serverConfig == null) {
            throw new ServiceException(400, "中台服务器为查询到uuid=" + uuid + "的用户信息！");
        }
        return serverConfig;
    }
}
