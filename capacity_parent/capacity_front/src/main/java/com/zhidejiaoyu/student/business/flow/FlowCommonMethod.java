package com.zhidejiaoyu.student.business.flow;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.constant.study.PointConstant;
import com.zhidejiaoyu.common.dto.NodeDto;
import com.zhidejiaoyu.common.mapper.CourseNewMapper;
import com.zhidejiaoyu.common.mapper.LearnExtendMapper;
import com.zhidejiaoyu.common.mapper.StudyFlowNewMapper;
import com.zhidejiaoyu.common.mapper.UnitNewMapper;
import com.zhidejiaoyu.common.pojo.CourseNew;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudyFlowNew;
import com.zhidejiaoyu.common.pojo.UnitNew;
import com.zhidejiaoyu.common.utils.TokenUtil;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.flow.FlowVO;
import com.zhidejiaoyu.student.business.flow.service.StudyFlowService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author: wuchenxi
 * @date: 2019/12/28 10:16:16
 */
@Component
public class FlowCommonMethod {

    @Resource
    private StudyFlowNewMapper studyFlowNewMapper;

    @Resource
    private UnitNewMapper unitNewMapper;

    @Resource
    private CourseNewMapper courseNewMapper;

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
            if (Objects.equals(gameTestFlowId, falseFlow.getType())) {
                ServerResponse<Object> x = this.toNextNode(dto, falseFlow, studyFlowService);
                if (x != null) {
                    return x;
                }
            } else if (Objects.equals(gameTestFlowId, trueFlow.getType())) {
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

    /**
     * 封装响应信息
     *
     * @param studyFlowNew
     * @param unitId
     * @return
     */
    public FlowVO packageFlowVO(StudyFlowNew studyFlowNew, Long unitId) {
        UnitNew unitNew = unitNewMapper.selectById(unitId);
        CourseNew courseNew = courseNewMapper.selectById(unitNew.getCourseId());
        String token = TokenUtil.getToken();
        HttpUtil.getHttpSession().setAttribute("token", token);

        Student student = (Student) HttpUtil.getHttpSession().getAttribute(UserConstant.CURRENT_STUDENT);
        LearnExtendMapper.del

        return FlowVO.builder()
                .courseId(courseNew.getId())
                .courseName(courseNew.getCourseName())
                .id(studyFlowNew.getId())
                .modelName(studyFlowNew.getModelName())
                .unitId(unitNew.getId())
                .unitName(unitNew.getUnitName())
                .token(token)
                .build();
    }
}
