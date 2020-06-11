package com.zhidejiaoyu.student.business.service.simple;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;

/**
 * 登陆业务接口层
 *
 * @author qizhentao
 * @version 1.0
 */
public interface SimpleLoginServiceSimple extends SimpleBaseService<Student> {
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

    ServerResponse<Object> clickPortrait(HttpSession session);

}
