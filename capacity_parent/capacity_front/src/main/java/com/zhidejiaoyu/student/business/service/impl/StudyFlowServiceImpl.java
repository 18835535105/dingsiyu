package com.zhidejiaoyu.student.business.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.zhidejiaoyu.common.constant.study.PointConstant;
import com.zhidejiaoyu.common.dto.NodeDto;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.study.PriorityUtil;
import com.zhidejiaoyu.student.business.service.StudyFlowService;
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

@Service
@Slf4j
public class StudyFlowServiceImpl extends BaseServiceImpl<StudyFlowMapper, StudyFlow> implements StudyFlowService {

    @Resource
    private StudyFlowNewMapper studyFlowNewMapper;

    @Resource
    private StudentFlowNewMapper studentFlowNewMapper;

    @Resource
    private CourseNewMapper courseNewMapper;

    @Resource
    private UnitNewMapper unitNewMapper;

    @Resource
    private VocabularyMapper vocabularyMapper;

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

    /**
     * 节点学完, 把下一节初始化到student_flow表, 并把下一节点返回
     *
     * @return id 节点id
     * model_name 节点模块名
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> getNode(NodeDto dto, String isTrueFlow, HttpSession session) {
        Student student = getStudent(session);
        StudyFlowNew studyFlowNew = studyFlowNewMapper.selectById(dto.getNodeId());

        if (studyFlowNew == null) {
            throw new ServiceException("未查询到流程信息！");
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
        return judgeNextNode(dto);
    }

    private ServerResponse<Object> judgeNextNode(NodeDto dto) {
        String isTrueFlow = dto.getTrueFlow();
        StudyFlowNew studyFlowNew = dto.getStudyFlowNew();
        // 学生选择项，是否进行下一个节点， true：进行下一个节点；否则跳过下个节点
        if (isTrueFlow != null) {
            if (Objects.equals("true", isTrueFlow)) {
                return toAnotherFlow(dto, studyFlowNew.getNextTrueFlow());
            } else {
                return toAnotherFlow(dto, studyFlowNew.getNextFalseFlow());
            }
        }

        // 判断学生是否在当前分数段
        if (studyFlowNew.getType() != null) {
            StudyFlowNew falseFlow = studyFlowNewMapper.selectById(studyFlowNew.getNextFalseFlow());
            StudyFlowNew trueFlow = studyFlowNewMapper.selectById(studyFlowNew.getNextTrueFlow());

            // 游戏前测节点 id
            int gameTestFlowId = 3;
            if (Objects.equals(gameTestFlowId, falseFlow.getType())) {
                ServerResponse<Object> x = toNextNode(dto, falseFlow);
                if (x != null) {
                    return x;
                }
            } else if (Objects.equals(gameTestFlowId, trueFlow.getType())) {
                ServerResponse<Object> x = toNextNode(dto, trueFlow);
                if (x != null) {
                    return x;
                }
            }

            Long grade = dto.getGrade();
            if (grade >= studyFlowNew.getType()) {
                return toAnotherFlow(dto, studyFlowNew.getNextTrueFlow());
            } else if (grade < studyFlowNew.getType()) {
                return toAnotherFlow(dto, studyFlowNew.getNextFalseFlow());
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
    private ServerResponse<Object> toNextNode(NodeDto dto, StudyFlowNew flow) {
        Long grade = dto.getGrade();
        if (grade != null) {
            // 50 <= 分数 < 90分走 nextTrue 流程，分数 < 50 走 nextFalse 流程
            if (grade >= PointConstant.FIFTY && grade < PointConstant.NINETY) {
                return toAnotherFlow(dto, flow.getNextTrueFlow());
            } else if (grade < PointConstant.NINETY) {
                return toAnotherFlow(dto, flow.getNextFalseFlow());
            }
        }
        return null;
    }

    /**
     * 进入其他流程
     *
     * @param dto
     * @param nextFlowId 下个节点id
     * @return
     */
    private ServerResponse<Object> toAnotherFlow(NodeDto dto, int nextFlowId) {
        Student student = dto.getStudent();

        // 判断当前单元单词是否有图片，如果都没有图片不进入单词图鉴
        StudyFlowNew studyFlowNew = this.getStudyFlow(dto, nextFlowId);
        UnitNew unitNew = unitNewMapper.selectById(dto.getUnitId());
        CourseNew courseNew = courseNewMapper.selectById(dto.getCourseId());

        studyFlowNew.setCourseId(dto.getCourseId());
        studyFlowNew.setUnitId(dto.getUnitId());
        studyFlowNew.setCourseName(courseNew.getCourseName());
        studyFlowNew.setUnitName(unitNew.getUnitName());

        // 判断当前单元是否含有当前模块的内容，如果没有当前模块的内容学习下个模块的内容
        studyFlowNew = this.judgeHasCurrentModel(studyFlowNew, dto);

        studentFlowNewMapper.update(StudentFlowNew.builder()
                .currentFlowId(studyFlowNew.getId())
                .updateTime(new Date())
                .build(), new EntityWrapper<StudentFlowNew>().eq("student_id", student.getId())
                .eq("unit_id", dto.getUnitId()));


        return ServerResponse.createBySuccess("true", studyFlowNew);
    }

