package com.zhidejiaoyu.student.business.flow.service;

import com.zhidejiaoyu.common.dto.NodeDto;
import com.zhidejiaoyu.common.pojo.StudyFlowNew;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.flow.FlowVO;
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
     * 自由学习选择单元获取当前单元应学习的节点
     *
     * @param unitId
     * @param type       2：单词；3：句型；4：课文
     * @param easyOrHard 1：简单；2：困难
     * @return
     */
    ServerResponse<FlowVO> getIndexNode(Long unitId, Integer easyOrHard, Integer type);
}
