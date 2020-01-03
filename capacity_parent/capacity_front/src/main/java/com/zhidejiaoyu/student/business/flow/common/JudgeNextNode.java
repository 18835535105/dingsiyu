package com.zhidejiaoyu.student.business.flow.common;

import com.zhidejiaoyu.common.constant.study.PointConstant;
import com.zhidejiaoyu.common.dto.NodeDto;
import com.zhidejiaoyu.common.mapper.StudyFlowNewMapper;
import com.zhidejiaoyu.common.pojo.StudyFlowNew;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.flow.service.StudyFlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 判断学生应学习的下个流程节点
 *
 * @author: wuchenxi
 * @date: 2019/12/28 10:16:16
 */
@Slf4j
@Component
public class JudgeNextNode {

    @Resource
    private StudyFlowNewMapper studyFlowNewMapper;

    public ServerResponse<Object> judgeNextNode(NodeDto dto, StudyFlowService studyFlowService) {
        String isTrueFlow = dto.getTrueFlow();
        StudyFlowNew studyFlowNew = dto.getStudyFlowNew();
        // 学生选择项，是否进行下一个节点， true：进行下一个节点；否则跳过下个节点
        if (isTrueFlow != null) {
            if (Objects.equals("true", isTrueFlow)) {
                return studyFlowService.toAnotherFlow(dto, studyFlowNew.getNextTrueFlow());
            } else {
                return studyFlowService.toAnotherFlow(dto, studyFlowNew.getNextFalseFlow());
            }
        }

        // 判断学生是否在当前分数段
        if (studyFlowNew.getType() != null) {

            StudyFlowNew falseFlow = studyFlowNewMapper.selectById(studyFlowNew.getNextFalseFlow());
            StudyFlowNew trueFlow = studyFlowNewMapper.selectById(studyFlowNew.getNextTrueFlow());

            // 游戏前测节点 id
            int gameTestFlowId = 3;
            if (falseFlow != null && Objects.equals(gameTestFlowId, falseFlow.getType())) {
                ServerResponse<Object> x = this.toNextNode(dto, falseFlow, studyFlowService);
                if (x != null) {
                    return x;
                }
            } else if (trueFlow != null && Objects.equals(gameTestFlowId, trueFlow.getType())) {
                ServerResponse<Object> x = this.toNextNode(dto, trueFlow, studyFlowService);
                if (x != null) {
                    return x;
                }
            }

            Long grade = dto.getGrade();
            if (grade >= studyFlowNew.getType()) {
                return studyFlowService.toAnotherFlow(dto, studyFlowNew.getNextTrueFlow());
            } else if (grade < studyFlowNew.getType()) {
                return studyFlowService.toAnotherFlow(dto, studyFlowNew.getNextFalseFlow());
            } else {
                // 判断是否进行单词好声音
                return ServerResponse.createBySuccess("true", studyFlowNew);
            }
        }
        return null;
    }

    /**
     * 注意：该方法将删除学生学习相关信息
     *
     * @param flow
     * @return
     */
    private ServerResponse<Object> toNextNode(NodeDto dto, StudyFlowNew flow, StudyFlowService studyFlowService) {
        Long grade = dto.getGrade();
        if (grade != null) {
            // 50 <= 分数 < 90分走 nextTrue 流程，分数 < 50 走 nextFalse 流程
            if (grade >= PointConstant.FIFTY && grade < PointConstant.NINETY) {
                return studyFlowService.toAnotherFlow(dto, flow.getNextTrueFlow());
            } else if (grade < PointConstant.NINETY) {
                return studyFlowService.toAnotherFlow(dto, flow.getNextFalseFlow());
            }
        }
        return null;
    }


}
