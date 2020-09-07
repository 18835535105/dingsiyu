package com.zhidejiaoyu.student.business.flow.service;

import com.zhidejiaoyu.common.dto.NodeDto;
import com.zhidejiaoyu.common.pojo.StudyFlowNew;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.BaseService;

import javax.servlet.http.HttpSession;

/**
 * 流程节点获取
 */
public interface StudyFlowService extends BaseService<StudyFlowNew> {


    /**
     * 节点学完, 把下一节点初始化到student_flow表, 并把下一节点返回
     *
     * @param dto
     * @param isTrueFlow
     * @param session
     * @return id 节点id
     * modelName 节点模块名
     */
    ServerResponse<Object> getNode(NodeDto dto, String isTrueFlow, HttpSession session);

    /**
     * 去指定id的节点
     *
     * @param dto
     * @param nextFlowId
     * @return
     */
    ServerResponse<Object> toAnotherFlow(NodeDto dto, int nextFlowId);

    /**
     * studyFlowNew.getType()=null，说明直接进入下一单元或者group
     * studyFlowNew.getType()!=null并且测试分数大于或者等于需要达到的分数，说明直接进入下一单元或者group
     *
     * @param dto
     * @param studyFlowNew
     * @return
     */
    default boolean checkNextUnitOrGroup(NodeDto dto, StudyFlowNew studyFlowNew) {
        return (studyFlowNew.getType() == null)
                || (dto.getGrade() != null && dto.getGrade() >= studyFlowNew.getType());
    }

    ServerResponse<Object> getModel(HttpSession session, Integer type);
}
