package com.zhidejiaoyu.student.business.flow.common;

import com.zhidejiaoyu.common.constant.session.SessionConstant;
import com.zhidejiaoyu.common.dto.NodeDto;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.study.PriorityUtil;
import com.zhidejiaoyu.common.vo.flow.FlowVO;
import com.zhidejiaoyu.student.business.flow.FlowConstant;
import com.zhidejiaoyu.student.business.flow.service.impl.FreeFlowServiceImpl;
import com.zhidejiaoyu.student.business.flow.service.impl.StudyFlowServiceImpl;
import com.zhidejiaoyu.student.common.redis.PayLogRedisOpt;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 完成一个group或者单元
 *
 * @author: wuchenxi
 * @date: 2020/1/3 17:51:51
 */
@Component
public class FinishGroupOrUnit {

    @Resource
    private StudentFlowNewMapper studentFlowNewMapper;

    @Resource
    private StudyFlowNewMapper studyFlowNewMapper;

    @Resource
    private PackageFlowVO packageFlowVO;

    @Resource
    private InitData initData;

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
    private LogOpt logOpt;

    @Resource
    private StudentStudyPlanNewMapper studentStudyPlanNewMapper;

    @Resource
    private LearnHistoryMapper learnHistoryMapper;

    @Resource
    private CourseNewMapper courseNewMapper;

    @Resource
    private PayLogRedisOpt payLogRedisOpt;

    @Resource
    private UnitNewMapper unitNewMapper;

    @Resource
    private SyntaxUnitMapper syntaxUnitMapper;

    @Resource
    private RedisOpt redisOpt;

    /**
     * 一键学习，学习完当前group
     *
     * @param dto
     * @return
     */
    public FlowVO finishOneKeyGroup(NodeDto dto) {

        // 清除单词首次错误记忆强度增加50%标识
        redisOpt.clearFirstFalseAdd(dto.getStudent().getId(), dto.getUnitId(), dto.getGroup());
        HttpUtil.getHttpSession().removeAttribute(SessionConstant.FIRST_FALSE_ADD);

        // 判断哪些模块有当前group
        this.judgeHasCurrentGroup(dto);

        // 判断单元是否有下个group
        LearnNew learnNew = this.judgeHasNextGroup(dto);

        // 删除当前学习记录
        this.deleteLearnInfo(dto);
        studentFlowNewMapper.deleteByLearnId(dto.getLearnNew().getId());

        boolean isEasy = dto.getEasyOrHard() == 1;
        if (learnNew == null) {
            // 说明当前单元学习完毕
            // 学习语法模块
            FlowVO flowVO = this.toSyntaxFlow(dto, isEasy);
            if (flowVO != null) {
                return flowVO;
            }
            // 语法模块学习完，学习下一优先级内容
            return this.finishOneKeyUnit(dto);
        }

        long nodeId = this.getNodeId(learnNew, isEasy);
        initData.initStudentFlow(NodeDto.builder()
                .student(dto.getStudent())
                .nodeId(nodeId)
                .learnNew(learnNew)
                .build());
        StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(nodeId);
        StudyFlowServiceImpl.setOneKeyGroup(learnNew);

        return packageFlowVO.packageFlowVO(studyFlowNew, dto.getStudent(), dto.getUnitId());
    }

    /**
     * 获取下个group的开始节点id
     *
     * @param learnNew
     * @param isEasy   true：简单流程；false：复杂流程
     * @return
     */
    public long getNodeId(LearnNew learnNew, boolean isEasy) {
        if (Objects.equals(learnNew.getModelType(), 1)) {
            // 单词模块节点
            return isEasy ? FlowConstant.BEFORE_GROUP_GAME_EASY : FlowConstant.BEFORE_GROUP_GAME_HARD;
        }
        if (Objects.equals(learnNew.getModelType(), 2)) {
            // 句型模块节点
            return isEasy ? FlowConstant.SENTENCE_GAME : FlowConstant.SENTENCE_WRITE;
        }
        // 课文模块节点
        return isEasy ? FlowConstant.TEKS_LISTEN : FlowConstant.TEKS_TRAINING;
    }

