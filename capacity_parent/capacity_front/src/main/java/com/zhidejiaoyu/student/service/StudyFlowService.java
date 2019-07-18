package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.pojo.StudyFlow;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.dto.NodeDto;

import javax.servlet.http.HttpSession;

public interface StudyFlowService extends BaseService<StudyFlow> {


    /**
     * 节点学完, 把下一节点初始化到student_flow表, 并把下一节点返回
     *
     * @return  id 节点id
     *          modelName 节点模块名
     */
    ServerResponse<Object> getNode(NodeDto dto, String isTrueFlow, HttpSession session);

    //ServerResponse<Object> getNode(Long courseId, Long unitId, Long nodeId, Long grade, HttpSession session);
}
