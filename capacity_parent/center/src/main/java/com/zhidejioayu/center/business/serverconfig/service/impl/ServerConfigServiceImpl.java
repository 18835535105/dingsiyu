package com.zhidejioayu.center.business.serverconfig.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.mapper.center.ServerConfigMapper;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejioayu.center.business.serverconfig.service.ServerConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/6/23 18:00:00
 */
@Service
public class ServerConfigServiceImpl extends ServiceImpl<ServerConfigMapper, ServerConfig> implements ServerConfigService {

    @Resource
    private ServerConfigMapper serverConfigMapper;

    @Override
    public ServerConfig getByServerNo(String serverNo) {
        return serverConfigMapper.selectByServerNo(serverNo);
    }
}
