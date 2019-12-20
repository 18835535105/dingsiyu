package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.constant.study.PointConstant;
import com.zhidejiaoyu.common.exception.Enum.ServiceExceptionEnum;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.dto.NodeDto;
import com.zhidejiaoyu.student.service.StudyFlowService;
import com.zhidejiaoyu.student.utils.CcieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
public class StudyFlowServiceImpl extends BaseServiceImpl<StudyFlowMapper, StudyFlow> implements StudyFlowService {

    /**
     * 进入流程 1 标识
     */
    private static final int TO_FLOW_ONE = -3;

    /**
     * 进入流程 2 标识
     */
    private static final int TO_FLOW_TWO = -1;

    /**
     * 流程 1 起始节点
     */
    private static final int FLOW_ONE = 11;

    /**
     * 流程 2 起始节点
     */
    private static final int FLOW_TWO = 24;

    private final String FLOW_ONE_STR = "流程1";

    @Resource
    private StudyFlowMapper studyFlowMapper;

    @Resource
    private StudentFlowMapper studentFlowMapper;

    @Resource
    private LearnMapper learnMapper;

    @Resource
    private UnitMapper unitMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private TestRecordMapper testRecordMapper;

    @Resource
    private CapacityPictureMapper capacityPictureMapper;

    @Autowired
    private CapacityMemoryMapper capacityMemoryMapper;

    @Autowired
    private CapacityWriteMapper capacityWriteMapper;

    @Autowired
    private CapacityListenMapper capacityListenMapper;

    @Resource
    private OpenUnitLogMapper openUnitLogMapper;

    @Autowired
    private CapacityStudentUnitMapper capacityStudentUnitMapper;

    @Autowired
    private StudentStudyPlanMapper studentStudyPlanMapper;

    @Autowired
    private CcieMapper ccieMapper;

    @Autowired
    private CcieUtil ccieUtil;

    @Resource
    private VocabularyMapper vocabularyMapper;

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
        StudyFlow studyFlow = studyFlowMapper.selectById(dto.getNodeId());

        dto.setStudyFlow(studyFlow);
        dto.setSession(session);
        dto.setStudent(student);

