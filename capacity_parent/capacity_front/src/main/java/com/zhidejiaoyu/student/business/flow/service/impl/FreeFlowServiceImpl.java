package com.zhidejiaoyu.student.business.flow.service.impl;

import com.zhidejiaoyu.common.dto.NodeDto;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.CcieUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.flow.FlowVO;
import com.zhidejiaoyu.student.business.flow.FlowCommonMethod;
import com.zhidejiaoyu.student.business.flow.FlowConstant;
import com.zhidejiaoyu.student.business.flow.service.StudyFlowService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 自由学习节点
 *
 * @author: wuchenxi
 * @date: 2019/12/26 14:33:33
 */
@Slf4j
@Service(value = "freeFlowService")
public class FreeFlowServiceImpl extends BaseServiceImpl<StudyFlowNewMapper, StudyFlowNew> implements StudyFlowService {

    /**
     * 流程名称与 studentStudyPlanNew 中type值的映射
     */
    private static final Map<String, Integer> FLOW_NAME_TO_TYPE = new HashMap<>();

    @Resource
    private StudyFlowNewMapper studyFlowNewMapper;

    @Resource
    private StudentFlowNewMapper studentFlowNewMapper;

    @Resource
    private LearnNewMapper learnNewMapper;

    @Resource
    private UnitNewMapper unitNewMapper;

    @Resource
    private CcieMapper ccieMapper;

    @Resource
    private CcieUtil ccieUtil;

    @Resource
    private UnitVocabularyNewMapper unitVocabularyNewMapper;

    @Resource
    private LearnHistoryMapper learnHistoryMapper;

    @Resource
    private FlowCommonMethod flowCommonMethod;

    @Resource
    private LearnExtendMapper learnExtendMapper;

    static {
        FLOW_NAME_TO_TYPE.put(FlowConstant.FLOW_ONE, 2);
        FLOW_NAME_TO_TYPE.put(FlowConstant.FLOW_TWO, 2);
        FLOW_NAME_TO_TYPE.put(FlowConstant.FLOW_THREE, 3);
        FLOW_NAME_TO_TYPE.put(FlowConstant.FLOW_FOUR, 3);
        FLOW_NAME_TO_TYPE.put(FlowConstant.FLOW_FIVE, 4);
    }

    /**
     * 节点学完, 把下一节初始化到student_flow表, 并把下一节点返回
     *
     * @return id 节点id
     * model_name 节点模块名
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> getNode(NodeDto dto, String isTrueFlow, HttpSession session) {
        Student student = super.getStudent(session);

        Long studentId = student.getId();

        Long unitId = dto.getUnitId();
        Integer easyOrHard = dto.getEasyOrHard();
        Integer modelType = dto.getModelType();

        if (dto.getNodeId() == null) {
            LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHard(studentId, unitId, easyOrHard);
            if (learnNew != null) {
                StudyFlowNew studyFlowNew = studyFlowNewMapper.selectByStudentIdAndUnitIdAndType(studentId, unitId, modelType);
                if (studyFlowNew != null) {
                    return ServerResponse.createBySuccess(this.packageFlowVO(studyFlowNew, unitId));
                }
                return this.getFlowVoServerResponse(unitId, easyOrHard, modelType, studentId);
            }

            UnitNew unitNew = unitNewMapper.selectById(unitId);
            learnNewMapper.insert(LearnNew.builder()
                    .easyOrHard(easyOrHard)
                    .group(1)
                    .studentId(studentId)
                    .unitId(unitId)
                    .updateTime(new Date())
                    .courseId(unitNew.getCourseId())
                    .build());
            return this.getFlowVoServerResponse(unitId, easyOrHard, modelType, studentId);
        }

        StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(dto.getNodeId());

        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitId(dto.getStudent().getId(), dto.getUnitId(), dto.getEasyOrHard());

        if (learnNew != null) {
            // 如果学生有当前单元的学习记录，删除其学习详情，防止学生重新学习该单元时获取不到题目
            learnExtendMapper.deleteByUnitIdAndStudyModel(learnNew.getId(), studyFlowNew.getModelName());
            dto.setGroup(learnNew.getGroup());
        }

        dto.setStudyFlowNew(studyFlowNew);
        dto.setSession(session);
        dto.setStudent(student);

        if (studyFlowNew == null) {
            return null;
        }

        // 学习下一单元, 前端需要一个弹框提示
        if (studyFlowNew.getNextTrueFlow() == 0) {
            // 开启下一单元并且返回需要学习的流程信息
            flowCommonMethod.saveOpenUnitLog(student, unitId);
            // 保存证书
            this.judgeCourseCcie(dto);
            return ServerResponse.createBySuccess(ResponseCode.UNIT_FINISH);
        }
        // 其余正常流程
        if (studyFlowNew.getNextFalseFlow() == null) {
            // 直接进入下个流程节点
            return this.toAnotherFlow(dto, studyFlowNew.getNextTrueFlow());
        }
        // 判断下个节点
        return flowCommonMethod.judgeNextNode(dto, this);
    }

    private void judgeCourseCcie(NodeDto dto) {
        // 判断学生是否能获取课程证书,每个课程智能获取一个课程证书
        Student student = dto.getStudent();
        Long courseId = dto.getCourseId();
        int count = ccieMapper.countCourseCcieByCourseId(student.getId(), courseId);
        if (count == 0) {
            int unitCount = unitNewMapper.countByCourseId(courseId);
            int learnedUnitCount = learnHistoryMapper.countUnitIdByCourseId(student.getId(), courseId);
            if (learnedUnitCount >= unitCount) {
                // 课程学习完毕，奖励学生课程证书
                Long grade = dto.getGrade();
                ccieUtil.saveCourseCcie(student, courseId, dto.getUnitId(), grade == null ? 0 : Integer.parseInt(grade.toString()));
            }
        }
    }

    /**
     * 进入其他流程
     *
     * @return
     */
    @Override
    public ServerResponse<Object> toAnotherFlow(NodeDto dto, int flowId) {
        Student student = dto.getStudent();

        // 判断当前单元单词是否有图片，如果都没有图片不进入单词图鉴
        StudyFlowNew studyFlowNew = this.getStudyFlow(dto, flowId);
        studentFlowNewMapper.updateFlowIdByStudentIdAndUnitIdAndType(student.getId(), studyFlowNew.getId(),
                dto.getUnitId(), FLOW_NAME_TO_TYPE.get(studyFlowNew.getFlowName()));

        FlowVO flowVo = this.packageFlowVO(studyFlowNew, dto.getUnitId());
        return ServerResponse.createBySuccess("true", flowVo);
    }

