package com.zhidejiaoyu.student.business.flow.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.zhidejiaoyu.common.dto.NodeDto;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.study.PriorityUtil;
import com.zhidejiaoyu.common.vo.flow.FlowVO;
import com.zhidejiaoyu.student.business.flow.FlowCommonMethod;
import com.zhidejiaoyu.student.business.flow.FlowConstant;
import com.zhidejiaoyu.student.business.flow.service.StudyFlowService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wuchenxi
 */
@Slf4j
@Service(value = "flowService")
public class StudyFlowServiceImpl extends BaseServiceImpl<StudyFlowNewMapper, StudyFlowNew> implements StudyFlowService {

    @Resource
    private StudyFlowNewMapper studyFlowNewMapper;

    @Resource
    private StudentFlowNewMapper studentFlowNewMapper;

    @Resource
    private CourseNewMapper courseNewMapper;

    @Resource
    private UnitNewMapper unitNewMapper;

    @Resource
    private UnitVocabularyNewMapper unitVocabularyNewMapper;

    @Resource
    private UnitSentenceNewMapper unitSentenceNewMapper;

    @Resource
    private UnitTeksNewMapper unitTeksNewMapper;

    @Resource
    private LearnNewMapper learnNewMapper;

    @Resource
    private LearnExtendMapper learnExtendMapper;

    @Resource
    private StudentStudyPlanNewMapper studentStudyPlanNewMapper;

    @Resource
    private LearnHistoryMapper learnHistoryMapper;

    @Resource
    private FlowCommonMethod flowCommonMethod;

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

        if (dto.getNodeId() == null) {
            // 在星球页请求，返回当前正在学习的节点信息
            StudentStudyPlanNew studentStudyPlanNew = studentStudyPlanNewMapper.selectMaxFinalLevelByLimit(student.getId(), 1).get(0);

            if (studentStudyPlanNew == null) {
                throw new ServiceException("学生还没有进行摸底测试，未查询到可以学习的课程！");
            }

            StudentFlowNew studentFlowNew = studentFlowNewMapper.selectByStudentIdAndUnitId(student.getId(), studentStudyPlanNew.getUnitId());
            if (studentFlowNew != null) {
                StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(studentFlowNew.getCurrentFlowId());

                return ServerResponse.createBySuccess(packageFlowVO(studyFlowNew, student, studentFlowNew.getUnitId()));
            } else {

                this.initStudentFlow(student, studentStudyPlanNew);

                StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(studentStudyPlanNew.getFlowId());
                return ServerResponse.createBySuccess(packageFlowVO(studyFlowNew, student, studentStudyPlanNew.getUnitId()));
            }
        }

        StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(dto.getNodeId());

        if (studyFlowNew == null) {
            log.error("未查询到id={}的流程信息！", dto.getNodeId());
            throw new ServiceException("未查询到流程信息！");
        }

        String modelName = studyFlowNew.getModelName();
        int easyOrHard = modelName.contains("写") || Objects.equals(modelName, "课文训练") ? 2 : 1;
        dto.setEasyOrHard(easyOrHard);
        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitId(student.getId(), dto.getUnitId(), dto.getEasyOrHard());

        if (learnNew != null) {
            // 如果学生有当前单元的学习记录，删除其学习详情，防止学生重新学习该单元时获取不到题目
            learnExtendMapper.deleteByLearnId(learnNew.getId());
            dto.setGroup(learnNew.getGroup());
        }
        dto.setStudyFlowNew(studyFlowNew);
        dto.setSession(session);
        dto.setStudent(student);

        // 学习下一单元, 前端需要一个弹框提示
        if (studyFlowNew.getNextTrueFlow() == 0) {
            // 开启下一单元并且返回需要学习的流程信息
            return ServerResponse.createBySuccess(this.finishGroup(dto));
        }
        // 其余正常流程
        if (studyFlowNew.getNextFalseFlow() == null) {
            // 直接进入下个流程节点
            return this.toAnotherFlow(dto, studyFlowNew.getNextTrueFlow());
        }

