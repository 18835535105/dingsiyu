package com.zhidejiaoyu.student.business.service;

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

    Student getStudent();

    /**
     * 从session中获取学生id
     *
     * @param session
     * @return
     */
    Long getStudentId(HttpSession session);

    Long getStudentId();

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
     * @throws RuntimeException
     */
    void saveRunLog(Student student, Integer type, Long courseId, Long unitId, String msg) throws RuntimeException;

    /**
     * 保存运行日志
     *
     * @param student
     * @param type    保存的日志类型
     * @param msg     保存的日志内容
     * @throws RuntimeException
     */
    void saveRunLog(Student student, Integer type, String msg) throws RuntimeException;

}