        if (studyFlow != null) {
            // 学习下一单元, 前端需要一个弹框提示
            if (studyFlow.getNextTrueFlow() == 0) {
                // 开启下一单元并且返回需要学习的流程信息
                return openNextUnitAndReturn(dto);
            } else if (Objects.equals(TO_FLOW_TWO, studyFlow.getNextTrueFlow())) {
                // 进入流程2
                return toAnotherFlow(dto, 24);
            } else if (Objects.equals(TO_FLOW_ONE, studyFlow.getNextTrueFlow())) {
                // 进入流程1
                // 判断当前单元流程1学习次数，如果没有学习，初始化同一单元的流程1；如果已学习初始化下一单元的流程1
                return toFlowOne(dto);
            } else if (Objects.equals(-2, studyFlow.getNextTrueFlow())) {
                // 继续上次流程
                StudyFlow lastStudyFlow = studyFlowMapper.selectStudentCurrentFlow(student.getId(), 1);
                return ServerResponse.createBySuccess("true", lastStudyFlow);
            } else {
                // 其余正常流程
                if (studyFlow.getNextFalseFlow() == null) {
                    // 直接进入下个流程节点
                    return this.toAnotherFlow(dto, studyFlow.getNextTrueFlow());
                } else {
                    // 判断下个节点
                    return judgeNextNode(dto);
                }
            }
        }
        return null;
    }

    /**
     * 前往流程 1 的时候判断是当前单元的流程 1 还是下一单元的流程 1
     *
     * @param dto
     * @return
     */
    private ServerResponse<Object> toFlowOne(NodeDto dto) {
        int count = learnMapper.countByStudentIdAndFlow(dto.getStudent().getId(), dto.getUnitId(), FLOW_ONE_STR);
        if (count == 0) {
            return toAnotherFlow(dto, FLOW_ONE);
        } else {
            String s = this.unlockNextUnit(dto);
            if (s != null) {
                toAnotherFlow(dto, FLOW_ONE);
                return ServerResponse.createBySuccess(300, s);
            } else {
                return toAnotherFlow(dto, FLOW_ONE);
            }
        }
    }

    private ServerResponse<Object> judgeNextNode(NodeDto dto) {
        String isTrueFlow = dto.getTrueFlow();
        StudyFlow studyFlow = dto.getStudyFlow();
        // 学生选择项，是否进行下一个节点， true：进行下一个节点；否则跳过下个节点
        if (isTrueFlow != null) {
            if (Objects.equals("true", isTrueFlow)) {
                return toAnotherFlow(dto, studyFlow.getNextTrueFlow());
            } else {
                return toAnotherFlow(dto, studyFlow.getNextFalseFlow());
            }
        }

        // 判断学生是否在当前分数段
        if (studyFlow.getType() != null) {
            StudyFlow falseFlow = studyFlowMapper.selectById(studyFlow.getNextFalseFlow());
            StudyFlow trueFlow = studyFlowMapper.selectById(studyFlow.getNextTrueFlow());

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
            if (grade >= studyFlow.getType()) {
                return toAnotherFlow(dto, studyFlow.getNextTrueFlow());
            } else if (grade < studyFlow.getType()) {
                return toAnotherFlow(dto, studyFlow.getNextFalseFlow());
            } else {
                // 判断是否进行单词好声音
                return ServerResponse.createBySuccess("true", studyFlow);
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
    private ServerResponse<Object> toNextNode(NodeDto dto, StudyFlow flow) {
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
     * @return
     */
    private ServerResponse<Object> toAnotherFlow(NodeDto dto, int flowId) {
        Student student = dto.getStudent();

        CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selectByStudentIdAndType(student.getId(), 1);

        // 判断当前单元单词是否有图片，如果都没有图片不进入单词图鉴
        StudyFlow byPrimaryKey = this.getStudyFlow(dto, flowId);
        studentFlowMapper.updateFlowByStudentId(student.getId(), byPrimaryKey.getId());

        if (Objects.equals(TO_FLOW_ONE, byPrimaryKey.getNextTrueFlow())) {
            log.info("[{} -{} -{}] 前往流程 1.", student.getId(), student.getAccount(), student.getStudentName());
            return toFlowOne(dto);
        } else if (Objects.equals(TO_FLOW_TWO, byPrimaryKey.getNextTrueFlow())) {
            log.info("[{} -{} -{}] 前往流程 2.", student.getId(), student.getAccount(), student.getStudentName());
            return toAnotherFlow(dto, FLOW_TWO);
        }

        if (capacityStudentUnit != null) {
            byPrimaryKey.setCourseId(capacityStudentUnit.getCourseId());
            byPrimaryKey.setUnitId(capacityStudentUnit.getUnitId());
            byPrimaryKey.setCourseName(capacityStudentUnit.getCourseName());
            byPrimaryKey.setUnitName(capacityStudentUnit.getUnitName());
        } else {
            log.error("教师已经删除[{} -> {} -> {}]正在学习的学习计划。", student.getId(), student.getAccount(), student.getStudentName());
        }
        return ServerResponse.createBySuccess("true", byPrimaryKey);
    }

    /**
     * @param dto
     * @param flowId 下个流程节点的 id
     * @return
     */
    private StudyFlow getStudyFlow(NodeDto dto, int flowId) {
        StudyFlow byPrimaryKey = studyFlowMapper.selectById(flowId);

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

        Unit unit = unitMapper.selectById(unitId);
        Student student = dto.getStudent() == null ? new Student() : dto.getStudent();

        // 需要跳转到的流程 id
        int flowId1;
        // 流程 1 单词图鉴流程 id
        int flowOnePicture = 15;
        // 流程 1 的单词图鉴
        if (flowId == flowOnePicture) {
            if (dto.getGrade() != null && dto.getGrade() >= dto.getStudyFlow().getType()) {
                // 去流程 2 的慧听写
                flowId1 = 18;
                this.changeFlowNodeLog(student, "慧听写", unit, flowId1);
                return studyFlowMapper.selectById(flowId1);
            }
            // 如果是从单词播放机直接进入单词图鉴，将流程跳转到慧记忆
            if (Objects.equals(dto.getNodeId(), 22L)) {
                flowId1 = 48;
                this.changeFlowNodeLog(student, "慧记忆", unit, flowId1);
                return studyFlowMapper.selectById(flowId1);
            }
            // 返回流程 1
            flowId1 = 9;
            this.changeFlowNodeLog(student, "单词播放机", unit, flowId1);
            return studyFlowMapper.selectById(flowId1);
        }

        // 流程 2 慧听写的单词图鉴
        int flowTwoListenPicture = 35;
        if (flowId == flowTwoListenPicture) {
            if (dto.getGrade() != null && dto.getGrade() >= dto.getStudyFlow().getType()) {
                // 去流程 2 的慧默写
                flowId1 = 28;
                this.changeFlowNodeLog(student, "慧默写", unit, flowId1);
                return studyFlowMapper.selectById(flowId1);
            }
            // 返回流程 1
            flowId1 = 9;
            this.changeFlowNodeLog(student, "单词播放机", unit, flowId1);
            return studyFlowMapper.selectById(9);
        }

        // 流程 2 慧默写的单词图鉴
        int flowTwoWritePicture = 33;
        if (flowId == flowTwoWritePicture) {
            if (dto.getGrade() != null && dto.getGrade() >= dto.getStudyFlow().getType()) {
                // 去游戏
                flowId1 = 31;
                this.changeFlowNodeLog(student, "游戏", unit, flowId1);
                return studyFlowMapper.selectById(flowId1);
            }
            // 返回流程 1
            flowId1 = 9;
            this.changeFlowNodeLog(student, "单词播放机", unit, flowId1);
            return studyFlowMapper.selectById(9);
        }

        return byPrimaryKey;
    }

    /**
     * 当需要调过单词图鉴节点的时候记录日志
     *
     * @param student
     * @param model   学习模块
     */
    private void changeFlowNodeLog(Student student, String model, Unit unit, int flowId) {
        log.info("单元[{}]没有单词图片，学生[{} - {} - {}]进入{}流程，流程 id=[{}]",
                unit.getJointName(), student.getId(), student.getAccount(), student.getStudentName(), model, flowId);
    }

    /**
     * 开启下一单元并且返回需要学习的流程信息
     *
     * @return
     */
    private ServerResponse<Object> openNextUnitAndReturn(NodeDto dto) {
        String flowName = dto.getStudyFlow().getFlowName();

        // 开启下一单元
        String s = unlockNextUnit(dto);
        // 获取流程信息
        if (s != null) {
            if (FLOW_ONE_STR.equals(flowName)) {
                this.toAnotherFlow(dto, 11);
            } else {
                this.toAnotherFlow(dto, 24);
            }
            // 分配单元已学习完
            return ServerResponse.createBySuccess(300, s);
        }

        if (FLOW_ONE_STR.equals(flowName)) {
            return this.toAnotherFlow(dto, 11);
        } else {
            return this.toAnotherFlow(dto, 24);
        }
    }

    /**
     * 当前流程学习完毕后开启下一单元
     *
     * @return
     */
    private String unlockNextUnit(NodeDto dto) {
        Student student = dto.getStudent();
        Long studentId = student.getId();
        Long grade = dto.getGrade();


        CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selectByStudentIdAndType(studentId, 1);
        if (capacityStudentUnit == null) {
            log.error("学生[{} - {} - {}]还没有初始化同步版课程！", studentId, student.getAccount(), student.getStudentName());
            throw new ServiceException(ServiceExceptionEnum.NO_CAPACITY_COURSE);
        }
        // 清除学生当前已分配的单元学习记录
        Long startUnit = capacityStudentUnit.getStartunit();
        Long endUnit = capacityStudentUnit.getEndunit();

        // 查询学生当前学习计划
        StudentStudyPlan studentStudyPlan = studentStudyPlanMapper.selectCurrentPlan(studentId, startUnit, endUnit, 1);
        if (studentStudyPlan == null) {
            return "恭喜你，完成了本次学习任务，快去向教师申请开始新的征程吧！";
        }

        // 学完当前学习计划最后一个单元
        if (Objects.equals(capacityStudentUnit.getUnitId(), capacityStudentUnit.getEndunit())) {

            // 初始化当前流程的初始单元
            // 获取流程信息
            if ("流程1".equals(dto.getStudyFlow().getFlowName())) {
                this.toAnotherFlow(dto, FLOW_ONE);
            } else {
                this.toAnotherFlow(dto, FLOW_TWO);
            }

            // 判断学生是否能获取课程证书,每个课程智能获取一个课程证书
            int count = ccieMapper.countCourseCcieByCourseId(studentId, capacityStudentUnit.getCourseId());
            if (count == 0) {
                int unitCount = unitMapper.countByCourseId(studentStudyPlan.getCourseId());
                int learnedUnitCount = learnMapper.countLearnedUnitByCourseId(studentStudyPlan.getCourseId(), studentId);
                if (learnedUnitCount >= unitCount) {
                    // 课程学习完毕，奖励学生课程证书
                    ccieUtil.saveCourseCcie(student, capacityStudentUnit.getCourseId(), capacityStudentUnit.getUnitId(), grade == null ? 0 : Integer.parseInt(grade.toString()));
                }
            }

            this.clearLearnRecord(studentId, startUnit, endUnit);

            if (studentStudyPlan.getCurrentStudyCount() >= studentStudyPlan.getTotalStudyCount()) {
                // 当前学习计划完成需要学习的遍数
                studentStudyPlan.setComplete(2);
                studentStudyPlan.setUpdateTime(new Date());
                studentStudyPlanMapper.updateById(studentStudyPlan);

                this.logInfo(student, studentStudyPlan);

                StudentStudyPlan nextPlan = studentStudyPlanMapper.selectNextPlan(studentId, 1);
                if (nextPlan == null) {
                    // 教师分配的所有计划已完成
                    return "恭喜你，完成了本次学习任务，快去向教师申请开始新的征程吧！";
                }
                updateCapacityStudentUnit(capacityStudentUnit, nextPlan.getStartUnitId(), nextPlan);

                // 进入下一学习计划前删除学前游戏测试记录，让学生再次进行学前游戏测试，重新初始化流程
                testRecordMapper.deleteGameRecord(studentId);

                return "干的漂亮，当前计划已经完成，新的计划已经开启！";
            } else {
                updateCapacityStudentUnit(capacityStudentUnit, startUnit, null);

                this.logInfo(student, studentStudyPlan);

                studentStudyPlan.setUpdateTime(new Date());
                studentStudyPlan.setCurrentStudyCount(studentStudyPlan.getCurrentStudyCount() + 1);
                studentStudyPlanMapper.updateById(studentStudyPlan);

                // 学生学习到老师分配的最后一个单元，提示学生
                return "傲人的成绩离不开反复的磨练，再学一次吧！";
            }
        } else {

            Long currentUnitId = capacityStudentUnit.getUnitId();

            // 防止需要进入下一单元的时候，学生重复刷新测试记录，导致capacityStudentUnit中单元持续累加的问题
            // 只有当前单元已经学习了，才会初始化下一个单元
            int count = learnMapper.countByStudentIdAndFlow(dto.getStudent().getId(), currentUnitId, dto.getStudyFlow().getFlowName());
            if (count > 0) {
                long nextUnitId = currentUnitId + 1;

                this.logInfo(student, studentStudyPlan);

                updateCapacityStudentUnit(capacityStudentUnit, nextUnitId, null);
                saveOpenUnitLog(student, dto.getUnitId(), nextUnitId);
            }
        }
        return null;
    }

    /**
     * 清除学生指定单元的学习记录
     *
     * @param studentId
     * @param startUnit
     * @param endUnit
     */
    private void clearLearnRecord(Long studentId, Long startUnit, Long endUnit) {
        learnMapper.updateTypeToLearned(studentId, 1, startUnit, endUnit);
        capacityPictureMapper.deleteByStudentIdAndUnitId(studentId, startUnit, endUnit);
        capacityMemoryMapper.deleteByStudentIdAndUnitId(studentId, startUnit, endUnit);
        capacityWriteMapper.deleteByStudentIdAndUnitId(studentId, startUnit, endUnit);
        capacityListenMapper.deleteByStudentIdAndUnitId(studentId, startUnit, endUnit);
    }

    private void logInfo(Student student, StudentStudyPlan studentStudyPlan) {
        log.info("学生[{} -{} -{}]当前学习计划courseId=[{}], unitId=[{}], type=[{}]需学习总遍数：[{}], 当前学习遍数：[{}]",
                student.getId(), student.getAccount(), student.getStudentName(),
                studentStudyPlan.getCourseId(), studentStudyPlan.getEndUnitId(), studentStudyPlan.getType(),
                studentStudyPlan.getTotalStudyCount(), studentStudyPlan.getCurrentStudyCount());
    }

    private void updateCapacityStudentUnit(CapacityStudentUnit capacityStudentUnit, long nextUnitId, StudentStudyPlan nextPlan) {
        Unit unit = unitMapper.selectById(nextUnitId);
        Course course = courseMapper.selectById(unit.getCourseId());
        capacityStudentUnit.setCourseName(course.getCourseName());
        capacityStudentUnit.setCourseId(unit.getCourseId());
        capacityStudentUnit.setUnitName(unit.getUnitName());
        capacityStudentUnit.setUnitId(unit.getId());
        capacityStudentUnit.setVersion(course.getVersion());
        if (nextPlan != null) {
            capacityStudentUnit.setStartunit(nextPlan.getStartUnitId());
            capacityStudentUnit.setEndunit(nextPlan.getEndUnitId());
            this.clearLearnRecord(capacityStudentUnit.getStudentId(), nextPlan.getStartUnitId(), nextPlan.getEndUnitId());
        }
        capacityStudentUnitMapper.updateById(capacityStudentUnit);
    }

    private void saveOpenUnitLog(Student student, Long unitId, Long nextUnitId) {
        // 保存开启单元日志记录
        OpenUnitLog openUnitLog = new OpenUnitLog();
        openUnitLog.setCreateTime(new Date());
        openUnitLog.setCurrentUnitId(unitId);
        openUnitLog.setNextUnitId(nextUnitId);
        openUnitLog.setStudentId(student.getId());
        try {
            openUnitLogMapper.insert(openUnitLog);
        } catch (Exception e) {
            log.error("学生 {}-{} 保存开启单元日志出错；当前单元 {}，下一单元 {}", student.getId(), student.getStudentName(), unitId, nextUnitId, e);
        }
    }

}
