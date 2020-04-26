package com.zhidejiaoyu.student.business.feedback.service.impl;

import com.zhidejiaoyu.common.mapper.BugFeedbackMapper;
import com.zhidejiaoyu.common.mapper.StudyCapacityMapper;
import com.zhidejiaoyu.common.pojo.BugFeedback;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.business.feedback.service.BugFeedBackService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: wuchenxi
 * @date: 2020/4/13 11:19:19
 */
@Service
public class BugFeedBackServiceImpl extends BaseServiceImpl<BugFeedbackMapper, BugFeedback> implements BugFeedBackService {

    @Resource
    private BugFeedbackMapper bugFeedbackMapper;
    @Resource
    private StudyCapacityMapper studyCapacityMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBugBack(HttpSession session, BugFeedback feedback, Long vocaId) {
        Integer type = getType(feedback.getStudyModel());

        Student student = getStudent(session);
        int count = bugFeedbackMapper.countByStudentIdAndDate(student.getId(), new Date());
        if (count < 5) {
            feedback.setFixed(1);
            feedback.setStudentId(student.getId());
            feedback.setCreateTime(LocalDateTime.now());
            bugFeedbackMapper.insert(feedback);
        }
        if (type != null && vocaId != null) {
            List<Integer> listType = getListType(type);
            studyCapacityMapper.deleteByTypeAndvocaId(listType, vocaId);
        }
    }

    private Integer getType(String studyModel) {
        if (studyModel == null) {
            return null;
        }
        int integer = Integer.parseInt(studyModel);
        boolean flag = (integer >= 0 && integer <= 3) || (integer >= 14 && integer <= 22)
                || integer == 27 || integer == 26;
        if (flag) {
            return 1;
        }
        if (integer >= 4 && integer <= 6) {
            return 2;
        }
        if (integer >= 28 && integer <= 30) {
            return 3;
        }
        if (integer >= 37 && integer <= 40) {
            return 4;
        }
        return null;
    }


    private List<Integer> getListType(Integer type) {
        //1.单词图鉴 3，慧记忆 4，会听写 5，慧默写 6，单词填写 7，句型翻译
        // 8，句型听力 9，音译练习 10，句型默写 11，课文试听 12，课文训练
        // 13，闯关测试 14，课文跟读,20，读语法 21，选语法 22，写语法',
        List<Integer> typeList = new ArrayList<>();
        if (type.equals(1)) {
            typeList.add(1);
            typeList.add(3);
            typeList.add(4);
            typeList.add(5);
            typeList.add(6);
        }
        if (type.equals(2)) {
            typeList.add(7);
            typeList.add(8);
            typeList.add(9);
            typeList.add(10);
        }
        if (type.equals(3)) {
            typeList.add(11);
            typeList.add(12);
            typeList.add(13);
            typeList.add(14);
        }
        if (type.equals(4)) {
            typeList.add(20);
            typeList.add(21);
            typeList.add(22);
        }
        return typeList;
    }
}
