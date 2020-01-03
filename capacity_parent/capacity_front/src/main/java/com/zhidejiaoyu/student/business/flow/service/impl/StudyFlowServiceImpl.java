package com.zhidejiaoyu.student.business.flow.service.impl;

import com.zhidejiaoyu.common.constant.test.GenreConstant;
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

    @Resource
    private SyntaxUnitTopicNewMapper syntaxUnitTopicNewMapper;

    @Resource
    private StudyCapacityMapper studyCapacityMapper;

    @Resource
    private TestRecordMapper testRecordMapper;

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

        // 星球页请求数据，获取当前应该学习的节点数据
        if (dto.getNodeId() == null) {
            return this.getIndexNodeResponse(student);
        }

        StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(dto.getNodeId());

        if (studyFlowNew == null) {
            log.error("未查询到id={}的流程信息！", dto.getNodeId());
            throw new ServiceException("未查询到流程信息！");
        }

        String modelName = studyFlowNew.getModelName();
        int easyOrHard = modelName.contains("写") || Objects.equals(modelName, "课文训练") ? 2 : 1;
        dto.setEasyOrHard(easyOrHard);
        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHard(student.getId(), dto.getUnitId(), dto.getEasyOrHard());
        if (learnNew != null) {
            // 如果学生有当前单元的学习记录，删除其学习详情，防止学生重新学习该单元时获取不到题目
            studyCapacityMapper.deleteByStudentIdAndUnitIdAndGroup(student.getId(), dto.getUnitId(), learnNew.getGroup());
            learnExtendMapper.deleteByLearnId(learnNew.getId());
            dto.setGroup(learnNew.getGroup());
            dto.setLearnNew(learnNew);
        } else {
            dto.setGroup(1);
        }
        dto.setStudyFlowNew(studyFlowNew);
        dto.setSession(session);
        dto.setStudent(student);

        if (studyFlowNew.getNextTrueFlow() == 0) {
            /*
                studyFlowNew.getType()=null，说明直接进入下一单元或者group
                studyFlowNew.getType()!=null并且测试分数大于或者等于需要达到的分数，说明直接进入下一单元或者group
                其他条件继续走下面的条件
             */
            boolean nextUnitOrGroup = (studyFlowNew.getType() == null)
                    || (dto.getGrade() != null && studyFlowNew.getType() != null && dto.getGrade() >= studyFlowNew.getType());
            if (nextUnitOrGroup) {
                return ServerResponse.createBySuccess(this.finishGroup(dto));
            }
        }
        // 其余正常流程
        if (studyFlowNew.getNextFalseFlow() == null) {
            // 直接进入下个流程节点
            return this.toAnotherFlow(dto, studyFlowNew.getNextTrueFlow());
        }

        // 判断下个节点
        return flowCommonMethod.judgeNextNode(dto, this);
    }

    /**
     * 学生在星球页获取应该学习的节点数据
     *
     * @param student
     * @return
     */
    public ServerResponse<Object> getIndexNodeResponse(Student student) {
        // 在星球页请求，返回当前正在学习的节点信息
        StudentFlowNew studentFlowNew = studentFlowNewMapper.selectByStudentIdAndType(student.getId(), 1);

        StudyFlowNew studyFlowNew;
        LearnNew learnNew;
        if (studentFlowNew == null) {
            TestRecord testRecord = testRecordMapper.selectByGenre(student.getId(), GenreConstant.TEST_BEFORE_STUDY);
            if (testRecord == null) {
                throw new ServiceException("学生还没有进行摸底测试，未查询到可以学习的课程！");
            }
            // 如果学生已经进行过摸底测试，初始化流程节点以及学习记录
            StudentStudyPlanNew maxFinalLevelStudentStudyPlanNew = studentStudyPlanNewMapper.selectMaxFinalLevelByLimit(student.getId(), 1).get(0);

            learnNew = this.saveLearn(maxFinalLevelStudentStudyPlanNew);

            studentFlowNewMapper.deleteByLearnId(learnNew.getId());
            this.initStudentFlow(student.getId(), maxFinalLevelStudentStudyPlanNew.getFlowId(), learnNew.getId());
            studyFlowNew = studyFlowNewMapper.selectById(maxFinalLevelStudentStudyPlanNew.getFlowId());
        } else {
            studyFlowNew = studyFlowNewMapper.selectById(studentFlowNew.getCurrentFlowId());
            learnNew = learnNewMapper.selectById(studentFlowNew.getLearnId());
        }

        FlowVO vo = this.packageFlowVO(studyFlowNew, student, learnNew.getUnitId());
        return ServerResponse.createBySuccess(vo);
    }

    public void initStudentFlow(Long studentId, Long flowId, Long learnId) {
        studentFlowNewMapper.insert(StudentFlowNew.builder()
                .currentFlowId(flowId)
                .learnId(learnId)
                .studentId(studentId)
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

        // 判断当前单元单词是否有图片，如果都没有图片不进入单词图鉴
        StudyFlowNew studyFlowNew = this.getStudyFlow(dto, nextFlowId);

        // 判断当前单元是否含有当前模块的内容，如果没有当前模块的内容学习下个模块的内容
        FlowVO flowVO = this.judgeHasCurrentModel(studyFlowNew, dto);

        studentFlowNewMapper.updateFlowIdByStudentIdAndUnitIdAndType(studyFlowNew.getId(), dto.getLearnNew().getId());

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
                return this.judgeHasCurrentModel(studyFlowNew, student, dto.getUnitId());
            }

            if (this.judgeHasSentenceModel(studyFlowNew, dto)) {
                return this.judgeHasCurrentModel(studyFlowNew, student, dto.getUnitId());
            }

            if (this.judgeHasTeksModel(studyFlowNew, dto)) {
                return this.judgeHasCurrentModel(studyFlowNew, student, dto.getUnitId());
            }

            if (this.judgeHasSyntaxModel(studyFlowNew, dto)) {
                return this.judgeHasCurrentModel(studyFlowNew, student, dto.getUnitId());
            }

        }

        // 句型模块
        if (Objects.equals(flowName, FlowConstant.FLOW_THREE) || Objects.equals(flowName, FlowConstant.FLOW_FOUR)) {
            Integer sentenceCount = unitSentenceNewMapper.countByUnitIdAndGroup(unitId, group);
            if (sentenceCount > 0) {
                return this.judgeHasCurrentModel(studyFlowNew, student, dto.getUnitId());
            }

            // 没有句型模块判断是否有课文模块
            if (this.judgeHasTeksModel(studyFlowNew, dto)) {
                return this.judgeHasCurrentModel(studyFlowNew, student, dto.getUnitId());
            }

            if (this.judgeHasSyntaxModel(studyFlowNew, dto)) {
                return this.judgeHasCurrentModel(studyFlowNew, student, dto.getUnitId());
            }
        }

        // 课文模块
        if (Objects.equals(flowName, FlowConstant.FLOW_FIVE)) {
            Integer teksCount = unitTeksNewMapper.countByUnitIdAndGroup(unitId, group);
            if (teksCount > 0) {
                return this.judgeHasCurrentModel(studyFlowNew, student, dto.getUnitId());
            }

            if (this.judgeHasSyntaxModel(studyFlowNew, dto)) {
                return this.judgeHasCurrentModel(studyFlowNew, student, dto.getUnitId());
            }
        }

        // 语法模块
        if (Objects.equals(flowName, FlowConstant.FLOW_SIX)) {
            int syntaxCount = syntaxUnitTopicNewMapper.countByUnitIdAndGroup(dto.getUnitId(), dto.getGroup());
            if (syntaxCount > 0) {
                return this.judgeHasCurrentModel(studyFlowNew, student, dto.getUnitId());
            }
        }

        // 学习完当前group
        return this.finishGroup(dto);
    }

    private FlowVO judgeHasCurrentModel(StudyFlowNew studyFlowNew, Student student, Long unitId) {
        StudyFlowNew studyFlowNew1 = studyFlowNewMapper.selectById(studyFlowNew.getId());
        return flowCommonMethod.packageFlowVO(studyFlowNew1, student, unitId);
    }

    /**
     * 判断当前单元group有没有语法模块
     *
     * @param studyFlowNew
     * @param dto
     * @return
     */
    private boolean judgeHasSyntaxModel(StudyFlowNew studyFlowNew, NodeDto dto) {
        // 没有句型模块判断是否有语法模块
        int syntaxCount = syntaxUnitTopicNewMapper.countByUnitIdAndGroup(dto.getUnitId(), dto.getGroup());
        if (syntaxCount > 0) {
            // 初始化语法游戏节点
            studyFlowNew.setId(120L);
            return true;
        }
        return false;
    }

    /**
     * 学习完当前group
     *
     * @param dto
     * @return
     */
    private FlowVO finishGroup(NodeDto dto) {

        // 判断哪些模块有当前group
        this.judgeHasCurrentGroup(dto);

        // 判断单元是否有下个group
        LearnNew learnNew = this.judgeHasNextGroup(dto);

        // 删除当前学习记录
        this.deleteLearnInfo(dto);

        if (learnNew == null) {
            // 说明当前单元学习完毕
            return this.finishUnit(dto);
        }

        studentFlowNewMapper.deleteByLearnId(dto.getLearnNew().getId());

        Long nodeId = dto.getEasyOrHard() == 1 ? FlowConstant.EASY_START : FlowConstant.HARD_START;
        this.initStudentFlow(dto.getStudent().getId(), nodeId, learnNew.getId());
        StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(nodeId);

        return this.packageFlowVO(studyFlowNew, dto.getStudent(), dto.getUnitId());
    }

    private void deleteLearnInfo(NodeDto dto) {
        LearnNew learnNew = dto.getLearnNew();
        if (learnNew != null) {
            learnExtendMapper.deleteByLearnId(learnNew.getId());
            learnNewMapper.deleteById(learnNew.getId());
        }
    }

    /**
     * 判断是否有下个group，如果有，将下个group初始化到正在学习表中，如果没有，返回null，再判断单元信息
     *
     * @param dto
     * @return
     */
    private LearnNew judgeHasNextGroup(NodeDto dto) {
        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHard(dto.getStudent().getId(),
                dto.getUnitId(), dto.getEasyOrHard());
        if (learnNew != null) {
            Long learnNewId = learnNew.getId();
            learnNewMapper.deleteById(learnNewId);
        }
        // 判断单词模块是否有下个group
        Integer group = unitVocabularyNewMapper.selectNextGroup(dto.getUnitId(), dto.getGroup());
        if (group != null) {
            return this.saveLearnNew(dto, group);
        }

        // 判断句型模块是否有下个group
        group = unitSentenceNewMapper.selectNextGroup(dto.getUnitId(), dto.getGroup());
        if (group != null) {
            return this.saveLearnNew(dto, group);
        }

        // 判断课文模块是否有下个group
        group = unitTeksNewMapper.selectNextGroup(dto.getUnitId(), dto.getGroup());
        if (group != null) {
            return this.saveLearnNew(dto, group);
        }

        group = syntaxUnitTopicNewMapper.selectNextGroup(dto.getUnitId(), dto.getGroup());
        if (group != null) {
            return this.saveLearnNew(dto, group);
        }

        return null;
    }

    private LearnNew saveLearnNew(NodeDto dto, Integer group) {
        LearnNew learnNew = LearnNew.builder()
                .easyOrHard(dto.getEasyOrHard())
                .group(group)
                .studentId(dto.getStudent().getId())
                .unitId(dto.getUnitId())
                .updateTime(new Date())
                .courseId(dto.getCourseId())
                .build();
        learnNewMapper.insert(learnNew);
        return learnNew;
    }

    /**
     * 判断哪些模块有当前group，并更新或新增当前group的学习已完成表
     *
     * @param dto
     */
    private void judgeHasCurrentGroup(NodeDto dto) {
        Long unitId = dto.getUnitId();
        Integer group = dto.getGroup();
        Integer count = unitVocabularyNewMapper.countUnitIdAndGroup(unitId, group);
        this.saveOrUpdateLearnHistory(dto, count, 1);

        count = unitSentenceNewMapper.countByUnitIdAndGroup(unitId, group);
        this.saveOrUpdateLearnHistory(dto, count, 2);

        count = syntaxUnitTopicNewMapper.countByUnitIdAndGroup(unitId, group);
        this.saveOrUpdateLearnHistory(dto, count, 3);

        count = unitTeksNewMapper.countByUnitIdAndGroup(unitId, group);
        this.saveOrUpdateLearnHistory(dto, count, 4);
    }

    private void saveOrUpdateLearnHistory(NodeDto dto, Integer count, int type) {
        if (count > 0) {
            // 查询已完成表中是否已有当前group信息，有更新，没有新增
            LearnHistory.LearnHistoryBuilder builder = LearnHistory.builder()
                    .studentId(dto.getStudent().getId())
                    .courseId(dto.getCourseId())
                    .unitId(dto.getUnitId())
                    .group(dto.getGroup())
                    .easyOrHard(dto.getEasyOrHard())
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

        // 更新优先级表中的变化优先级
        StudentStudyPlanNew oldMaxFinalLevel = this.getMaxFinalLevelStudentStudyPlanNew(dto);
        this.updateLevel(dto, oldMaxFinalLevel);

        Student student = dto.getStudent();
        Long studentId = student.getId();

        flowCommonMethod.saveOpenUnitLog(student, dto.getUnitId());

        // 根据优先级初始化学习表数据
        StudentStudyPlanNew maxStudentStudyPlanNew = studentStudyPlanNewMapper.selectMaxFinalByStudentId(studentId);
        LearnNew learnNew = this.saveLearn(maxStudentStudyPlanNew);

        // 将当前单元的已学习记录状态置为已完成
        learnHistoryMapper.updateStateByStudentIdAndUnitId(studentId, dto.getUnitId(), 2);

        studentFlowNewMapper.deleteByLearnId(dto.getLearnNew().getId());
        Long flowId = maxStudentStudyPlanNew.getFlowId();
        this.initStudentFlow(dto.getStudent().getId(), flowId, learnNew.getId());
        StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(flowId);

        return this.packageFlowVO(studyFlowNew, student, maxStudentStudyPlanNew.getUnitId());
    }

    /**
     * 获取最终优先级最高的数据
     *
     * @param dto
     * @return
     */
    private StudentStudyPlanNew getMaxFinalLevelStudentStudyPlanNew(NodeDto dto) {
        List<StudentStudyPlanNew> studentStudyPlanNews = studentStudyPlanNewMapper.selectMaxFinalLevelByLimit(dto.getStudent().getId(), 5);
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
        return maxStudentStudyPlanNew;
    }

    /**
     * 单元学习完成，更新优先级表
     *
     * @param dto
     * @param maxFinalLevel
     */
    private void updateLevel(NodeDto dto, StudentStudyPlanNew maxFinalLevel) {
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
    private LearnNew saveLearn(StudentStudyPlanNew studentStudyPlanNew) {
        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHard(studentStudyPlanNew.getStudentId(), studentStudyPlanNew.getUnitId(),
                studentStudyPlanNew.getEasyOrHard());
        if (learnNew == null) {
            learnNew = LearnNew.builder()
                    .courseId(studentStudyPlanNew.getCourseId())
                    .easyOrHard(studentStudyPlanNew.getEasyOrHard())
                    .group(1)
                    .studentId(studentStudyPlanNew.getStudentId())
                    .unitId(studentStudyPlanNew.getUnitId())
                    .updateTime(new Date())
                    .build();
            learnNewMapper.insert(learnNew);
        }
        return learnNew;
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
            studyFlowNew.setId(FlowConstant.TEKS_LISTEN);
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

        boolean canStudyWordPicture = flowCommonMethod.judgeWordPicture(dto, studyFlowNew);
        if (canStudyWordPicture) {
            return studyFlowNew;
        }

        // 流程 1 单词图鉴流程 id
        int flowOnePicture = 73;
        // 流程 1 的单词图鉴
        if (nextFlowId == flowOnePicture) {
            UnitNew unitNew = unitNewMapper.selectById(dto.getUnitId());
            Student student = dto.getStudent() == null ? new Student() : dto.getStudent();
            if (dto.getGrade() != null && dto.getGrade() >= dto.getStudyFlowNew().getType()) {
                // 去句型翻译
                int flowId = 85;
                flowCommonMethod.changeFlowNodeLog(student, "句型翻译", unitNew, flowId);
                return studyFlowNewMapper.selectById(flowId);
            }
            // 如果是从单词播放机直接进入单词图鉴，将流程跳转到慧记忆
            if (Objects.equals(dto.getNodeId(), 78L)) {
                int flowId = 71;
                flowCommonMethod.changeFlowNodeLog(student, "慧记忆", unitNew, flowId);
                return studyFlowNewMapper.selectById(flowId);
            }
            // 返回流程 1
            int flowId = 70;
            flowCommonMethod.changeFlowNodeLog(student, "单词播放机", unitNew, flowId);
            return studyFlowNewMapper.selectById(flowId);
        }

        return studyFlowNew;
    }
}
