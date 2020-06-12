package com.zhidejiaoyu.student.business.flow.common;

import com.zhidejiaoyu.common.constant.study.PointConstant;
import com.zhidejiaoyu.common.dto.NodeDto;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.flow.FlowConstant;
import com.zhidejiaoyu.student.business.flow.service.StudyFlowService;
import org.assertj.core.util.Objects;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 判断句型游戏后的下个节点
 *
 * @author: wuchenxi
 * @date: 2020/6/12 10:06:06
 */
@Component
public class JudgeSentenceGame {

    @Resource
    private StudyFlowService flowService;

    @Resource
    private StudyFlowService freeFlowService;

    /**
     * 如果当前节点是句型游戏测试，判断下个节点
     * <ul>
     *     <li>point>=80:去句型翻译</li>
     *     <li>60=<point<80去音译练习2</li>
     *     <li>point<60去音译练习1</li>
     * </ul>
     *
     * @param dto
     * @return
     */
    public ServerResponse<Object> judgeSentenceGame(NodeDto dto) {
        // 是否是一键学习
        boolean isOneKeyLearn = Objects.areEqual(dto.getType(), 1);
        if (isOneKeyLearn) {
            // 一键学习
            if (java.util.Objects.equals(dto.getNodeId(), FlowConstant.SENTENCE_GAME)) {
                if (dto.getGrade() >= PointConstant.EIGHTY) {
                    // 全部答对
                    return flowService.toAnotherFlow(dto, (int) FlowConstant.SENTENCE_TRANSLATE);
                }

                if (dto.getGrade() >= PointConstant.SIXTY && dto.getGrade() < PointConstant.EIGHTY) {
                    return flowService.toAnotherFlow(dto, (int) FlowConstant.YIN_YI_EXERCISE_TWO);
                }

                if (dto.getGrade() < PointConstant.SIXTY) {
                    return flowService.toAnotherFlow(dto, (int) FlowConstant.YIN_YI_EXERCISE_ONE);
                }
            }
        } else {
            // 自由学习
            if (java.util.Objects.equals(dto.getNodeId(), FlowConstant.FREE_SENTENCE_GAME)) {
                if (dto.getGrade() >= PointConstant.EIGHTY) {
                    // 全部答对
                    return freeFlowService.toAnotherFlow(dto, (int) FlowConstant.FREE_SENTENCE_TRANSLATE);
                }

                if (dto.getGrade() >= PointConstant.SIXTY && dto.getGrade() < PointConstant.EIGHTY) {
                    return freeFlowService.toAnotherFlow(dto, (int) FlowConstant.FREE_YIN_YI_EXERCISE_TWO);
                }

                if (dto.getGrade() < PointConstant.SIXTY) {
                    return freeFlowService.toAnotherFlow(dto, (int) FlowConstant.FREE_YIN_YI_EXERCISE_ONE);
                }
            }
        }
        return null;
    }

}
