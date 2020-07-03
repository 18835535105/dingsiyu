package com.zhidejioayu.center.business.wechat.smallapp.serivce.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.mapper.PrizeConfigMapper;
import com.zhidejiaoyu.common.mapper.center.WeChatMapper;
import com.zhidejiaoyu.common.pojo.PrizeConfig;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejiaoyu.common.pojo.center.WeChat;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.util.ServerConfigUtil;
import com.zhidejioayu.center.business.feignclient.smallapp.BaseSmallAppFeignClient;
import com.zhidejioayu.center.business.feignclient.util.FeignClientUtil;
import com.zhidejioayu.center.business.wechat.smallapp.dto.PrizeConfigDTO;
import com.zhidejioayu.center.business.wechat.smallapp.serivce.PrizeConfigService;
import com.zhidejioayu.center.business.wechat.smallapp.vo.ReturnAdminVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class PrizeConfigServiceImpl extends ServiceImpl<PrizeConfigMapper, PrizeConfig> implements PrizeConfigService {
    @Resource
    private WeChatMapper weChatMapper;

    public ServerConfig getServerConfig(String openId) {
        return ServerConfigUtil.getServerInfoByStudentOpenid(openId);
    }


    @Override
    public Object getPrizeConfig(PrizeConfigDTO dto) {
        ServerConfig serverConfig = getServerConfig(dto.getOpenId());
        BaseSmallAppFeignClient smallAppFeignClient = FeignClientUtil.getSmallAppFeignClient(serverConfig.getServerName());
        return smallAppFeignClient.getPrizeConfig(dto);
    }

    @Override
    public ServerResponse<ReturnAdminVo> getAdmin(String openId) {

        ServerConfig serverConfig = getServerConfig(openId);
        BaseSmallAppFeignClient smallAppFeignClient = FeignClientUtil.getSmallAppFeignClient(serverConfig.getServerName());
        ServerResponse<ReturnAdminVo> response = smallAppFeignClient.getAdmin(openId);

        if (response != null && response.getStatus() == ResponseCode.SUCCESS.getCode()) {
            WeChat weChat = weChatMapper.selectByOpenId(openId);
            ReturnAdminVo returnAdminVo = response.getData();
            if (weChat != null) {
                returnAdminVo.setWeChatName(weChat.getWeChatName());
                returnAdminVo.setWeChatImgUrl(weChat.getWeChatImgUrl());
            } else {
                log.error("we_chat表中未查询到 openid={} 的数据！", openId);
            }

            return ServerResponse.createBySuccess(returnAdminVo);
        }
        return response;
    }
}
