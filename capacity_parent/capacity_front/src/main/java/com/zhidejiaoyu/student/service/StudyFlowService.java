package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;

public interface StudyFlowService {

    ServerResponse<Object> initializeNode(long studentId, long courseId, long unitId, long node, Long grade, HttpSession session);
}
