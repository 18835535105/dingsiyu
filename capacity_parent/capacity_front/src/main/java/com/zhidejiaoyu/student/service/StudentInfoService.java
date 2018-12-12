package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;

/**
 * 学生信息相关service
 *
 * @author wuchenxi
 * @date 2018年5月8日
 */
public interface StudentInfoService extends BaseService<Student>{

    /**
     * 学生完善个人信息，保存学生信息
     *
     * @param student
     * @param newPassword
     * @param oldPassword
     * @return
     */
    ServerResponse<String> saveStudentInfo(HttpSession session, Student student, String oldPassword, String newPassword);

    /**
     * 膜拜
     *
     * @param session
     * @param userId  被膜拜人的id
     * @return
     */
    ServerResponse<String> worship(HttpSession session, Long userId);

    /**
     *  学生退出学习页面，记录本次在学习页面学习时长
     * @param session
     * @param classify 学习模块(有效时长)，区分各个学习模块的时长，7：单元闯关测试；8：复习测试；9：已学测试；10：熟词测试；11：生词测试；
     *                       12：五维测试；13：任务课程；'14:单词辨音; 15:词组辨音; 16:单词认读; 17:词组认读; 18:词汇考点; 19:句型认读;
     *                       20:语法辨析; 21单词拼写; 22:词组拼写;
     * @param courseId
     * @param unitId
     * @param validTime 学生学习的有效时长，单位：秒
     */
    ServerResponse<String> calculateValidTime(HttpSession session, Integer classify, Long courseId, Long unitId, Long validTime);

    /**
     * 验证原密码是否正确
     *
     * @param nowPassword   当前密码
     * @param oldPassword   原密码
     * @return
     */
    ServerResponse<String> judgeOldPassword(String nowPassword, String oldPassword);

    ServerResponse<String> updateStudentInfo(HttpSession session, Student student);
}
