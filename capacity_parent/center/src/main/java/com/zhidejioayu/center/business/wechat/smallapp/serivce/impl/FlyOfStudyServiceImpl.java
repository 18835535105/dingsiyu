package com.zhidejioayu.center.business.wechat.smallapp.serivce.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.mapper.CurrentDayOfStudyMapper;
import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.util.ServerConfigUtil;
import com.zhidejioayu.center.business.feignclient.smallapp.BaseSmallAppFeignClient;
import com.zhidejioayu.center.business.feignclient.util.FeignClientUtil;
import com.zhidejioayu.center.business.wechat.smallapp.serivce.FlyOfStudyService;
import org.springframework.stereotype.Service;

/**
 * @author: wuchenxi
 * @date: 2020/6/2 15:23:23
 */
@Service
public class FlyOfStudyServiceImpl extends ServiceImpl<CurrentDayOfStudyMapper, CurrentDayOfStudy> implements FlyOfStudyService {

    @Override
    public ServerResponse<Object> getTotalStudyInfo(String openId) {
        BaseSmallAppFeignClient smallAppFeignClient = getBaseSmallAppFeignClient(openId);
        return smallAppFeignClient.getTotalStudyInfo(openId, -1);
    }

    public BaseSmallAppFeignClient getBaseSmallAppFeignClient(String openId) {
        ServerConfig serverConfig = ServerConfigUtil.getServerInfoByStudentOpenid(openId);
        return FeignClientUtil.getSmallAppFeignClient(serverConfig.getServerName());
    }

    @Override
    public ServerResponse<Object> getStudyInfo(String openId, Integer num) {
        BaseSmallAppFeignClient smallAppFeignClient = getBaseSmallAppFeignClient(openId);
        return smallAppFeignClient.getTotalStudyInfo(openId, num);
    }

    @Override
    public ServerResponse<Object> getStudentInfo(String openId, Integer num) {
        BaseSmallAppFeignClient smallAppFeignClient = getBaseSmallAppFeignClient(openId);
        return smallAppFeignClient.getStudentInfo(openId, num);
    }
}
