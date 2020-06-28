package com.zhidejioayu.center.business.userinfo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.mapper.center.BusinessUserInfoMapper;
import com.zhidejiaoyu.common.pojo.center.BusinessUserInfo;
import com.zhidejioayu.center.business.userinfo.service.UserInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/6/28 16:35:35
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<BusinessUserInfoMapper, BusinessUserInfo> implements UserInfoService {

    @Resource
    private BusinessUserInfoMapper businessUserInfoMapper;

    @Override
    public BusinessUserInfo getUserInfoByUserUuid(String uuid) {
        return businessUserInfoMapper.selectByUserUuid(uuid);
    }
}
