package com.zhidejiaoyu.student.business.service.impl;

import com.zhidejiaoyu.common.mapper.LearnNewMapper;
import com.zhidejiaoyu.common.mapper.StudentStudyPlanNewMapper;
import com.zhidejiaoyu.common.pojo.LearnNew;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.business.service.LearnNewService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Service
public class LearnNewServiceImpl extends BaseServiceImpl<LearnNewMapper, LearnNew> implements LearnNewService {

    @Resource
    private StudentStudyPlanNewMapper studentStudyPlanNewMapper;


    @Override
    public Object getStudy(HttpSession session, Integer getModel,Long unitId) {
        Student student = getStudent(session);
        //获取学习单元

        //获取学习数据
        switch (getModel) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;
            case 11:
                break;
            case 12:
                break;
            case 13:
                break;
            case 14:
                break;
            case 15:
                break;
            case 16:
                break;
            case 17:
                break;
            case 18:
                break;
            default:
                break;

        }


        return null;
    }


}