    /**
     * 验证当前单元中的group是否包含当前学习模块，如果不包含，学习下个学习模块
     *
     * @param studyFlowNew
     * @param dto
     * @return
     */
    private StudyFlowNew judgeHasCurrentModel(StudyFlowNew studyFlowNew, NodeDto dto) {
        String flowName = studyFlowNew.getFlowName();
        // 单词模块
        if (Objects.equals(flowName, "流程1") || Objects.equals(flowName, "流程2")) {
            Integer wordCount = unitVocabularyNewMapper.selectCount(new EntityWrapper<UnitVocabularyNew>()
                    .eq("unit_id", dto.getUnitId())
                    .eq("group", dto.getGroup()));
            if (wordCount > 0) {
                return studyFlowNew;
            }

            if (this.judgeHasSentenceModel(studyFlowNew, dto)) {
                return studyFlowNew;
            }

            if (this.judgeHasTeksModel(studyFlowNew, dto)) {
                return studyFlowNew;
            }

        }

        // 句型模块
        if (Objects.equals(flowName, "流程3") || Objects.equals(flowName, "流程4")) {
            Integer sentenceCount = unitSentenceNewMapper.selectCount(new EntityWrapper<UnitSentenceNew>()
                    .eq("unit_id", dto.getUnitId())
                    .eq("group", dto.getGroup()));
            if (sentenceCount > 0) {
                return studyFlowNew;
            }

            // 没有句型模块判断是否有课文模块
            if (this.judgeHasTeksModel(studyFlowNew, dto)) {
                return studyFlowNew;
            }
        }

        // 课文模块
        if (Objects.equals(flowName, "流程5")) {
            Integer teksCount = unitTeksNewMapper.selectCount(new EntityWrapper<UnitTeksNew>()
                    .eq("unit_id", dto.getUnitId())
                    .eq("group", dto.getGroup()));
            if (teksCount > 0) {
                return studyFlowNew;
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
    private StudyFlowNew finishGroup(NodeDto dto) {
        StudentStudyPlanNew studentStudyPlanNew = studentStudyPlanNewMapper.selectMaxFinalLevelByLimit(dto.getStudent().getId(), 1).get(0);
        LearnNew learnNew = learnNewMapper.selectByStudentStudyPlanNew(studentStudyPlanNew);

        // 判断哪些模块有当前group
        this.judgeHasCurrentGroup(dto, studentStudyPlanNew);

        // 判断单元是否有下个group
        Integer group = unitVocabularyNewMapper.selectNextGroup(dto.getUnitId(), dto.getGroup());
        if (group != null) {
            dto.setGroup(group);
            learnNew.setGroup(group);
            learnNewMapper.updateById(learnNew);
        } else {
            group = unitSentenceNewMapper.selectNextGroup(dto.getUnitId(), dto.getGroup());
            if (group != null) {
                dto.setGroup(group);
                learnNew.setGroup(group);
                learnNewMapper.updateById(learnNew);
            } else {
                group = unitSentenceNewMapper.selectNextGroup(dto.getUnitId(), dto.getGroup());
                if (group != null) {
                    dto.setGroup(group);
                    learnNew.setGroup(group);
                    learnNewMapper.updateById(learnNew);
                }
            }
        }

        if (group == null) {
            // 说明当前单元学习完毕
            return this.finishUnit(dto);
        }


        Long courseId = dto.getCourseId();
        Long unitId = dto.getUnitId();

        CourseNew courseNew = courseNewMapper.selectById(courseId);
        UnitNew unitNew = unitNewMapper.selectById(unitId);

        return StudyFlowNew.builder()
                .id(dto.getNodeId())
                .courseId(courseId)
                .unitId(unitId)
                .courseName(courseNew.getCourseName())
                .unitName(unitNew.getUnitName())
                .build();
    }

    /**
     * 判断哪些模块有当前group，并更新或新增当前group的学习已完成表
     *
     * @param dto
     * @param studentStudyPlanNew
     */
    private void judgeHasCurrentGroup(NodeDto dto, StudentStudyPlanNew studentStudyPlanNew) {
        Integer count = unitVocabularyNewMapper.selectCount(new EntityWrapper<UnitVocabularyNew>()
                .eq("unit_id", dto.getUnitId())
                .eq("group", dto.getGroup()));
        this.saveOrUpdateLearnHistory(dto, studentStudyPlanNew, count, 1);

        count = unitSentenceNewMapper.selectCount(new EntityWrapper<UnitSentenceNew>()
                .eq("unit_id", dto.getUnitId())
                .eq("group", dto.getGroup()));
        this.saveOrUpdateLearnHistory(dto, studentStudyPlanNew, count, 2);

        count = unitTeksNewMapper.selectCount(new EntityWrapper<UnitTeksNew>()
                .eq("unit_id", dto.getUnitId())
                .eq("group", dto.getGroup()));
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
    private StudyFlowNew finishUnit(NodeDto dto) {
        Long studentId = dto.getStudent().getId();

        // 更新优先级表中的变化优先级
        this.updateLevel(dto, studentId);

        StudentStudyPlanNew maxStudentStudyPlanNew = this.initLearnInfo(dto, studentId);

        // 将当前单元的已学习记录状态置为已完成
        this.updateLearnHistory(dto);

        Long courseId = maxStudentStudyPlanNew.getCourseId();
        Long unitId = maxStudentStudyPlanNew.getUnitId();

        CourseNew courseNew = courseNewMapper.selectById(courseId);
        UnitNew unitNew = unitNewMapper.selectById(unitId);

        return StudyFlowNew.builder()
                .id(maxStudentStudyPlanNew.getFlowId())
                .courseId(courseId)
                .unitId(unitId)
                .courseName(courseNew.getCourseName())
                .unitName(unitNew.getUnitName())
                .build();
    }

    private void updateLearnHistory(NodeDto dto) {
        learnHistoryMapper.update(LearnHistory.builder().state(2).updateTime(new Date()).build(),
                new EntityWrapper<LearnHistory>().eq("student_id", dto.getStudent().getId())
                        .eq("unit_id", dto.getUnitId()));
    }

    /**
     * 根据优先级初始化学习表数据
     *
     * @param dto
     * @param studentId
     * @return
     */
    private StudentStudyPlanNew initLearnInfo(NodeDto dto, Long studentId) {
        // 从正在学习表中删除当前单元信息
        List<LearnNew> learnNews = learnNewMapper.selectList(new EntityWrapper<LearnNew>().eq("student_id", studentId)
                .eq("unit_id", dto.getUnitId()));
        learnExtendMapper.delete(new EntityWrapper<LearnExtend>().in("learn_id", learnNews.stream().map(LearnNew::getId).collect(Collectors.toList())));
        learnNewMapper.deleteBatchIds(learnNews);

        // 初始化下个优先级的单元数据
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
        Integer sentenceCount = unitSentenceNewMapper.selectCount(new EntityWrapper<UnitSentenceNew>()
                .eq("unit_id", dto.getUnitId())
                .eq("group", dto.getGroup()));
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
        Integer teksCount = unitTeksNewMapper.selectCount(new EntityWrapper<UnitTeksNew>()
                .eq("unit_id", dto.getUnitId())
                .eq("group", dto.getGroup()));
        if (teksCount > 0) {
            studyFlowNew.setId(89L);
            return true;
        }
        return false;
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
        int pictureCount = vocabularyMapper.countPicture(unitId);
        if (pictureCount > 0) {
            return byPrimaryKey;
        }

        UnitNew unitNew = unitNewMapper.selectById(unitId);
        Student student = dto.getStudent() == null ? new Student() : dto.getStudent();

        // 需要跳转到的流程 id
        int flowId1;
        // 流程 1 单词图鉴流程 id
        int flowOnePicture = 73;
        // 流程 1 的单词图鉴
        if (flowId == flowOnePicture) {
            if (dto.getGrade() != null && dto.getGrade() >= dto.getStudyFlowNew().getType()) {
                // 去流程 2 的慧听写
                flowId1 = 79;
                this.changeFlowNodeLog(student, "慧听写", unitNew, flowId1);
                return studyFlowNewMapper.selectById(flowId1);
            }
            // 如果是从单词播放机直接进入单词图鉴，将流程跳转到慧记忆
            if (Objects.equals(dto.getNodeId(), 78)) {
                flowId1 = 71;
                this.changeFlowNodeLog(student, "慧记忆", unitNew, flowId1);
                return studyFlowNewMapper.selectById(flowId1);
            }
            // 返回流程 1
            flowId1 = 70;
            this.changeFlowNodeLog(student, "单词播放机", unitNew, flowId1);
            return studyFlowNewMapper.selectById(flowId1);
        }

        // 流程 2 慧听写的单词图鉴
        int flowTwoListenPicture = 93;
        if (flowId == flowTwoListenPicture) {
            if (dto.getGrade() != null && dto.getGrade() >= dto.getStudyFlowNew().getType()) {
                // 去流程 2 的慧默写
                flowId1 = 82;
                this.changeFlowNodeLog(student, "慧默写", unitNew, flowId1);
                return studyFlowNewMapper.selectById(flowId1);
            }
            // 返回流程 1
            flowId1 = 70;
            this.changeFlowNodeLog(student, "单词播放机", unitNew, flowId1);
            return studyFlowNewMapper.selectById(9);
        }

        // 流程 2 慧默写的单词图鉴
        int flowTwoWritePicture = 97;
        if (flowId == flowTwoWritePicture) {
            if (dto.getGrade() != null && dto.getGrade() >= dto.getStudyFlowNew().getType()) {
                // 去游戏
                flowId1 = 84;
                this.changeFlowNodeLog(student, "游戏", unitNew, flowId1);
                return studyFlowNewMapper.selectById(flowId1);
            }
            // 返回流程 1
            flowId1 = 70;
            this.changeFlowNodeLog(student, "单词播放机", unitNew, flowId1);
            return studyFlowNewMapper.selectById(flowId1);
        }

        return byPrimaryKey;
    }

    /**
     * 当需要调过单词图鉴节点的时候记录日志
     *
     * @param student
     * @param model   学习模块
     */
    private void changeFlowNodeLog(Student student, String model, UnitNew unit, int flowId) {
        log.info("单元[{}]没有单词图片，学生[{} - {} - {}]进入{}流程，流程 id=[{}]",
                unit.getJointName(), student.getId(), student.getAccount(), student.getStudentName(), model, flowId);
    }

}
