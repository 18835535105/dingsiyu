package com.zhidejiaoyu.student.business.service;

import com.baomidou.mybatisplus.service.IService;
import com.zhidejiaoyu.common.pojo.LearnNew;

import javax.servlet.http.HttpSession;

public interface LearnNewService extends IService<LearnNew> {
    Object getStudy(HttpSession session, Integer getModel, Long unitId);
}
