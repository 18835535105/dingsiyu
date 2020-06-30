package com.zhidejioayu.center.business.wechat.smallapp.serivce.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.mapper.center.ServerConfigMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.feignclient.smallapp.BaseSmallAppFeignClient;
import com.zhidejioayu.center.business.wechat.feignclient.util.FeignClientUtil;
import com.zhidejioayu.center.business.wechat.smallapp.dto.PrizeDTO;
import com.zhidejioayu.center.business.wechat.smallapp.serivce.IndexService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

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
    private RestTemplate restTemplate;

    @Resource
    private ServerConfigMapper serverConfigMapper;

    @Override
    public ServerResponse<Object> index(String openId) {

        ServerConfig serverConfig = getServerConfig(openId);

        String forObject = restTemplate.getForObject(serverConfig.getStudentServerUrl() + "/ec/smallApp/index/index?openId=" + openId, String.class);
        return JSONObject.parseObject(forObject, ServerResponse.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> replenish(String date, String openId) {

        ServerConfig serverConfig = getServerConfig(openId);

        LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>(16);
        params.add("date", date);
        params.add("openId", openId);
        String s = restTemplate.postForObject(serverConfig.getStudentServerUrl() + "/ec/smallApp/index/replenish", params, String.class);
        return JSONObject.parseObject(s, ServerResponse.class);
    }

    @Override
    public ServerResponse<Object> record(String openId) {
        ServerConfig serverConfig = getServerConfig(openId);

        String s = restTemplate.getForObject(serverConfig.getStudentServerUrl() + "/ec/smallApp/index/record?openId=" + openId, String.class);
        return JSONObject.parseObject(s, ServerResponse.class);
    }

    public ServerConfig getServerConfig(String openId) {
        ServerConfig serverConfig = serverConfigMapper.selectStudentServerByOpenid(openId);
        if (serverConfig == null) {
            throw new ServiceException(400, "中台服务器为查询到openid=" + openId + "的学生或者校管信息！");
        }
        return serverConfig;
    }

    @Override
    public ServerResponse<Object> prize(PrizeDTO dto) {

        ServerConfig serverConfig = getServerConfig(dto.getOpenId());

        String s = restTemplate.getForObject(serverConfig.getStudentServerUrl() + "/ec/smallApp/index/prize", String.class, dto);
        return JSONObject.parseObject(s, ServerResponse.class);
    }

    @Override
    public ServerResponse<Object> cardInfo(String openId) {
        ServerConfig serverConfig = getServerConfig(openId);
        BaseSmallAppFeignClient smallAppFeignClient = FeignClientUtil.getSmallAppFeignClient(serverConfig.getServerName());
        return smallAppFeignClient.cardInfo(openId);
    }

}