    private FlowVO toSyntaxFlow(NodeDto dto, boolean isEasy) {
        if (dto.getLastUnit()) {
            // 说明当前课程的单元已学习到最后一个单元，获取当前课程下个语法单元节点
            SyntaxUnit syntaxUnit = syntaxUnitMapper.selectNextUnitByCourseId(dto.getUnitId(), dto.getCourseId());
            if (syntaxUnit != null) {
                StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(isEasy ? FlowConstant.SYNTAX_GAME : FlowConstant.SYNTAX_WRITE);
                return this.getSyntaxFlowVo(NodeDto.builder()
                        .student(dto.getStudent())
                        .easyOrHard(isEasy ? 1 : 2)
                        .lastUnit(true)
                        .studyFlowNew(studyFlowNew)
                        .build(), syntaxUnit);
            }
        } else {

            // 将当前单元的已学习记录状态置为已完成
            learnHistoryMapper.updateStateByStudentIdAndUnitId(dto.getStudent().getId(), dto.getUnitId(), 2);

            // 获取当前单词对应的语法课程
            SyntaxUnit syntaxUnit = this.getSyntaxUnit(dto);
            if (syntaxUnit == null) {
                return null;
            }

            UnitNew unitNew = unitNewMapper.selectMaxUnitByCourseId(dto.getCourseId());

            boolean isLastUnit = Objects.equals(dto.getUnitId(), unitNew.getId());

            StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(isEasy ? FlowConstant.SYNTAX_GAME : FlowConstant.SYNTAX_WRITE);

            return this.getSyntaxFlowVo(NodeDto.builder()
                    .student(dto.getStudent())
                    .easyOrHard(isEasy ? 1 : 2)
                    .lastUnit(isLastUnit)
                    .studyFlowNew(studyFlowNew)
                    .build(), syntaxUnit);
        }
        return null;
    }

    private FlowVO getSyntaxFlowVo(NodeDto dto, SyntaxUnit syntaxUnit) {
        Student student = dto.getStudent();
        Long syntaxUnitId = syntaxUnit.getId();
        Long syntaxCourseId = syntaxUnit.getCourseId();
        StudyFlowNew studyFlowNew = dto.getStudyFlowNew();

        LearnNew learnNew = initData.saveLearnNew(NodeDto.builder()
                .student(student)
                .easyOrHard(dto.getEasyOrHard())
                .unitId(syntaxUnitId)
                .courseId(syntaxCourseId)
                .build(), 1, 4);

        initData.initStudentFlow(NodeDto.builder()
                .nodeId(studyFlowNew.getId())
                .learnNew(learnNew)
                .modelType(1)
                .build());

        return packageFlowVO.packageSyntaxFlowVO(NodeDto.builder()
                .studyFlowNew(studyFlowNew)
                .student(student)
                .unitId(syntaxUnitId)
                .lastUnit(dto.getLastUnit())
                .build());
    }

    private SyntaxUnit getSyntaxUnit(NodeDto dto) {
        CourseNew courseNew = courseNewMapper.selectById(dto.getCourseId());
        if (courseNew == null) {
            return null;
        }

        UnitNew unitNew = unitNewMapper.selectById(dto.getUnitId());
        String grade = courseNew.getGrade();
        String label = courseNew.getLabel();
        String jointNameLike = "(" +
                (StringUtils.isEmpty(grade) ? courseNew.getGradeExt() : grade) +
                "-" +
                (StringUtils.isEmpty(label) ? courseNew.getLabelExt() : label) +
                ")-" +
                unitNew.getUnitName();
        return syntaxUnitMapper.selectIdLikeJointName(jointNameLike);
    }

