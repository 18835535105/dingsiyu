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


    void getValidateCode(HttpSession session, HttpServletResponse response) throws IOException;

    Object getRiepCount(HttpSession session);

    Object getModelStatus(HttpSession session, Integer type);

}