    public ServerResponse<Object> getFlowVoServerResponse(Long unitId, Integer easyOrHard, Integer modelType, Long studentId) {
        StudentFlowNew studentFlowNew = studentFlowNewMapper.selectByStudentIdAndUnitIdAndType(studentId, unitId, modelType);
        Long startFlowId = this.getStartFlowId(easyOrHard, modelType);
        if (studentFlowNew == null) {
            studentFlowNewMapper.insert(StudentFlowNew.builder()
                    .type(modelType)
                    .updateTime(new Date())
                    .studentId(studentId)
                    .unitId(unitId)
                    .currentFlowId(startFlowId)
                    .build());
        } else {
            studentFlowNew.setCurrentFlowId(startFlowId);
            studentFlowNew.setUpdateTime(new Date());
            studentFlowNew.setUnitId(unitId);
            studentFlowNewMapper.updateById(studentFlowNew);
        }
        StudyFlowNew studyFlowNew1 = studyFlowNewMapper.selectById(startFlowId);
        return ServerResponse.createBySuccess(this.packageFlowVO(studyFlowNew1, unitId));
    }

    /**
     * 获取当前模块起始节点
     *
     * @param easyOrHard
     * @param type
     * @return
     */
    private Long getStartFlowId(Integer easyOrHard, Integer type) {
        switch (type) {
            case 2:
                return Objects.equals(easyOrHard, 1) ? FlowConstant.FREE_PLAYER : FlowConstant.FREE_LETTER_WRITE;
            case 3:
                return Objects.equals(easyOrHard, 1) ? FlowConstant.FREE_SENTENCE_TRANSLATE : FlowConstant.FREE_SENTENCE_WRITE;
            case 4:
                return FlowConstant.FREE_TEKS_LISTEN;
            default:
                return null;
        }
    }

    public FlowVO packageFlowVO(StudyFlowNew studyFlowNew, Long unitId) {
        return flowCommonMethod.packageFlowVO(studyFlowNew, unitId);
    }

    /**
     * @param dto
     * @param flowId 下个流程节点的 id
     * @return
     */
    private StudyFlowNew getStudyFlow(NodeDto dto, int flowId) {
        StudyFlowNew byPrimaryKey = studyFlowNewMapper.selectById(flowId);

        // 如果下个节点不是单词图鉴模块，执行正常流程
        String studyModel = "单词图鉴";
        if (!byPrimaryKey.getModelName().contains(studyModel)) {
            return byPrimaryKey;
        }

        Long unitId = dto.getUnitId();
        if (unitId == null) {
            return byPrimaryKey;
        }

        // 当前单元含有图片的单词个数，如果大于零，执行正常流程，否则跳过单词图鉴模块
        int pictureCount = unitVocabularyNewMapper.countPicture(unitId);
        if (pictureCount > 0) {
            return byPrimaryKey;
        }

        UnitNew unitNew = unitNewMapper.selectById(unitId);
        Student student = dto.getStudent() == null ? new Student() : dto.getStudent();

        // 需要跳转到的流程 id
        int flowId1;
        // 流程 1 单词图鉴流程 id
        int flowOnePicture = 15;
        // 流程 1 的单词图鉴
        if (flowId == flowOnePicture) {
            if (dto.getGrade() != null && dto.getGrade() >= dto.getStudyFlowNew().getType()) {
                // 去流程 2 的慧听写
                flowId1 = 18;
                flowCommonMethod.changeFlowNodeLog(student, "慧听写", unitNew, flowId1);
                return studyFlowNewMapper.selectById(flowId1);
            }
            // 如果是从单词播放机直接进入单词图鉴，将流程跳转到慧记忆
            if (Objects.equals(dto.getNodeId(), 22L)) {
                flowId1 = 48;
                flowCommonMethod.changeFlowNodeLog(student, "慧记忆", unitNew, flowId1);
                return studyFlowNewMapper.selectById(flowId1);
            }
            // 返回流程 1
            flowId1 = 9;
            flowCommonMethod.changeFlowNodeLog(student, "单词播放机", unitNew, flowId1);
            return studyFlowNewMapper.selectById(flowId1);
        }

        return byPrimaryKey;
    }
}
