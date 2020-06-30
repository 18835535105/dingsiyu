package com.zhidejioayu.center.business.wechat.smallapp.serivce.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.mapper.CurrentDayOfStudyMapper;
import com.zhidejiaoyu.common.mapper.center.ServerConfigMapper;
import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.feignclient.smallapp.BaseSmallAppFeignClient;
import com.zhidejioayu.center.business.wechat.feignclient.util.FeignClientUtil;
import com.zhidejioayu.center.business.wechat.smallapp.serivce.FlyOfStudyService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/6/2 15:23:23
 */
@Service
public class FlyOfStudyServiceImpl extends ServiceImpl<CurrentDayOfStudyMapper, CurrentDayOfStudy> implements FlyOfStudyService {

   @Resource
   private ServerConfigMapper serverConfigMapper;

    @Override
    public ServerResponse<Object> getTotalStudyInfo(String studentUuid) {
        ServerConfig serverConfig = serverConfigMapper.selectByUUID(studentUuid);
        BaseSmallAppFeignClient smallAppFeignClient = FeignClientUtil.getSmallAppFeignClient(serverConfig.getServerName());
        return smallAppFeignClient.getTotalStudyInfo(studentUuid, -1);
    }

    @Override
    public ServerResponse<Object> getStudyInfo(String studentUuid, Integer num) {
        ServerConfig serverConfig = serverConfigMapper.selectByUUID(studentUuid);
        BaseSmallAppFeignClient smallAppFeignClient = FeignClientUtil.getSmallAppFeignClient(serverConfig.getServerName());
        return smallAppFeignClient.getTotalStudyInfo(studentUuid, num);
    }

    @Override
    public ServerResponse<Object> getStudentInfo(String studentUuid, Integer num) {
        ServerConfig serverConfig = serverConfigMapper.selectByUUID(studentUuid);
        BaseSmallAppFeignClient smallAppFeignClient = FeignClientUtil.getSmallAppFeignClient(serverConfig.getServerName());
        return smallAppFeignClient.getStudentInfo(studentUuid, num);
    }
}
