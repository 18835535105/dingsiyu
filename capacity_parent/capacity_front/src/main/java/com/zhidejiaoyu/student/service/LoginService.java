package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

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
    /***
     * 修改密码
     *
     * @param password 新密码
     * @param session
     * @return
     */
    ServerResponse<String> updatePassword(String password, HttpSession session, String oldPassword, Long studentId);

    ServerResponse<Object> index(HttpSession session);

    ServerResponse<Object> sentenceIndex(HttpSession session);

    /**
     * 首页点击头像
     *
     * @param type 类型：1.单词；2.句型；3.课文；4.字母、音标
     */
    ServerResponse<Object> clickPortrait(HttpSession session, Integer type);

    /**
     * 登录
     *
     * @param account
     * @param password
     * @param session
     * @param request
     * @return
     */
    ServerResponse loginJudge(String account, String password, HttpSession session, HttpServletRequest request, String code);

    /**
     * 退出登录
     *
     * @param session
     * @param request
     */
    void loginOut(HttpSession session, HttpServletRequest request);

    /**
     * session过期和系统重启时保存学生时长相关信息
     *
     * @param sessionMap
     */
    void saveDurationInfo(Map<String, Object> sessionMap);

    void getValidateCode(HttpSession session, HttpServletResponse response) throws IOException;

    Object getRiepCount(HttpSession session);

    Object getModelStatus(HttpSession session, Integer type);

    Object isLoginOut(HttpSession session, String teacherAccount);
}
