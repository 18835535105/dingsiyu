package com.zhidejiaoyu.student.service.simple;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.simple.server.ServerResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * 登陆业务接口层
 *
 * @author qizhentao
 * @version 1.0
 */
public interface LoginService extends BaseService<Student> {

    Student LoginJudge(String account, String password);

    Integer judgeUser(Long id);

    /***
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param password 新密码
     * @param session
     * @return
     */
    ServerResponse<String> updatePassword(String oldPassword, String password, HttpSession session);

    ServerResponse<Object> index(HttpSession session);

    /**
     * 获取有效时长
     *
     * @param session
     * @return
     */
    Integer validTime(HttpSession session);

    /**
     * 获取在线时长
     *
     * @param session
     * @return
     */
    Integer onlineTime(HttpSession session);

    ServerResponse<Object> sentenceIndex(HttpSession session);

    Integer judgePreschoolTest(Long id);

    ServerResponse<Object> clickPortrait(HttpSession session);

    /**
     * 登录
     *
     * @param account
     * @param password
     * @param session
     * @return
     */
    ServerResponse loginJudge(String account, String password, HttpSession session, String code, HttpServletRequest request);

    /**
     * session过期和系统重启时保存学生时长相关信息
     *
     * @param sessionMap
     */
    void saveDurationInfo(Map<String, Object> sessionMap);

    void getValidateCode(HttpSession session, HttpServletResponse response) throws IOException;

    /**
     * 判断学生是否可以学习智能版课程
     *
     * @param student
     * @return
     */
    boolean hasCapacityCourse(Student student);
}
