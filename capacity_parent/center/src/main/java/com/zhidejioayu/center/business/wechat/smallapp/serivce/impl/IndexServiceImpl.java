package com.zhidejioayu.center.business.wechat.smallapp.serivce.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.center.ServerConfigMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.util.UserInfoUtil;
import com.zhidejioayu.center.business.wechat.feignclient.smallapp.BaseSmallAppFeignClient;
import com.zhidejioayu.center.business.wechat.feignclient.util.FeignClientUtil;
import com.zhidejioayu.center.business.wechat.smallapp.dto.PrizeDTO;
import com.zhidejioayu.center.business.wechat.smallapp.serivce.IndexService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 首页数据
 *
 * @author: wuchenxi
 * @date: 2020/2/14 15:00:00
 */
@Service("smallAppIndexService")
public class IndexServiceImpl extends ServiceImpl<StudentMapper, Student> implements IndexService {

    @Resource
    private ServerConfigMapper serverConfigMapper;

    @Override
    public ServerResponse<Object> index(String openId) {

        ServerConfig serverConfig = getServerConfig(openId);
        BaseSmallAppFeignClient smallAppFeignClient = FeignClientUtil.getSmallAppFeignClient(serverConfig.getServerName());
        return smallAppFeignClient.index(openId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> replenish(String date, String openId) {

        ServerConfig serverConfig = getServerConfig(openId);
        BaseSmallAppFeignClient smallAppFeignClient = FeignClientUtil.getSmallAppFeignClient(serverConfig.getServerName());
        return smallAppFeignClient.replenish(date, openId);
    }

    @Override
    public ServerResponse<Object> record(String openId) {
        ServerConfig serverConfig = getServerConfig(openId);
        BaseSmallAppFeignClient smallAppFeignClient = FeignClientUtil.getSmallAppFeignClient(serverConfig.getServerName());
        return smallAppFeignClient.record(openId);
    }

    public ServerConfig getServerConfig(String openId) {
        return UserInfoUtil.getServerInfoByStudentOpenid(openId);
    }

    @Override
    public ServerResponse<Object> prize(PrizeDTO dto) {
        ServerConfig serverConfig = getServerConfig(dto.getOpenId());
        BaseSmallAppFeignClient smallAppFeignClient = FeignClientUtil.getSmallAppFeignClient(serverConfig.getServerName());
        return smallAppFeignClient.prize(dto);
    }

    @Override
    public ServerResponse<Object> cardInfo(String openId) {
        ServerConfig serverConfig = getServerConfig(openId);
        BaseSmallAppFeignClient smallAppFeignClient = FeignClientUtil.getSmallAppFeignClient(serverConfig.getServerName());
        return smallAppFeignClient.cardInfo(openId);
    }

    @Override
    public ServerResponse<Object> myState(String openId) {
        ServerConfig serverConfig = getServerConfig(openId);
        BaseSmallAppFeignClient smallAppFeignClient = FeignClientUtil.getSmallAppFeignClient(serverConfig.getServerName());
        return smallAppFeignClient.myState(openId);
    }

}
