package com.zhidejioayu.center.business.userinfo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.mapper.center.BusinessUserInfoMapper;
import com.zhidejiaoyu.common.mapper.center.ServerConfigMapper;
import com.zhidejiaoyu.common.pojo.center.BusinessUserInfo;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejiaoyu.common.utils.IdUtil;
import com.zhidejioayu.center.business.userinfo.service.UserInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author: wuchenxi
 * @date: 2020/6/28 16:35:35
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<BusinessUserInfoMapper, BusinessUserInfo> implements UserInfoService {

    @Resource
    private BusinessUserInfoMapper businessUserInfoMapper;
    @Resource
    private ServerConfigMapper serverConfigMapper;

    @Override
    public BusinessUserInfo getUserInfoByUserUuid(String uuid) {
        return businessUserInfoMapper.selectByUserUuid(uuid);
    }

    @Override
    public void getUser(BusinessUserInfo businessUserInfo, String no) {
        BusinessUserInfo info = businessUserInfoMapper.selectByUserUuid(businessUserInfo.getUserUuid());
        if (info == null) {
            ServerConfig serverConfig = serverConfigMapper.selectByServerNo(no);
            businessUserInfo.setCreateTime(new Date());
            businessUserInfo.setUpdateTime(new Date());
            businessUserInfo.setServerConfigId(serverConfig.getId());
            businessUserInfo.setId(IdUtil.getId());
            businessUserInfoMapper.insert(businessUserInfo);
        }
    }
}
