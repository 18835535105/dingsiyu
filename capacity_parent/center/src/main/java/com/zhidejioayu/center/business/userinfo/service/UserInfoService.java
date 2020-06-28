package com.zhidejioayu.center.business.userinfo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.center.BusinessUserInfo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

/**
 * @author: wuchenxi
 * @date: 2020/6/28 16:34:34
 */
public interface UserInfoService extends IService<BusinessUserInfo> {

    /**
     * 根据用户uuid查询用户信息
     *
     * @param uuid
     * @return
     */
    BusinessUserInfo getUserInfoByUserUuid(String uuid);
}