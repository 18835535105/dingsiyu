package com.zhidejiaoyu.student.business.service;

import com.baomidou.mybatisplus.service.IService;
import com.zhidejiaoyu.common.pojo.LearnNew;

import javax.servlet.http.HttpSession;

public interface IStudyService extends IService<LearnNew> {


    Object getStudy(HttpSession session, Long unitId);

    Object saveStudy(Long studentId, Long unitId, Long wordId, boolean isTrue, Integer plan, Integer total);


}
