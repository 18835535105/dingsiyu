package com.zhidejioayu.center.business.wechat.qy.fly.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.mapper.CurrentDayOfStudyMapper;
import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.qy.fly.service.QyFlyService;
import com.zhidejioayu.center.business.wechat.util.CenterUserInfoUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/6/16 13:54:54
 */
@Service
public class QyFlyServiceImpl extends ServiceImpl<CurrentDayOfStudyMapper, CurrentDayOfStudy> implements QyFlyService {

    @Resource
    private RestTemplate restTemplate;

    @Override
    public ServerResponse<Object> getCurrentDayOfStudy(String studentUuid) {

        ServerConfig serverConfig = CenterUserInfoUtil.getByUuid(studentUuid);

        String forObject = restTemplate.getForObject(serverConfig.getStudentServerUrl() + "/ec/qy/fly/getCurrentDayOfStudy?studentUuid=" + studentUuid, String.class);

        return JSONObject.parseObject(forObject, ServerResponse.class);
    }
}
