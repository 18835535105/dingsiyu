package com.zhidejiaoyu.student.business.shipconfig.service;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.business.service.BaseService;

import javax.servlet.http.HttpSession;

public interface ShipTestService extends BaseService<Student> {
    Object getTest(HttpSession session, Long studentId);

    Object saveTest(HttpSession session, Long beChallenged, Integer type);

    Object getPKRecord(HttpSession session,int type);

    Object getSingleTesting(HttpSession session, Long bossId);

    Object saveSingleTesting(HttpSession session, Long bossId, Integer bloodVolume);
}
