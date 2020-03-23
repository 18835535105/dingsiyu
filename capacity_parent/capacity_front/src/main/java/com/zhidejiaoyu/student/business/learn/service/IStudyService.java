package com.zhidejiaoyu.student.business.learn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.LearnNew;

import javax.servlet.http.HttpSession;

public interface IStudyService extends IService<LearnNew> {


    Object getStudy(HttpSession session, Long unitId, Integer difficulty);

    Object saveStudy(HttpSession session, Long unitId, Long wordId, boolean isTrue, Integer plan, Integer total, Long courseId, Long flowId);


}
