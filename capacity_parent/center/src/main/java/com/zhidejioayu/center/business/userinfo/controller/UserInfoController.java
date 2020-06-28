package com.zhidejioayu.center.business.userinfo.controller;

import com.zhidejiaoyu.common.pojo.center.BusinessUserInfo;
import com.zhidejioayu.center.business.userinfo.service.UserInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户信息相关接口（教师、学生）
 *
 * @author: wuchenxi
 * @date: 2020/6/28 16:30:30
 */
@RestController
@RequestMapping("/userInfo")
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    /**
     * 根据用户uuid查询用户信息
     *
     * @param uuid
     * @return
     */
    @GetMapping("/getUserInfoByUserUuid")
    public BusinessUserInfo getUserInfoByUserUuid(String uuid) {
        return userInfoService.getUserInfoByUserUuid(uuid);
    }

    /**
     * 修改用户信息
     *
     * @param businessUserInfo
     * @return
     */
    @PostMapping("/updateUserInfo")
    public Boolean updateUserInfo(BusinessUserInfo businessUserInfo) {
        return userInfoService.updateById(businessUserInfo);
    }

    /**
     * 保存用户信息
     *
     * @param businessUserInfo
     * @return
     */
    @PostMapping("/saveUserInfo")
    public Boolean saveUserInfo(BusinessUserInfo businessUserInfo) {
        return userInfoService.save(businessUserInfo);
    }
}
