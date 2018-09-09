package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.pojo.Duration;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * 登陆业务接口层
 *
 * @author qizhentao
 * @version 1.0
 */
public interface LoginService {

    Student LoginJudge(String account, String password);

    Integer judgeUser(Long id);

    /***
     * 修改密码
     *
     * @param password 新密码
     * @param session
     * @return
     */
    ServerResponse<String> updatePassword(String password, HttpSession session);

    ServerResponse<Object> index(HttpSession session);

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
    ServerResponse LoginJudge(String account, String password, HttpSession session, String code);

    /**
     * 退出登录
     *
     * @param session
     */
    void loginOut(HttpSession session);

    /**
     * 保存学生时长相关信息
     *
     * @param session
     */
    void saveDuration(HttpSession session);

    /**
     * session过期和系统重启时保存学生时长相关信息
     *
     * @param student
     * @param map
     * @param loginTime
     */
    void saveDurationInfo(Student student, Map<Integer, Duration> map, Date loginTime);

    void getValidateCode(HttpSession session, HttpServletResponse response) throws IOException;
}
