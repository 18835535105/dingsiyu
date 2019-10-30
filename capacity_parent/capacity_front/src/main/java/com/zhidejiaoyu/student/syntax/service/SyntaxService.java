package com.zhidejiaoyu.student.syntax.service;

import com.zhidejiaoyu.common.pojo.SyntaxTopic;
import com.zhidejiaoyu.student.service.BaseService;

import javax.servlet.http.HttpSession;

/**
 * 语法实现层
 *
 * @author: liumaoyu
 * @Date: 2019/10/30 16:19
 */
public interface SyntaxService extends BaseService<SyntaxTopic> {
    /**
     * 获取学生学习课程
     * @param session
     * @return
     */
    Object getStudyCourse(HttpSession session);
}
