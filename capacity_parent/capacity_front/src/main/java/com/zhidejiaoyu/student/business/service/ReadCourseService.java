package com.zhidejiaoyu.student.business.service;

import com.baomidou.mybatisplus.service.IService;
import com.zhidejiaoyu.common.pojo.ReadCourse;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;

public interface ReadCourseService extends IService<ReadCourse> {

    ServerResponse<Object> getAllCourse(HttpSession session,String grade);

    ServerResponse<Object> updStudyPlan(HttpSession session, Long courseId, String grade);

    ServerResponse<Object> getStudyCourse(HttpSession session, Long unitId, String grade);

    ServerResponse<Object> getContent(Long typeId, Long courseId);

    ServerResponse<Object> getVersion(HttpSession session);

    /**
     * 智能匹配
     *
     * @param session
     * @param courseId
     * @param readTypeId
     * @return
     */
    ServerResponse capacityMatching(HttpSession session, Long courseId, Long readTypeId);

    ServerResponse<Object> getEnglishParadise(Long courseId, Integer type);
}
