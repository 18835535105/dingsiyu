package com.zhidejiaoyu.student.business.goldCoinFactory.service;

import com.zhidejiaoyu.common.dto.rank.RankDto;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.business.service.BaseService;

import javax.servlet.http.HttpSession;

public interface GoldCoinFactoryService extends BaseService<Student> {
    Object getList(HttpSession session);
}
