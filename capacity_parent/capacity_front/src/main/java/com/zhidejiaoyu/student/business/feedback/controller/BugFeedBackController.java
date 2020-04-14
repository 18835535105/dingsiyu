package com.zhidejiaoyu.student.business.feedback.controller;

import com.zhidejiaoyu.common.pojo.BugFeedback;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.feedback.service.BugFeedBackService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * bug反馈
 *
 * @author: wuchenxi
 * @date: 2020/4/13 11:18:18
 */
@RestController
@RequestMapping("/bug/feedback")
public class BugFeedBackController {

    @Resource
    private BugFeedBackService bugFeedBackService;

    @PostMapping("/saveBugBack")
    public Object saveBugBack(HttpSession session, BugFeedback feedback, Long vocaId) {
        if (vocaId == null) {
            ServerResponse.createByErrorCodeMessage(500,"vocaId不为空");
        }
        bugFeedBackService.saveBugBack(session, feedback, vocaId);
        return ServerResponse.createBySuccess();
    }


}
