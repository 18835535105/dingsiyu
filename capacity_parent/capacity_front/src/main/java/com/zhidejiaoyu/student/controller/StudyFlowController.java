package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.StudyFlowService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
     * 节点学完, 把下一节初始化到student_flow表, 并把下一节点返回
     *
     * @param studentId 学生id
     * @param courseId 课程id
     * @param unitId 单元id
     * @param nodeId 当前节点id
     * @param grade 得分
     *
     * @return  id 节点id
     *          modelName 节点模块名
     */
    @PostMapping("/getNode")
    public ServerResponse<Object> initializeNode(long studentId, long courseId, long unitId, long nodeId, Long grade, HttpSession session){
        return StudyFlowServic.initializeNode(studentId, courseId, unitId, nodeId, grade, session);
    }

}
