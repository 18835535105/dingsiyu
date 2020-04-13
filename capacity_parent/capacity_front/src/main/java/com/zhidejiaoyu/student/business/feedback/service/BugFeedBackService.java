package com.zhidejiaoyu.student.business.feedback.service;

import com.zhidejiaoyu.common.pojo.BugFeedback;
import com.zhidejiaoyu.student.business.service.BaseService;

import javax.servlet.http.HttpSession;

/**
 * @author: wuchenxi
 * @date: 2020/4/13 11:18:18
 */
public interface BugFeedBackService extends BaseService<BugFeedback> {
    /**
     * 保存学生反馈数据
     * @param session
     * @param feedback
     */
    void saveBugBack(HttpSession session, BugFeedback feedback);
}
