package com.zhidejiaoyu.student.business.feedback.controller;

import com.zhidejiaoyu.common.pojo.BugFeedback;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.feedback.service.BugFeedBackService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * bug反馈
 *
 * @author: wuchenxi
 * @date: 2020/4/13 11:18:18
 */
@Validated
@RestController
@RequestMapping("/bug/feedback")
public class BugFeedBackController {

    @Resource
    private BugFeedBackService bugFeedBackService;

    @PostMapping("/saveBugBack")
    public Object saveBugBack(HttpSession session, @Valid BugFeedback feedback, String vocaId) {
        /*if (vocaId == null) {
            return ServerResponse.createByErrorCodeMessage(400, "vocaId不能为空");
        }*/
        if (vocaId == null || vocaId.equals("")) {
            bugFeedBackService.saveBugBack(session, feedback, null);
        } else {
            bugFeedBackService.saveBugBack(session, feedback, Long.parseLong(vocaId));
        }

        return ServerResponse.createBySuccess();
    }


}
