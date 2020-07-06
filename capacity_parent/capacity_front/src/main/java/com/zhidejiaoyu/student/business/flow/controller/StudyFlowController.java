package com.zhidejiaoyu.student.business.flow.controller;

import com.zhidejiaoyu.common.constant.session.SessionConstant;
import com.zhidejiaoyu.common.dto.NodeDto;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.flow.service.StudyFlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * 流程
 *
 * @author wuchenxi
 */
@Slf4j
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
        ServerResponse<Object> node;
        if (Objects.equals(dto.getType(), 1)) {
            // 一键排课流程
            session.setAttribute(SessionConstant.STUDY_FLAG, 1);
            node = flowService.getNode(dto, isTrueFlow, session);
        } else {
            // 自由学习流程
            session.setAttribute(SessionConstant.STUDY_FLAG, 2);
            node = freeFlowService.getNode(dto, isTrueFlow, session);
        }
        log.info("getNode response={}", node.toString());
        return node;
    }
}
