package com.zhidejiaoyu.student.business.clientValidity.service.impl;

import com.zhidejiaoyu.common.mapper.SysConfigMapper;
import com.zhidejiaoyu.common.pojo.SysConfig;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.clientValidity.service.ClientValidityService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ClientValidityServiceImpl implements ClientValidityService {

    @Resource
    private SysConfigMapper sysConfigMapper;

    private String clientValidityTimeStr = "客户端有效期";

    @Override
    public Object getClientValidity() {

        SysConfig sysConfig = sysConfigMapper.selectByExplain(clientValidityTimeStr);
        if (sysConfig != null) {
            return ServerResponse.createBySuccess(sysConfig.getContent());
        }
        return ServerResponse.createBySuccess();
    }
}
