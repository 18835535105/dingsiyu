package com.zhidejioayu.center.business.userinfo.controller;

import com.zhidejiaoyu.common.dto.student.SaveStudentInfoToCenterDTO;
import com.zhidejiaoyu.common.pojo.center.BusinessUserInfo;
import com.zhidejioayu.center.business.redis.UserInfoRedisOpt;
import com.zhidejioayu.center.business.userinfo.service.UserInfoService;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户信息相关接口（教师、学生）
 *
 * @author: wuchenxi
 * @date: 2020/6/28 16:30:30
 */
@RestController("centerUserInfoController")
@RequestMapping("/userInfo")
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private UserInfoRedisOpt userInfoRedisOpt;

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
    public Boolean updateUserInfo(@RequestBody BusinessUserInfo businessUserInfo) {
        return userInfoService.updateById(businessUserInfo);
    }

    /**
     * 保存用户信息
     *
     * @param dto
     * @return
     */
    @PostMapping("/saveUserInfo")
    public Boolean saveUserInfo(@RequestBody SaveStudentInfoToCenterDTO dto) {
        return userInfoService.saveUserInfo(dto);
    }

    /**
     * 批量保存用户信息
     *
     * @param dto
     * @return
     */
    @PostMapping("/saveUserInfos")
    public Boolean saveUserInfo(@RequestBody List<SaveStudentInfoToCenterDTO> dto) {
        return userInfoService.saveUserInfos(dto);
    }

    @GetMapping("/user")
    public void getUser(@SpringQueryMap BusinessUserInfo businessUserInfo) {
        userInfoService.getUser(businessUserInfo, businessUserInfo.getNo());
        userInfoRedisOpt.saveUserInfoToCenterServer(businessUserInfo.getUserUuid());
    }
}
