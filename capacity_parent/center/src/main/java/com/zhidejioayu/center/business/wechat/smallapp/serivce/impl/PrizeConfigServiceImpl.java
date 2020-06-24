package com.zhidejioayu.center.business.wechat.smallapp.serivce.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.mapper.center.ServerConfigMapper;
import com.zhidejiaoyu.common.mapper.center.WeChatMapper;
import com.zhidejiaoyu.common.pojo.PrizeConfig;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejiaoyu.common.pojo.center.WeChat;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.smallapp.dto.PrizeConfigDTO;
import com.zhidejioayu.center.business.wechat.smallapp.serivce.PrizeConfigService;
import com.zhidejioayu.center.business.wechat.smallapp.vo.ReturnAdminVo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Service
public class PrizeConfigServiceImpl extends ServiceImpl<PrizeConfigMapper, PrizeConfig> implements PrizeConfigService {
    @Resource
    private WeChatMapper weChatMapper;

    @Resource
    private ServerConfigMapper serverConfigMapper;

    @Resource
    private RestTemplate restTemplate;

    public ServerConfig getServerConfig(String openId) {
        ServerConfig serverConfig = serverConfigMapper.selectStudentServerByOpenid(openId);
        if (serverConfig == null) {
            throw new ServiceException(400, "中台服务器为查询到openid=" + openId + "的学生或者校管信息！");
        }
        return serverConfig;
    }


    @Override
    public Object getPrizeConfig(PrizeConfigDTO dto) {

        ServerConfig serverConfig = getServerConfig(dto.getOpenId());

        String s = restTemplate.getForObject(serverConfig.getStudentServerUrl() + "/ec/smallApp/prizeConfig/getPrizeConfig", String.class, dto);
        return JSONObject.parseObject(s, ServerResponse.class);
    }

    @Override
    public Object getAdmin(String openId) {

        ServerConfig serverConfig = getServerConfig(openId);

        String s = restTemplate.getForObject(serverConfig.getStudentServerUrl() + "/ec/smallApp/prizeConfig/getAdmin?openId=" + openId, String.class);
        ServerResponse serverResponse = JSONObject.parseObject(s, ServerResponse.class);

        if (serverResponse != null && serverResponse.getStatus() == ResponseCode.SUCCESS.getCode()) {
            WeChat weChat = weChatMapper.selectByOpenId(openId);
            ReturnAdminVo returnAdminVo = (ReturnAdminVo) serverResponse.getData();
            returnAdminVo.setWeChatName(weChat.getWeChatName());
            returnAdminVo.setWeChatImgUrl(weChat.getWeChatImgUrl());
            return ServerResponse.createBySuccess(returnAdminVo);
        }
        return ServerResponse.createBySuccess(serverResponse);
    }
}