    /**
     * 自由学习，完成group
     *
     * @param dto
     * @return
     */
    public ServerResponse<Object> finishFreeGroup(NodeDto dto) {

        // 清除单词首次错误记忆强度增加50%标识
        redisOpt.clearFirstFalseAdd(dto.getStudent().getId(), dto.getUnitId(), dto.getGroup());
        HttpUtil.getHttpSession().removeAttribute(SessionConstant.FIRST_FALSE_ADD);

        // 更新学习历史表
        initData.saveOrUpdateFreeLearnHistory(dto);

        // 判断单元是否有下个group
        LearnNew learnNew = this.judgeHasNextFreeGroup(dto);

        // 删除当前学习记录
        Long learnNewId = dto.getLearnNew().getId();
        learnExtendMapper.deleteByLearnId(learnNewId);
        learnNewMapper.deleteById(learnNewId);
        studentFlowNewMapper.deleteByLearnId(learnNewId);

        Student student = dto.getStudent();

        if (learnNew == null) {

            // 将当前单元的已学习记录状态置为已完成
            learnHistoryMapper.updateStateByStudentIdAndUnitId(student.getId(), dto.getUnitId(), 2);

            boolean isPaid = payLogRedisOpt.isPaid(student.getId());
            if (!isPaid) {
                // 学生没有充值，不修改优先级
                return ServerResponse.createBySuccess(ResponseCode.UNIT_FINISH);
            }
            // 说明当前单元学习完毕
            return finishFreeUnit(dto, student);
        }

        Long startFlowId = this.getStartFlowId(dto.getEasyOrHard(), dto.getModelType());
        initData.initStudentFlow(NodeDto.builder()
                .student(student)
                .nodeId(startFlowId)
                .learnNew(learnNew)
                .build());
        StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(startFlowId);

        FreeFlowServiceImpl.setFreeGroup(learnNew);

        FlowVO flowVO = packageFlowVO.packageFlowVO(studyFlowNew, student, dto.getUnitId());
        return ServerResponse.createBySuccess(flowVO);
    }

    /**
     * 自由学习单元学习完毕
     *
     * @param dto
     * @param student
     * @return
     */
    public ServerResponse<Object> finishFreeUnit(NodeDto dto, Student student) {
        if (Objects.equals(dto.getStudyFlowNew().getFlowName(), FlowConstant.FLOW_SIX)) {
            return ServerResponse.createBySuccess(ResponseCode.UNIT_FINISH);
        }
        StudentStudyPlanNew studentStudyPlanNew = studentStudyPlanNewMapper.selectByStudentIdAndUnitIdAndEasyOrHard(student.getId(), dto.getUnitId(), dto.getEasyOrHard());
        if (studentStudyPlanNew == null) {
            CourseNew courseNew = courseNewMapper.selectByUnitId(dto.getUnitId());
            // 说明当前课程还没有优先级，初始化当前课程的优先级
            String grade = StringUtils.isNotEmpty(courseNew.getGrade()) ? courseNew.getGrade() : courseNew.getGradeExt();
            String label = StringUtils.isNotEmpty(courseNew.getLabel()) ? courseNew.getLabel() : courseNew.getLabelExt();
            int basePriority = PriorityUtil.getBasePriority(student.getGrade(), grade, label, 0);
            int timePriority = PriorityUtil.BASE_TIME_PRIORITY;

            boolean isEasy = dto.getEasyOrHard() == 1;
            studentStudyPlanNewMapper.insert(StudentStudyPlanNew.builder()
                    .studentId(student.getId())
                    .complete(1)
                    .courseId(courseNew.getId())
                    .currentStudyCount(1)
                    .errorLevel(1)
                    .group(0)
                    .timeLevel(timePriority)
                    .totalStudyCount(1)
                    .updateTime(new Date())
                    .unitId(dto.getUnitId())
                    .finalLevel(isEasy ? basePriority + 1 + timePriority : basePriority - PriorityUtil.HARD_NUM + 1 + timePriority)
                    .baseLevel(isEasy ? basePriority : basePriority - PriorityUtil.HARD_NUM)
                    .easyOrHard(dto.getEasyOrHard())
                    .flowId(FlowConstant.FREE_BEFORE_GROUP_GAME)
                    .build());
        } else {
            // 已有当前课程的优先级，更新优先级
            this.updateLevel(dto, studentStudyPlanNew);
        }

        return ServerResponse.createBySuccess(ResponseCode.UNIT_FINISH);
    }

