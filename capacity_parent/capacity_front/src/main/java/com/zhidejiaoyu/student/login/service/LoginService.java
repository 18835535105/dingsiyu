package com.zhidejiaoyu.student.login.service;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.BaseService;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author: wuchenxi
 * @Date: 2019/12/2 15:39
 */
public interface LoginService extends BaseService<Student> {
    /**
     * 登录
     *
     * @param account
     * @param password
     * @return
     */
    ServerResponse<Object> loginJudge(String account, String password);

    /**
     * session过期和系统重启时保存学生时长相关信息
     *
     * @param sessionMap
     */
    void saveDurationInfo(Map<String, Object> sessionMap);

    /**
     * 客户端学生退出登录，教师输入账号确认接口
     *
     * @param session
     * @param teacherAccount
     * @return
     */
    Object isLoginOut(HttpSession session, String teacherAccount);
}
