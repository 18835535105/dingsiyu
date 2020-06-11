package com.zhidejiaoyu.student.business.currentDayOfStudy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;

import javax.servlet.http.HttpSession;


public interface CurrentDayOfStudyService extends IService<CurrentDayOfStudy> {

    Object getCurrentDayOfStudy(HttpSession session);

}