    /**
     * 获取当前模块起始节点
     *
     * @param easyOrHard
     * @param type
     * @return
     */
    public Long getStartFlowId(Integer easyOrHard, Integer type) {
        boolean isEasy = Objects.equals(easyOrHard, 1);
        switch (type) {
            case 2:
                return FlowConstant.FREE_BEFORE_GROUP_GAME;
            case 3:
                return isEasy ? FlowConstant.FREE_SENTENCE_GAME : FlowConstant.FREE_SENTENCE_WRITE;
            case 4:
                return isEasy ? FlowConstant.FREE_TEKS_LISTEN : FlowConstant.FREE_TEKS_TRAIN;
            case 5:
                return isEasy ? FlowConstant.FREE_SYNTAX_GAME : FlowConstant.FREE_SYNTAX_WRITE;
            case 6:
                return FlowConstant.GOLD_TEST;
            default:
                return null;
        }
    }

    private void deleteLearnInfo(NodeDto dto) {
        LearnNew learnNew = dto.getLearnNew();
        if (learnNew != null) {
            learnExtendMapper.deleteByLearnId(learnNew.getId());
            learnNewMapper.deleteById(learnNew.getId());
        }
    }

    /**
     * 自由学习判断当前单元是否有下个group
     *
     * @param dto
     * @return
     */
    private LearnNew judgeHasNextFreeGroup(NodeDto dto) {
        Long unitId = dto.getUnitId();
        Integer group = dto.getGroup();

        Integer newGroup = null;
        Integer modelType = dto.getModelType();
        switch (modelType) {
            case 2:
                newGroup = unitVocabularyNewMapper.selectNextGroup(unitId, group);
                break;
            case 3:
                newGroup = unitSentenceNewMapper.selectNextGroup(unitId, group);
                break;
            case 4:
                newGroup = unitTeksNewMapper.selectNextGroup(unitId, group);
                break;
            default:
        }

        if (newGroup != null) {
            return initData.saveLearnNew(dto, newGroup, modelType - 1);
        }
        return null;
    }

    /**
     * 判断是否有下个group，如果有，将下个group初始化到正在学习表中，如果没有，返回null，再判断单元信息
     *
     * @param dto
     * @return
     */
    private LearnNew judgeHasNextGroup(NodeDto dto) {
        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHardAndModelType(dto.getStudent().getId(),
                dto.getUnitId(), dto.getEasyOrHard(), FlowNameToLearnModelType.FLOW_NEW_TO_LEARN_MODEL_TYPE.get(dto.getStudyFlowNew().getFlowName()));
        if (learnNew != null) {
            Long learnNewId = learnNew.getId();
            learnNewMapper.deleteById(learnNewId);
        }
        // 判断单词模块是否有下个group
        Integer group = unitVocabularyNewMapper.selectNextGroup(dto.getUnitId(), dto.getGroup());
        if (group != null) {
            return initData.saveLearnNew(dto, group, 1);
        }

        // 判断句型模块是否有下个group
        group = unitSentenceNewMapper.selectNextGroup(dto.getUnitId(), dto.getGroup());
        if (group != null) {
            return initData.saveLearnNew(dto, group, 2);
        }

        // 判断课文模块是否有下个group
        group = unitTeksNewMapper.selectNextGroup(dto.getUnitId(), dto.getGroup());
        if (group != null) {
            return initData.saveLearnNew(dto, group, 3);
        }

        return null;
    }

