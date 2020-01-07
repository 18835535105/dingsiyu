package com.zhidejiaoyu.student.business.flow.service.impl;

import com.zhidejiaoyu.common.dto.NodeDto;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.CcieUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.flow.FlowVO;
import com.zhidejiaoyu.student.business.flow.common.*;
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
    private LearnHistoryMapper learnHistoryMapper;

    @Resource
    private JudgeNextNode judgeNextNode;

    @Resource
    private LearnExtendMapper learnExtendMapper;

    @Resource
    private StudyCapacityMapper studyCapacityMapper;

    @Resource
    private PackageFlowVO packageFlowVO;

    @Resource
    private JudgeWordPicture judgeWordPicture;

    @Resource
    private InitData initData;

    @Resource
    private FinishGroupOrUnit finishGroupOrUnit;

    /**
     * 流程名称与 studyCapacity 中 type 的映射
     */
    private static final Map<String, Integer> FLOW_NAME_TO_STUDY_CAPACITY_TYPE = new HashMap<>();

    static {
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("单词图鉴", 1);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("慧记忆", 3);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("慧听写", 4);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("慧默写", 5);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("字母填写", 6);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("句型翻译", 7);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("句型听力", 8);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("音译练习", 9);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("句型默写", 10);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("课文试听", 11);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("课文训练", 12);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("闯关测试", 13);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("学语法", 20);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("选语法", 21);
        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.put("写语法", 22);
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
        dto.setStudent(student);

        if (dto.getNodeId() == null) {
            return this.getIndexNodeResponse(dto);
        }

        StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(dto.getNodeId());

        Long studentId = student.getId();

        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHard(studentId, dto.getUnitId(), dto.getEasyOrHard());

        if (learnNew != null) {
            // 如果学生有当前单元的学习记录，删除其学习详情，防止学生重新学习该单元时获取不到题目
            String modelName = studyFlowNew.getModelName();
            if (FLOW_NAME_TO_STUDY_CAPACITY_TYPE.containsKey(modelName)) {
                studyCapacityMapper.deleteByStudentIdAndUnitIdAndType(studentId, dto.getUnitId(),
                        FLOW_NAME_TO_STUDY_CAPACITY_TYPE.get(modelName));
            }

            learnExtendMapper.deleteByUnitIdAndStudyModel(learnNew.getId(), this.getModelName(modelName));
            dto.setGroup(learnNew.getGroup());
            dto.setLearnNew(learnNew);
        } else {
            dto.setGroup(1);
        }

        dto.setStudyFlowNew(studyFlowNew);
        dto.setSession(session);
        dto.setStudent(student);

        if (studyFlowNew == null) {
            return null;
        }

        // 学习下一单元, 前端需要一个弹框提示
        if (studyFlowNew.getNextTrueFlow() == 0) {

            if (checkNextUnitOrGroup(dto, studyFlowNew)) {
                // 保存证书
                this.judgeCourseCcie(dto);

                // 验证当前模块是否有下一个group，如果有初始化，没有说明当前单元学习完毕
                if (learnNew != null) {
                    return finishGroupOrUnit.finishFreeGroup(dto);
                }
                return ServerResponse.createBySuccess(ResponseCode.UNIT_FINISH);
            }
        }
        // 其余正常流程
        if (studyFlowNew.getNextFalseFlow() == null) {
            // 直接进入下个流程节点
            return this.toAnotherFlow(dto, studyFlowNew.getNextTrueFlow());
        }
        // 判断下个节点
        return judgeNextNode.judgeNextNode(dto, this);
    }

    /**
     * 匹配learnExtend中的studyModel
     *
     * @param modelName
     * @return
     */
    private String getModelName(String modelName) {
        if (modelName.contains("句型")) {
            return modelName.replace("句型", "例句");
        }
        if (Objects.equals("字母填写", modelName)) {
            return "单词填字";
        }
        return modelName;
    }

    /**
     * 学生选择单元的时候返回节点信息
     *
     * @param dto
     * @return
     */
    public ServerResponse<Object> getIndexNodeResponse(NodeDto dto) {
        Student student = dto.getStudent();
        Long studentId = student.getId();
        Long unitId = dto.getUnitId();
        Integer easyOrHard = dto.getEasyOrHard();
        Integer modelType = dto.getModelType();

        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHard(studentId, unitId, easyOrHard);
        if (learnNew != null) {
            StudyFlowNew studyFlowNew = studyFlowNewMapper.selectByLearnId(learnNew.getId());
            if (studyFlowNew != null) {
                return ServerResponse.createBySuccess(this.packageFlowVO(studyFlowNew, student, unitId));
            }
            return this.getFlowVoServerResponse(learnNew, modelType, student);
        }

        UnitNew unitNew = unitNewMapper.selectById(unitId);
        learnNew = initData.saveLearnNew(NodeDto.builder()
                .student(student)
                .courseId(unitNew.getCourseId())
                .unitId(unitId)
                .easyOrHard(easyOrHard)
                .build(), 1);
        return this.getFlowVoServerResponse(learnNew, modelType, student);
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
    public ServerResponse<Object> toAnotherFlow(NodeDto dto, int nextFlowId) {
        // 判断当前单元单词是否有图片，如果都没有图片不进入单词图鉴
        return this.getStudyFlow(dto, nextFlowId);
    }

    public ServerResponse<Object> getFlowVoServerResponse(LearnNew learnNew, Integer modelType, Student student) {
        Long studentId = student.getId();

        StudentFlowNew studentFlowNew = studentFlowNewMapper.selectByLearnId(learnNew.getId());
        Long startFlowId = finishGroupOrUnit.getStartFlowId(learnNew.getEasyOrHard(), modelType);
        if (studentFlowNew == null) {
            studentFlowNewMapper.insert(StudentFlowNew.builder()
                    .type(modelType)
                    .updateTime(new Date())
                    .studentId(studentId)
                    .learnId(learnNew.getId())
                    .currentFlowId(startFlowId)
                    .build());
        } else {
            studentFlowNew.setCurrentFlowId(startFlowId);
            studentFlowNew.setUpdateTime(new Date());
            studentFlowNew.setLearnId(learnNew.getId());
            studentFlowNewMapper.updateById(studentFlowNew);
        }
        StudyFlowNew studyFlowNew1 = studyFlowNewMapper.selectById(startFlowId);
        return ServerResponse.createBySuccess(this.packageFlowVO(studyFlowNew1, student, learnNew.getUnitId()));
    }

    public FlowVO packageFlowVO(StudyFlowNew studyFlowNew, Student student, Long unitId) {
        return packageFlowVO.packageFlowVO(studyFlowNew, student, unitId);
    }

    /**
     * @param dto
     * @param nextFlowId 下个流程节点的 id
     * @return
     */
    private ServerResponse<Object> getStudyFlow(NodeDto dto, int nextFlowId) {

        Student student = dto.getStudent() == null ? new Student() : dto.getStudent();

        StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(nextFlowId);

        boolean canStudyWordPicture = judgeWordPicture.judgeWordPicture(dto, studyFlowNew);
        if (canStudyWordPicture) {
            studentFlowNewMapper.updateFlowIdByStudentIdAndUnitIdAndType(studyFlowNew.getId(), dto.getLearnNew().getId());
            FlowVO flowVo = this.packageFlowVO(studyFlowNew, student, dto.getUnitId());
            return ServerResponse.createBySuccess(flowVo);
        }

        return finishGroupOrUnit.finishFreeGroup(dto);
    }
}
