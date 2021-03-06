package com.zhidejiaoyu.student.business.wechat.qy.fly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.dto.student.StudentStudyPlanListDto;
import com.zhidejiaoyu.common.dto.wechat.qy.fly.SearchStudentDTO;
import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import java.util.List;
import java.util.Map;

/**
 * @author: wuchenxi
 * @date: 2020/6/16 13:54:54
 */
public interface QyFlyService extends IService<CurrentDayOfStudy> {

    /**
     * 获取当前教师下的所有学生
     *
     * @param dto 查询条件
     * @return
     */
    ServerResponse<Object> getStudents(SearchStudentDTO dto);

    /**
     * 获取学生学习计划列表
     *
     * @param dto
     * @return
     */
    ServerResponse<Map<String, Object>> getStudentStudyPlan(StudentStudyPlanListDto dto);

    /**
     * 校验学生今天智慧飞行记录是否已经上传
     *
     * @param uuid
     * @return
     */
    boolean checkUpload(String uuid);

    /**
     * 获取学生智慧飞行记录日历
     *
     * @param uuid
     * @param month 指定月份
     * @return
     */
    List<String> getFlyCalendar(String uuid, String month);

    /**
     * 检查当前学生二维码序号是否已经上传
     *
     * @param studentId
     * @param num       二维码序号
     * @return
     */
    ServerResponse<Object> checkScanQrCode(Long studentId, Integer num);

    /**
     * 获取当天的智慧笔记记录
     *
     * @param uuid
     * @return
     */
    CurrentDayOfStudy getTodayCurrentDayOfStudy(String uuid);
}