    public FlowVO finishGoldTest(NodeDto dto) {
        NodeDto nodeDto = this.getNextFlowInfo(dto);
        return packageFlowVO.packageFlowVO(nodeDto.getStudyFlowNew(), nodeDto.getStudent(), nodeDto.getUnitId());
    }

    /**
     * 获取下一个优先级信息
     *
     * @param dto
     * @return
     */
    public NodeDto getNextFlowInfo(NodeDto dto) {
        // 更新优先级表中的变化优先级
        StudentStudyPlanNew oldMaxFinalLevel = this.getMaxFinalLevelStudentStudyPlanNew(dto);
        this.updateLevel(dto, oldMaxFinalLevel);

        Student student = dto.getStudent();
        Long studentId = student.getId();

        StudentStudyPlanNew maxStudentStudyPlanNew = studentStudyPlanNewMapper.selectMaxFinalByStudentId(studentId);

        Long flowId = maxStudentStudyPlanNew.getFlowId();
        StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(flowId);
        return NodeDto.builder()
                .student(dto.getStudent())
                .studyFlowNew(studyFlowNew)
                .unitId(maxStudentStudyPlanNew.getUnitId())
                .studentStudyPlanNew(maxStudentStudyPlanNew)
                .build();
    }

    /**
     * 一键学习单元完成操作
     *
     * @param dto
     * @return
     */
    public FlowVO finishOneKeyUnit(NodeDto dto) {
        NodeDto nodeDto = this.getNextFlowInfo(dto);

        // 根据优先级初始化学习表数据
        StudyFlowNew studyFlowNew = nodeDto.getStudyFlowNew();
        Integer modelType = FlowNameToLearnModelType.FLOW_NEW_TO_LEARN_MODEL_TYPE.get(studyFlowNew.getFlowName());
        LearnNew learnNew = initData.saveLearn(nodeDto.getStudentStudyPlanNew(), modelType);

        // 将当前单元的已学习记录状态置为已完成
        Student student = nodeDto.getStudent();
        learnHistoryMapper.updateStateByStudentIdAndUnitId(student.getId(), dto.getUnitId(), 2);

        initData.initStudentFlow(NodeDto.builder()
                .student(dto.getStudent())
                .nodeId(nodeDto.getStudentStudyPlanNew().getFlowId())
                .learnNew(learnNew)
                .build());

        return packageFlowVO.packageFlowVO(studyFlowNew, student, nodeDto.getUnitId());
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
     * 判断哪些模块有当前group，并更新或新增当前group的学习已完成表
     *
     * @param dto
     */
    private void judgeHasCurrentGroup(NodeDto dto) {
        if (Objects.equals(dto.getStudyFlowNew().getFlowName(), FlowConstant.FLOW_SIX)) {
            SyntaxUnit syntaxUnit = syntaxUnitMapper.selectById(dto.getUnitId());
            if (syntaxUnit != null) {
                initData.saveOrUpdateOneKeyLearnHistory(NodeDto.builder()
                        .student(dto.getStudent())
                        .unitId(syntaxUnit.getId())
                        .courseId(syntaxUnit.getCourseId())
                        .group(1)
                        .easyOrHard(dto.getEasyOrHard())
                        .build(), 1, 3);
            }
        } else {
            Long unitId = dto.getUnitId();
            Integer group = dto.getGroup();
            Integer count = unitVocabularyNewMapper.countUnitIdAndGroup(unitId, group);
            initData.saveOrUpdateOneKeyLearnHistory(dto, count, 1);

            count = unitSentenceNewMapper.countByUnitIdAndGroup(unitId, group);
            initData.saveOrUpdateOneKeyLearnHistory(dto, count, 2);

            count = unitTeksNewMapper.countByUnitIdAndGroup(unitId, group);
            initData.saveOrUpdateOneKeyLearnHistory(dto, count, 4);
        }

    }


}