        // 判断下个节点
        return flowCommonMethod.judgeNextNode(dto, this);
    }

    public void initStudentFlow(Student student, StudentStudyPlanNew studentStudyPlanNew) {
        studentFlowNewMapper.insert(StudentFlowNew.builder()
                .currentFlowId(studentStudyPlanNew.getFlowId())
                .unitId(studentStudyPlanNew.getUnitId())
                .studentId(student.getId())
                .updateTime(new Date())
                .type(1)
                .build());
    }

    public FlowVO packageFlowVO(StudyFlowNew studyFlowNew, Student student, Long unitId) {
        return flowCommonMethod.packageFlowVO(studyFlowNew, student, unitId);
    }


    /**
     * 进入其他流程
     *
     * @param dto
     * @param nextFlowId 下个节点id
     * @return
     */
    @Override
    public ServerResponse<Object> toAnotherFlow(NodeDto dto, int nextFlowId) {
        Student student = dto.getStudent();

        // 判断当前单元单词是否有图片，如果都没有图片不进入单词图鉴
        StudyFlowNew studyFlowNew = this.getStudyFlow(dto, nextFlowId);

        // 判断当前单元是否含有当前模块的内容，如果没有当前模块的内容学习下个模块的内容
        FlowVO flowVO = this.judgeHasCurrentModel(studyFlowNew, dto);

        studentFlowNewMapper.updateFlowIdByStudentIdAndUnitIdAndType(student.getId(), studyFlowNew.getId(), dto.getUnitId(), 1);

        return ServerResponse.createBySuccess("true", flowVO);
    }

    /**
     * 验证当前单元中的group是否包含当前学习模块，如果不包含，学习下个学习模块
     *
     * @param studyFlowNew
     * @param dto
     * @return
     */
    private FlowVO judgeHasCurrentModel(StudyFlowNew studyFlowNew, NodeDto dto) {
        String flowName = studyFlowNew.getFlowName();
        // 单词模块
        Long unitId = dto.getUnitId();
        Integer group = dto.getGroup();
        Student student = dto.getStudent();
        if (Objects.equals(flowName, FlowConstant.FLOW_ONE) || Objects.equals(flowName, FlowConstant.FLOW_TWO)) {
            Integer wordCount = unitVocabularyNewMapper.countUnitIdAndGroup(unitId, group);
            if (wordCount > 0) {
                return this.packageFlowVO(studyFlowNew, student, dto.getUnitId());
            }

            if (this.judgeHasSentenceModel(studyFlowNew, dto)) {
                return this.packageFlowVO(studyFlowNew, student, dto.getUnitId());
            }

            if (this.judgeHasTeksModel(studyFlowNew, dto)) {
                return this.packageFlowVO(studyFlowNew, student, dto.getUnitId());
            }

        }

        // 句型模块
        if (Objects.equals(flowName, FlowConstant.FLOW_THREE) || Objects.equals(flowName, FlowConstant.FLOW_FOUR)) {
            Integer sentenceCount = unitSentenceNewMapper.countByUnitIdAndGroup(unitId, group);
            if (sentenceCount > 0) {
                return this.packageFlowVO(studyFlowNew, student, dto.getUnitId());
            }

            // 没有句型模块判断是否有课文模块
            if (this.judgeHasTeksModel(studyFlowNew, dto)) {
                return this.packageFlowVO(studyFlowNew, student, dto.getUnitId());
            }
        }

        // 课文模块
        if (Objects.equals(flowName, FlowConstant.FLOW_FIVE)) {
            Integer teksCount = unitTeksNewMapper.countByUnitIdAndGroup(unitId, group);
            if (teksCount > 0) {
                return this.packageFlowVO(studyFlowNew, student, dto.getUnitId());
            }
        }

        // 学习完当前group
        return this.finishGroup(dto);
    }

    /**
     * 学习完当前group
     *
     * @param dto
     * @return
     */
    private FlowVO finishGroup(NodeDto dto) {
        StudentStudyPlanNew studentStudyPlanNew = studentStudyPlanNewMapper.selectMaxFinalLevelByLimit(dto.getStudent().getId(), 1).get(0);

        // 判断哪些模块有当前group
        this.judgeHasCurrentGroup(dto, studentStudyPlanNew);

        // 判断单元是否有下个group
        Integer group = this.judgeHasNextGroup(dto, studentStudyPlanNew);

        if (group == null) {
            // 说明当前单元学习完毕
            return this.finishUnit(dto);
        }

        Long nodeId = dto.getNodeId();
        StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(nodeId);

        return this.packageFlowVO(studyFlowNew, dto.getStudent(), dto.getUnitId());
    }

    /**
     * 判断是否有下个group，如果有，将下个group初始化到正在学习表中，如果没有，返回null，再判断单元信息
     *
     * @param dto
     * @param studentStudyPlanNew
     * @return
     */
    private Integer judgeHasNextGroup(NodeDto dto, StudentStudyPlanNew studentStudyPlanNew) {
        LearnNew learnNew = learnNewMapper.selectByStudentStudyPlanNew(studentStudyPlanNew);
        if (learnNew != null) {
            Long learnNewId = learnNew.getId();
            learnNewMapper.deleteById(learnNewId);
        }
        // 判断单词模块是否有下个group
        Integer group = unitVocabularyNewMapper.selectNextGroup(dto.getUnitId(), dto.getGroup());
        if (group != null) {
            this.saveLearnNew(dto, studentStudyPlanNew, group);
            return group;
        }

        // 判断句型模块是否有下个group
        group = unitSentenceNewMapper.selectNextGroup(dto.getUnitId(), dto.getGroup());
        if (group != null) {
            this.saveLearnNew(dto, studentStudyPlanNew, group);
            return group;
        }

        // 判断课文模块是否有下个group
        group = unitTeksNewMapper.selectNextGroup(dto.getUnitId(), dto.getGroup());
        if (group != null) {
            this.saveLearnNew(dto, studentStudyPlanNew, group);
            return group;
        }
        return null;
    }

    private void saveLearnNew(NodeDto dto, StudentStudyPlanNew studentStudyPlanNew, Integer group) {
        learnNewMapper.insert(LearnNew.builder()
                .easyOrHard(studentStudyPlanNew.getEasyOrHard())
                .group(group)
                .studentId(dto.getStudent().getId())
                .unitId(studentStudyPlanNew.getUnitId())
                .updateTime(new Date())
                .courseId(studentStudyPlanNew.getCourseId())
                .build());
    }

    /**
     * 判断哪些模块有当前group，并更新或新增当前group的学习已完成表
     *
     * @param dto
     * @param studentStudyPlanNew
     */
    private void judgeHasCurrentGroup(NodeDto dto, StudentStudyPlanNew studentStudyPlanNew) {
        Long unitId = dto.getUnitId();
        Integer group = dto.getGroup();
        Integer count = unitVocabularyNewMapper.countUnitIdAndGroup(unitId, group);
        this.saveOrUpdateLearnHistory(dto, studentStudyPlanNew, count, 1);

        count = unitSentenceNewMapper.countByUnitIdAndGroup(unitId, group);
        this.saveOrUpdateLearnHistory(dto, studentStudyPlanNew, count, 2);

        count = unitTeksNewMapper.countByUnitIdAndGroup(unitId, group);
        this.saveOrUpdateLearnHistory(dto, studentStudyPlanNew, count, 4);
    }

    private void saveOrUpdateLearnHistory(NodeDto dto, StudentStudyPlanNew studentStudyPlanNew, Integer count,
                                          int type) {
        if (count > 0) {
            // 查询已完成表中是否已有当前group信息，有更新，没有新增
            LearnHistory.LearnHistoryBuilder builder = LearnHistory.builder()
                    .studentId(dto.getStudent().getId())
                    .unitId(dto.getUnitId())
                    .group(dto.getGroup())
                    .easyOrHard(studentStudyPlanNew.getEasyOrHard())
                    .type(type);
            LearnHistory learnHistory = learnHistoryMapper.selectOne(builder.build());
            if (learnHistory != null) {
                learnHistory.setState(1);
                learnHistory.setUpdateTime(new Date());
                learnHistory.setStudyCount(learnHistory.getStudyCount() + 1);
                learnHistoryMapper.updateById(learnHistory);
            } else {
                learnHistoryMapper.insert(builder
                        .state(1)
                        .studyCount(1)
                        .updateTime(new Date())
                        .build());
            }
        }
    }

    /**
     * 单元完成操作
     *
     * @param dto
     * @return
     */
    private FlowVO finishUnit(NodeDto dto) {
        Student student = dto.getStudent();
        Long studentId = student.getId();

        // 更新优先级表中的变化优先级
        this.updateLevel(dto, studentId);

        flowCommonMethod.saveOpenUnitLog(student, dto.getUnitId());

        StudentStudyPlanNew maxStudentStudyPlanNew = this.initLearnInfo(dto, studentId);

        // 将当前单元的已学习记录状态置为已完成
        learnHistoryMapper.updateStateByStudentIdAndUnitId(studentId, dto.getUnitId(), 2);

        Long flowId = maxStudentStudyPlanNew.getFlowId();
        StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(flowId);

        return packageFlowVO(studyFlowNew, student, maxStudentStudyPlanNew.getUnitId());

    }

    /**
     * 根据优先级初始化学习表数据
     *
     * @param dto
     * @param studentId
     * @return
     */
    private StudentStudyPlanNew initLearnInfo(NodeDto dto, Long studentId) {
        List<StudentStudyPlanNew> studentStudyPlanNews = studentStudyPlanNewMapper.selectMaxFinalLevelByLimit(studentId, 5);
        StudentStudyPlanNew maxStudentStudyPlanNew = studentStudyPlanNews.get(0);
        StudentStudyPlanNew finalMaxStudentStudyPlanNew = maxStudentStudyPlanNew;
        List<StudentStudyPlanNew> collect = studentStudyPlanNews.stream()
                .filter(studentStudyPlanNew -> Objects.equals(studentStudyPlanNew.getFinalLevel(), finalMaxStudentStudyPlanNew.getFinalLevel()))
                .collect(Collectors.toList());
        if (collect.size() > 1) {
            // 说明有相同优先级的数据，取最接近当前单元的一个
            // 与当前单元的差值
            int difference = -1;
            // 最接近当前单元的记录
            for (StudentStudyPlanNew studentStudyPlanNew : collect) {
                Long unitId = studentStudyPlanNew.getUnitId();
                long abs = Math.abs(unitId - dto.getUnitId());
                if (difference == -1 || abs < difference) {
                    difference = (int) abs;
                    maxStudentStudyPlanNew = studentStudyPlanNew;
                }
            }
        }
        this.saveLearn(maxStudentStudyPlanNew);
        return maxStudentStudyPlanNew;
    }

    /**
     * 单元学习完成，更新优先级表
     *
     * @param dto
     * @param studentId
     */
    private void updateLevel(NodeDto dto, Long studentId) {
        StudentStudyPlanNew maxFinalLevel = studentStudyPlanNewMapper.selectMaxFinalLevelByLimit(studentId, 1).get(0);

        CourseNew currentCourse = courseNewMapper.selectById(maxFinalLevel.getCourseId());
        String gradeExt = currentCourse.getGradeExt();
        String currentGrade = StringUtils.isNotBlank(gradeExt) ? gradeExt : currentCourse.getGrade();
        PriorityUtil.finishUnitUpdateErrorLevel(maxFinalLevel, dto.getStudent().getGrade(), currentGrade);
        studentStudyPlanNewMapper.updateById(maxFinalLevel);
    }

    /**
     * 如果学习表中没有最高优先级的数据，新增
     *
     * @param studentStudyPlanNew
     */
    private void saveLearn(StudentStudyPlanNew studentStudyPlanNew) {
        Integer count = learnNewMapper.selectCount(new EntityWrapper<LearnNew>()
                .eq("student_id", studentStudyPlanNew.getStudentId())
                .eq("unit_id", studentStudyPlanNew.getUnitId())
                .eq("easy_or_hard", studentStudyPlanNew.getEasyOrHard()));
        if (count == 0) {
            learnNewMapper.insert(LearnNew.builder()
                    .courseId(studentStudyPlanNew.getCourseId())
                    .easyOrHard(studentStudyPlanNew.getEasyOrHard())
                    .group(1)
                    .studentId(studentStudyPlanNew.getStudentId())
                    .unitId(studentStudyPlanNew.getUnitId())
                    .updateTime(new Date())
                    .build());
        }

    }

    /**
     * 判断当前单元group是否有句型模块
     *
     * @param studyFlowNew
     * @param dto
     * @return
     */
    private boolean judgeHasSentenceModel(StudyFlowNew studyFlowNew, NodeDto dto) {
        // 没有单词模块判断是否有句型模块
        Integer sentenceCount = unitSentenceNewMapper.countByUnitIdAndGroup(dto.getUnitId(), dto.getGroup());
        if (sentenceCount > 0) {
            studyFlowNew.setId(85L);
            return true;
        }
        return false;
    }

    /**
     * 判断当前单元group有没有句型模块
     *
     * @param studyFlowNew
     * @param dto
     * @return
     */
    private boolean judgeHasTeksModel(StudyFlowNew studyFlowNew, NodeDto dto) {
        // 没有句型模块判断是否有课文模块
        Integer teksCount = unitTeksNewMapper.countByUnitIdAndGroup(dto.getUnitId(), dto.getGroup());
        if (teksCount > 0) {
            studyFlowNew.setId(89L);
            return true;
        }
        return false;
    }

    /**
     * @param dto
     * @param nextFlowId 下个流程节点的 id
     * @return
     */
    private StudyFlowNew getStudyFlow(NodeDto dto, int nextFlowId) {
        StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(nextFlowId);

        // 如果下个节点不是单词图鉴模块，执行正常流程
        String studyModel = "单词图鉴";
        if (!studyFlowNew.getModelName().contains(studyModel)) {
            return studyFlowNew;
        }

        Long unitId = dto.getUnitId();
        if (unitId == null) {
            return studyFlowNew;
        }

        // 当前单元含有图片的单词个数，如果大于零，执行正常流程，否则跳过单词图鉴模块
        int pictureCount = unitVocabularyNewMapper.countPicture(unitId);
        if (pictureCount > 0) {
            return studyFlowNew;
        }

        UnitNew unitNew = unitNewMapper.selectById(unitId);
        Student student = dto.getStudent() == null ? new Student() : dto.getStudent();

        // 需要跳转到的流程 id
        int flowId1;
        // 流程 1 单词图鉴流程 id
        int flowOnePicture = 73;
        // 流程 1 的单词图鉴
        if (nextFlowId == flowOnePicture) {
            if (dto.getGrade() != null && dto.getGrade() >= dto.getStudyFlowNew().getType()) {
                // 去句型翻译
                flowId1 = 85;
                flowCommonMethod.changeFlowNodeLog(student, "句型翻译", unitNew, flowId1);
                return studyFlowNewMapper.selectById(flowId1);
            }
            // 如果是从单词播放机直接进入单词图鉴，将流程跳转到慧记忆
            if (Objects.equals(dto.getNodeId(), 78L)) {
                flowId1 = 71;
                flowCommonMethod.changeFlowNodeLog(student, "慧记忆", unitNew, flowId1);
                return studyFlowNewMapper.selectById(flowId1);
            }
            // 返回流程 1
            flowId1 = 70;
            flowCommonMethod.changeFlowNodeLog(student, "单词播放机", unitNew, flowId1);
            return studyFlowNewMapper.selectById(flowId1);
        }

        return studyFlowNew;
    }
}
