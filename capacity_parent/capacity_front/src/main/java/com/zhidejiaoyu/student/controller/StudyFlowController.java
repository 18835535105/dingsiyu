package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.dto.NodeDto;
import com.zhidejiaoyu.student.service.StudyFlowService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 流程
 */
@RestController
@RequestMapping("/flow")
public class StudyFlowController {

    @Resource
    private StudyFlowService StudyFlowServic;


    /**
     * 节点学完, 把下一节点初始化到student_flow表, 并把下一节点返回
     *
     * @param isTrueFlow 是否执行正确情况的节点
     * @return  id 节点id
     *          modelName 节点模块名
     */
    @RequestMapping("/getNode")
    public ServerResponse<Object> getNode(NodeDto dto, @RequestParam(required = false) String isTrueFlow, HttpSession session) {
        dto.setTrueFlow(isTrueFlow);
        return StudyFlowServic.getNode(dto, isTrueFlow, session);
    }
}
