package com.zhidejiaoyu.student.business.flow.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.dto.NodeDto;
import com.zhidejiaoyu.common.vo.flow.FlowVO;
import com.zhidejiaoyu.student.business.flow.service.StudyFlowService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * 流程
 */
@RestController
@RequestMapping("/flow")
public class StudyFlowController {

    @Resource
    private StudyFlowService flowService;

    @Resource
    private StudyFlowService freeFlowService;


    /**
     * 节点学完, 把下一节点初始化到student_flow表, 并把下一节点返回
     *
     * @param isTrueFlow 是否执行正确情况的节点
     * @return id 节点id
     * modelName 节点模块名
     */
    @RequestMapping("/getNode")
    public ServerResponse<Object> getNode(NodeDto dto, @RequestParam(required = false) String isTrueFlow, HttpSession session) {
        dto.setTrueFlow(isTrueFlow);
        if (Objects.equals(dto.getType(), 1)) {
            // 一键排课流程
            return flowService.getNode(dto, isTrueFlow, session);
        }
        // 自由学习流程
        return freeFlowService.getNode(dto, isTrueFlow, session);
    }

    /**
     * 自由学习选择单元获取当前单元应学习的节点
     *
     * @param unitId
     * @param easyOrHard
     * @param type 1：单词；2：句型；3：课文
     * @return
     */
    @RequestMapping("/getIndexNode")
    public ServerResponse<FlowVO> getIndexNode(Long unitId, Integer easyOrHard, Integer type) {
        return freeFlowService.getIndexNode(unitId, easyOrHard, type);
    }
}
