package com.zhidejiaoyu.student.business.flow.common;

import com.zhidejiaoyu.common.dto.NodeDto;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.study.PriorityUtil;
import com.zhidejiaoyu.common.vo.flow.FlowVO;
import com.zhidejiaoyu.student.business.flow.FlowConstant;
import com.zhidejiaoyu.student.common.redis.PayLogRedisOpt;
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

    /**
     * 一键学习，学习完当前group
     *
     * @param dto
     * @return
     */
    public FlowVO finishOneKeyGroup(NodeDto dto) {

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

        Long nodeId = isEasy ? FlowConstant.EASY_START : FlowConstant.HARD_START;
        initData.initStudentFlow(NodeDto.builder()
                .student(dto.getStudent())
                .nodeId(nodeId)
                .learnNew(learnNew)
                .build());
        StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(nodeId);

        return packageFlowVO.packageFlowVO(studyFlowNew, dto.getStudent(), dto.getUnitId());
    }

    private FlowVO toSyntaxFlow(NodeDto dto, boolean isEasy) {
        if (dto.getLastUnit()) {
            // 说明当前课程的单元已学习到最后一个单元，获取当前课程下个语法单元节点
            SyntaxUnit syntaxUnit = syntaxUnitMapper.selectNextUnitByCourseId(dto.getUnitId(), dto.getCourseId());
            StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(isEasy ? FlowConstant.SYNTAX_GAME : FlowConstant.SYNTAX_WRITE);
            if (syntaxUnit != null) {
                initData.saveLearnNew(dto, 1, 4);
                return packageFlowVO.packageSyntaxFlowVO(NodeDto.builder()
                        .studyFlowNew(studyFlowNew)
                        .student(dto.getStudent())
                        .unitId(syntaxUnit.getId())
                        .lastUnit(true)
                        .build());
            }
        } else {

            // 将当前单元的已学习记录状态置为已完成
            learnHistoryMapper.updateStateByStudentIdAndUnitId(dto.getStudent().getId(), dto.getUnitId(), 2);

            // 获取当前单词对应的语法课程
            SyntaxUnit syntaxUnit = this.getSyntaxUnit(dto);

            UnitNew unitNew = unitNewMapper.selectMaxUnitByCourseId(dto.getCourseId());

            boolean isLastUnit = false;
            if (Objects.equals(dto.getUnitId(), unitNew.getId())) {
                // 说明是课程的最后一个单元
                isLastUnit = true;
            }

            StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(isEasy ? FlowConstant.SYNTAX_GAME : FlowConstant.SYNTAX_WRITE);

            if (syntaxUnit != null) {
                initData.saveLearnNew(dto, 1, 4);
                return packageFlowVO.packageSyntaxFlowVO(NodeDto.builder()
                        .studyFlowNew(studyFlowNew)
                        .student(dto.getStudent())
                        .unitId(syntaxUnit.getId())
                        .lastUnit(isLastUnit)
                        .build());
            }
        }
        return null;
    }

    private SyntaxUnit getSyntaxUnit(NodeDto dto) {
        CourseNew courseNew = courseNewMapper.selectById(dto.getCourseId());
        UnitNew unitNew = unitNewMapper.selectById(dto.getUnitId());
        String grade = courseNew.getGrade();
        String label = courseNew.getLabel();
        String jointNameLike = "(" +
                (StringUtils.isEmpty(grade) ? courseNew.getGradeExt() : grade) +
                "-" +
                (StringUtils.isEmpty(label) ? courseNew.getLabelExt() : label) +
                "-" +
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
            int basePriority = PriorityUtil.getBasePriority(student.getGrade(), courseNew.getGrade(), 0);
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
                    .flowId(isEasy ? FlowConstant.EASY_START : FlowConstant.HARD_START)
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
                return isEasy ? FlowConstant.FREE_PLAYER : FlowConstant.FREE_LETTER_WRITE;
            case 3:
                return isEasy ? FlowConstant.FREE_SENTENCE_TRANSLATE : FlowConstant.FREE_SENTENCE_WRITE;
            case 4:
                return isEasy ? FlowConstant.FREE_TEKS_LISTEN : FlowConstant.FREE_TEKS_TRAIN;
            case 5:
                return isEasy ? FlowConstant.FREE_SYNTAX_GAME : FlowConstant.FREE_SYNTAX_WRITE;
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

    /**
     * 一键学习单元完成操作
     *
     * @param dto
     * @return
     */
    private FlowVO finishOneKeyUnit(NodeDto dto) {

        // 更新优先级表中的变化优先级
        StudentStudyPlanNew oldMaxFinalLevel = this.getMaxFinalLevelStudentStudyPlanNew(dto);
        this.updateLevel(dto, oldMaxFinalLevel);

        Student student = dto.getStudent();
        Long studentId = student.getId();

        logOpt.saveOpenUnitLog(student, dto.getUnitId());

        // 根据优先级初始化学习表数据
        StudentStudyPlanNew maxStudentStudyPlanNew = studentStudyPlanNewMapper.selectMaxFinalByStudentId(studentId);
        Long flowId = maxStudentStudyPlanNew.getFlowId();
        StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(flowId);

        Integer modelType = FlowNameToLearnModelType.FLOW_NEW_TO_LEARN_MODEL_TYPE.get(studyFlowNew.getFlowName());
        LearnNew learnNew = initData.saveLearn(maxStudentStudyPlanNew, modelType);

        // 将当前单元的已学习记录状态置为已完成
        learnHistoryMapper.updateStateByStudentIdAndUnitId(studentId, dto.getUnitId(), 2);

        initData.initStudentFlow(NodeDto.builder()
                .student(dto.getStudent())
                .nodeId(flowId)
                .learnNew(learnNew)
                .build());

        return packageFlowVO.packageFlowVO(studyFlowNew, student, maxStudentStudyPlanNew.getUnitId());
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
