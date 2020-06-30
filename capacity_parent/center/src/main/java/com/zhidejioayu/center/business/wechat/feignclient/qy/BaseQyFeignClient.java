package com.zhidejioayu.center.business.wechat.feignclient.qy;

import com.zhidejiaoyu.common.dto.wechat.qy.fly.SearchStudentDTO;
import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.zhidejiaoyu.common.pojo.SysUser;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.wechat.qy.LoginVO;
import com.zhidejioayu.center.business.wechat.qy.auth.dto.LoginDTO;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: wuchenxi
 * @date: 2020/6/29 12:30:30
 */
public interface BaseQyFeignClient {
    /**
     * 绑定企业微信
     *
     * @param loginDTO
     * @return
     */
    @PostMapping("/qy/auth/login")
    ServerResponse<LoginVO> login(@RequestBody LoginDTO loginDTO);

    /**
     * 校验学生今天智慧飞行记录是否已经上传
     *
     * @param studentUuid
     * @return <ul>
     * <li>true:未上传</li>
     * <li>false:已上传</li>
     * </ul>
     */
    @GetMapping("/qy/fly/checkUpload")
    boolean checkUpload(@RequestParam String studentUuid);

    /**
     * 保存智慧飞行记录
     *
     * @param currentDayOfStudy
     * @return
     */
    @PostMapping("/currentDayOfStudy/save")
    boolean saveCurrentDayOfStudy(@RequestBody CurrentDayOfStudy currentDayOfStudy);

    /**
     * 查询教师下的学生信息
     *
     * @param searchStudentDTO
     * @return
     */
    @GetMapping("/qy/fly/getStudents")
    ServerResponse<Object> getStudents(@SpringQueryMap SearchStudentDTO searchStudentDTO);

    /**
     * 通过账号获取学管信息
     *
     * @param account
     * @return
     */
    @GetMapping("/user/getUserByAccount")
    SysUser getUserByAccount(@RequestParam String account);

    /**
     * 更新学管信息
     *
     * @param sysUser
     * @return
     */
    @PostMapping("/user/update")
    boolean updateSysUser(@RequestBody SysUser sysUser);

    /**
     * 获取新学生当天的智慧飞行记录
     *
     * @param studentUuid
     * @return
     */
    @GetMapping("/qy/fly/getCurrentDayOfStudy")
    ServerResponse<Object> getCurrentDayOfStudy(@RequestParam String studentUuid);
}
