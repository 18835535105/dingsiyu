package com.zhidejiaoyu.student.business.feedback.service.impl;

import com.zhidejiaoyu.common.mapper.BugFeedbackMapper;
import com.zhidejiaoyu.common.pojo.BugFeedback;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.business.feedback.service.BugFeedBackService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author: wuchenxi
 * @date: 2020/4/13 11:19:19
 */
@Service
public class BugFeedBackServiceImpl extends BaseServiceImpl<BugFeedbackMapper, BugFeedback> implements BugFeedBackService {

    @Resource
    private BugFeedbackMapper bugFeedbackMapper;

    @Override
    public void saveBugBack(HttpSession session, BugFeedback feedback) {
        Student student = getStudent(session);
        int count = bugFeedbackMapper.countByStudentIdAndDate(student.getId(), new Date());
        if (count < 5) {
            feedback.setFixed(1);
            feedback.setCreateTime(LocalDateTime.now());
            bugFeedbackMapper.insert(feedback);
        }
    }
}
