package com.zhidejiaoyu.student.business.sysuser.controller;

import com.zhidejiaoyu.common.pojo.SysUser;
import com.zhidejiaoyu.student.business.sysuser.service.SysUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/6/30 10:02:02
 */
@RestController
@RequestMapping("/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    /**
     * 根据账号获取用户信息
     *
     * @param account
     * @return
     */
    @GetMapping("/getUserByAccount")
    public SysUser getUserByAccount(String account) {
       return sysUserService.getUserByAccount(account);
    }

    @PostMapping("/update")
    public boolean update(@RequestBody SysUser sysUser) {
        return sysUserService.updateById(sysUser);
    }
}
