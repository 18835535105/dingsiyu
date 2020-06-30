package com.zhidejiaoyu.student.business.sysuser.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.SysUser;

/**
 * @author: wuchenxi
 * @date: 2020/6/30 10:03:03
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 根据账号获取用户信息
     *
     * @param account
     * @return
     */
    SysUser getUserByAccount(String account);
}
