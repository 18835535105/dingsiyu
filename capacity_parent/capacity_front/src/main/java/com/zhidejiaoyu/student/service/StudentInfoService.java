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
     *  @param session
     * @param classify 学习模块，区分各个学习模块的时长，1：慧记忆；2：慧听写；3：慧默写；4：例句听力；5：例句翻译；6：例句默写
     * @param courseId
     * @param unitId
     */
    ServerResponse<String> calculateValidTime(HttpSession session, Integer classify, Long courseId, Long unitId);

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
