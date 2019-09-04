package com.zhidejiaoyu.student.service;

import com.baomidou.mybatisplus.service.IService;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudyFlow;

import javax.servlet.http.HttpSession;

/**
 * 学生service基类
 *
 * @author wuchenxi
 * @date 2018/8/29
 */
public interface BaseService<T> extends IService<T> {

    /**
     * 从session中获取学生信息
     *
     * @param session
     * @return
     */
    Student getStudent(HttpSession session);

    /**
     * 从session中获取学生id
     *
     * @param session
     * @return
     */
    Long getStudentId(HttpSession session);

    /**
     * 获取学生指定时间段的有效时长
     *
     * @param studentId
     * @param beginTime 起始时间字符串
     * @param endTime   结束时间字符串
     * @return 有效时长 （秒）
     */
    Integer getValidTime(Long studentId, String beginTime, String endTime);

    /**
     * 获取学生指定时间段的有效时长
     *
     * @param session
     * @param beginTime 起始时间字符串
     * @param endTime   结束时间字符串
     * @return 有效时长 （秒）
     */
    Integer getOnLineTime(HttpSession session, String beginTime, String endTime);

    void getLevel(HttpSession session);

    /**
     * 获取学生当前正在学习的流程信息
     *
     * @param studentId
     * @return
     */
    StudyFlow getCurrentStudyFlow(Long studentId);

    /**
     * 保存运行日志
     *
     * @param student
     * @param type     保存的日志类型
     * @param courseId
     * @param unitId
     * @param msg      保存的日志内容
     */
    void saveRunLog(Student student, Integer type, Long courseId, Long unitId, String msg) throws RuntimeException;

    /**
     * 保存运行日志
     *
     * @param student
     * @param type    保存的日志类型
     * @param msg     保存的日志内容
     */
    void saveRunLog(Student student, Integer type, String msg) throws RuntimeException;

    /**
     * 计算今天的在线时长
     *
     * @param session
     * @return
     */
    Integer getTodayOnlineTime(HttpSession session);

    /**
     * 计算今天的有效时长
     *
     * @param studentId
     * @return
     */
    Integer getTodayValidTime(Long studentId);

}
