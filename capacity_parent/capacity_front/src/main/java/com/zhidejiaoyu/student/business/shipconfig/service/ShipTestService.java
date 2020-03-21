package com.zhidejiaoyu.student.business.shipconfig.service;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.BaseService;

import javax.servlet.http.HttpSession;

public interface ShipTestService extends BaseService<Student> {
    Object getTest(HttpSession session, Long studentId);

    Object saveTest(HttpSession session, Long beChallenged, Integer type);

    Object getPkRecord(HttpSession session, int type);

    /**
     * 保存校区副本挑战信息
     *
     * @param copyId           挑战的副本id
     * @param reduceDurability 副本减少的耐久度
     * @return
     */
    ServerResponse<Object> saveSchoolCopyInfo(Long copyId, Integer reduceDurability);

    Object getSingleTesting(HttpSession session, Long bossId);

    Object saveSingleTesting(HttpSession session, Long bossId, Integer bloodVolume);

    /**
     * 获取校区副本挑战状态
     *
     * @param bossId 挑战的副本id
     * @return
     */
    ServerResponse<Object> getSchoolCopyInfo(Long bossId);
}
