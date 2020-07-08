package com.zhidejioayu.center.business.feignclient.qy;

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

import java.util.List;

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
     * @param uuid
     * @return <ul>
     * <li>true:未上传</li>
     * <li>false:已上传</li>
     * </ul>
     */
    @GetMapping("/qy/fly/checkUpload")
    boolean checkUpload(@RequestParam String uuid);

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
     * 飞行记录学习总览
     *
     * @param uuid 学生uuid
     * @param date 指定日期
     * @return
     */
    @GetMapping("/qy/fly/recordOverview")
    ServerResponse<Object> recordOverview(@RequestParam String uuid, @RequestParam String date);

    /**
     * 获取新学生当天的智慧飞行记录
     *
     * @param uuid
     * @param date 指定日期
     * @return
     */
    @GetMapping("/qy/fly/getCurrentDayOfStudy")
    ServerResponse<Object> getCurrentDayOfStudy(@RequestParam String uuid, @RequestParam(required = false) String date);

    /**
     * 获取学生智慧飞行记录日历
     *
     * @param uuid
     * @param month
     * @return
     */
    @GetMapping("/qy/fly/getFlyCalendar")
    List<String> getFlyCalendar(@RequestParam String uuid, @RequestParam String month);
}
