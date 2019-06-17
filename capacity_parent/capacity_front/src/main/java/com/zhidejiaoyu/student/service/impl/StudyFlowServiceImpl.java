package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
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
    private StudentMapper studentMapper;

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

    /**
     * 节点学完, 把下一节初始化到student_flow表, 并把下一节点返回
     *
     * @param studentId 学生id
     * @param courseId 课程id
     * @param unitId 单元id
     * @param node 当前节点
     * @param grade 得分
     *
     * @return  id 节点id
     *          model_name 节点模块名
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> getNode(Long courseId, Long unitId, Long nodeId, Long grade, String isTrueFlow, HttpSession session) {
        Student student = getStudent(session);
        StudyFlow studyFlow = studyFlowMapper.selectById(nodeId);

        if (studyFlow != null) {
            // 学习下一单元, 前端需要一个弹框提示
            if (studyFlow.getNextTrueFlow() == 0) {
                // 开启下一单元并且返回需要学习的流程信息
                return openNextUnitAndReturn(unitId, session, student, studyFlow, grade);
            } else if (Objects.equals(TO_FLOW_TWO, studyFlow.getNextTrueFlow())) {
                // 进入流程2
                return toAnotherFlow(student, 24);
            } else if (Objects.equals(TO_FLOW_ONE, studyFlow.getNextTrueFlow())) {
                // 进入流程1
                // 判断当前单元流程1学习次数，如果没有学习，初始化同一单元的流程1；如果已学习初始化下一单元的流程1
                int count = learnMapper.countByStudentIdAndFlow(student.getId(), unitId, FLOW_ONE_STR);
                if (count == 0) {
                    return toAnotherFlow(student, FLOW_ONE);
                } else {
                    String s = this.unlockNextUnit(student, unitId, session, studyFlow, grade);
                    if (s != null) {
                        toAnotherFlow(student, FLOW_ONE);
                        return ServerResponse.createBySuccess(300, s);
                    } else {
                        return toAnotherFlow(student, FLOW_ONE);
                    }
                }
            } else if (Objects.equals(-2, studyFlow.getNextTrueFlow())) {
                // 继续上次流程
                StudyFlow lastStudyFlow = studyFlowMapper.selectStudentCurrentFlow(student.getId(), 1);
                return ServerResponse.createBySuccess("true", lastStudyFlow);
            } else {
                // 其余正常流程
                if (studyFlow.getNextFalseFlow() == null) {
                    // 直接进入下个流程节点
                    return this.toAnotherFlow(student, studyFlow.getNextTrueFlow());
                } else {
                    // 判断下个节点
                    return judgeNextNode(student, grade, isTrueFlow, studyFlow);
                }
            }
        }
        return null;
    }

    private ServerResponse<Object> judgeNextNode(Student student, Long grade, String isTrueFlow, StudyFlow studyFlow) {
        // 学生选择项，是否进行下一个节点， true：进行下一个节点；否则跳过下个节点
        if (isTrueFlow != null) {
            if (Objects.equals("true", isTrueFlow)) {
                return toAnotherFlow(student, studyFlow.getNextTrueFlow());
            } else {
                return toAnotherFlow(student, studyFlow.getNextFalseFlow());
            }
        }

        // 判断学生是否在当前分数段
        if (studyFlow.getType() != null) {
            StudyFlow falseFlow = studyFlowMapper.selectById(studyFlow.getNextFalseFlow());
            StudyFlow trueFlow = studyFlowMapper.selectById(studyFlow.getNextTrueFlow());
            if (Objects.equals(3, falseFlow.getType())) {
                ServerResponse<Object> x = toNextNode(student, grade, falseFlow);
                if (x != null) {
                    return x;
                }
            } else if (Objects.equals(3, trueFlow.getType())) {
                ServerResponse<Object> x = toNextNode(student, grade, trueFlow);
                if (x != null) {
                    return x;
                }
            }

            if (grade >= studyFlow.getType()) {
                return toAnotherFlow(student, studyFlow.getNextTrueFlow());
            } else if (grade < studyFlow.getType()) {
                return toAnotherFlow(student, studyFlow.getNextFalseFlow());
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
     * @param student
     * @param grade
     * @param flow
     * @return
     */
    private ServerResponse<Object> toNextNode(Student student, Long grade, StudyFlow flow) {
        if (grade != null) {
            // 50 <= 分数 < 90分走 nextTrue 流程，分数 < 50 走 nextFalse 流程
            if (grade >= 50 && grade < 90) {
                return toAnotherFlow(student, flow.getNextTrueFlow());
            } else if (grade < 90) {
                clearLearnRecord(student);
                return toAnotherFlow(student, flow.getNextFalseFlow());
            }
        }
        return null;
    }

    /**
     * 清除学生当前单元学习、测试、记忆追踪信息
     *
     * @param student
     */
    private void clearLearnRecord(Student student) {
        Long studentId = student.getId();
        CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selectCurrentUnitIdByStudentIdAndType(studentId, 1);
        Long unitId = capacityStudentUnit.getUnitId();

        learnMapper.updateTypeToLearned(studentId, 1, unitId, unitId);
        learnMapper.deleteByStudentIdAndUnitId(studentId, unitId);
        capacityPictureMapper.deleteByStudentIdAndUnitId(studentId, unitId, unitId);
        capacityMemoryMapper.deleteByStudentIdAndUnitId(studentId, unitId, unitId);
        capacityWriteMapper.deleteByStudentIdAndUnitId(studentId, unitId, unitId);
        capacityListenMapper.deleteByStudentIdAndUnitId(studentId, unitId, unitId);
    }

    /**
     * 进入其他流程
     *
     * @param student
     * @param flowId  其他流程初始流程id
     * @return
     */
    private ServerResponse<Object> toAnotherFlow(Student student, int flowId) {
        CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selectCurrentUnitIdByStudentIdAndType(student.getId(), 1);
        studentFlowMapper.updateFlowByStudentId(student.getId(), flowId);
        StudyFlow byPrimaryKey = studyFlowMapper.selectById(flowId);

        if (Objects.equals(TO_FLOW_ONE, byPrimaryKey.getNextTrueFlow())) {
            log.info("[{} -{} -{}] 前往流程 1.", student.getId(), student.getAccount(), student.getStudentName());
            return toAnotherFlow(student, FLOW_ONE);
        } else if (Objects.equals(TO_FLOW_TWO, byPrimaryKey.getNextTrueFlow())) {
            log.info("[{} -{} -{}] 前往流程 2.", student.getId(), student.getAccount(), student.getStudentName());
            return toAnotherFlow(student, FLOW_TWO);
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
     * 开启下一单元并且返回需要学习的流程信息
     *
     * @param unitId
     * @param session
     * @param student
     * @param studyFlow
     * @param grade
     * @return
     */
    private ServerResponse<Object> openNextUnitAndReturn(Long unitId, HttpSession session, Student student,
                                                         StudyFlow studyFlow, Long grade) {
        // 开启下一单元
        String s = unlockNextUnit(student, unitId, session, studyFlow, grade);
        // 获取流程信息
        if (s != null) {
            if (FLOW_ONE_STR.equals(studyFlow.getFlowName())) {
                this.toAnotherFlow(student, 11);
            } else {
                this.toAnotherFlow(student, 24);
            }
            // 分配单元已学习完
            return ServerResponse.createBySuccess(300, s);
        }

        if (FLOW_ONE_STR.equals(studyFlow.getFlowName())) {
            return this.toAnotherFlow(student, 11);
        } else {
            return this.toAnotherFlow(student, 24);
        }
    }

    /**
     * 当前流程学习完毕后开启下一单元
     *
     * @param unitId   单元id（当前单元闯关测试的单元id）
     * @param studyFlow
     * @param grade
     * @return
     */
    private String unlockNextUnit(Student student, Long unitId, HttpSession session, StudyFlow studyFlow, Long grade) {
        Long studentId = student.getId();
        CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selectCurrentUnitIdByStudentIdAndType(student.getId(), 1);
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
            if ("流程1".equals(studyFlow.getFlowName())) {
                this.toAnotherFlow(student, FLOW_ONE);
            } else {
                this.toAnotherFlow(student, FLOW_TWO);
            }

            // 判断学生是否能获取课程证书,每个课程智能获取一个课程证书
            int count = ccieMapper.countCourseCcieByCourseId(studentId, capacityStudentUnit.getCourseId());
            if (count == 0) {
                int unitCount = unitMapper.countByCourseId(studentStudyPlan.getCourseId());
                int learnedUnitCount = learnMapper.countLearnedUnitByCourseId(studentStudyPlan.getCourseId(), studentId);
                if (learnedUnitCount >= unitCount) {
                    // 课程学习完毕，奖励学生课程证书
                    ccieUtil.saveCourseCcie(student, capacityStudentUnit.getCourseId(), capacityStudentUnit.getUnitId(), grade == null ? 0 : Integer.valueOf(grade.toString()));
                }
            }

            learnMapper.updateTypeToLearned(studentId, 1, startUnit, endUnit);
            capacityPictureMapper.deleteByStudentIdAndUnitId(studentId, startUnit, endUnit);
            capacityMemoryMapper.deleteByStudentIdAndUnitId(studentId, startUnit, endUnit);
            capacityWriteMapper.deleteByStudentIdAndUnitId(studentId, startUnit, endUnit);
            capacityListenMapper.deleteByStudentIdAndUnitId(studentId, startUnit, endUnit);

            if (studentStudyPlan.getCurrentStudyCount() >= studentStudyPlan.getTotalStudyCount()) {
                // 当前学习计划完成需要学习的遍数
                studentStudyPlan.setComplete(2);
                studentStudyPlan.setUpdateTime(new Date());
                studentStudyPlanMapper.updateById(studentStudyPlan);

                StudentStudyPlan nextPlan = studentStudyPlanMapper.selectNextPlan(studentId, studentStudyPlan.getId(), 1);
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

                studentStudyPlan.setUpdateTime(new Date());
                studentStudyPlan.setCurrentStudyCount(studentStudyPlan.getCurrentStudyCount() + 1);
                studentStudyPlanMapper.updateById(studentStudyPlan);

                // 学生学习到老师分配的最后一个单元，提示学生
                return "傲人的成绩离不开反复的磨练，再学一次吧！";
            }
        } else {

            Long currentUnitId = capacityStudentUnit.getUnitId();
            long nextUnitId = currentUnitId + 1;

            studentStudyPlan.setUpdateTime(new Date());
            studentStudyPlan.setCurrentStudyCount(studentStudyPlan.getCurrentStudyCount() + 1);
            studentStudyPlanMapper.updateById(studentStudyPlan);

            updateCapacityStudentUnit(capacityStudentUnit, nextUnitId, null);
            saveOpenUnitLog(student, unitId, nextUnitId);

            // 更新学生session
            Student student1 = studentMapper.selectByPrimaryKey(student.getId());
            session.setAttribute(UserConstant.CURRENT_STUDENT, student1);
        }
        return null;
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
        }
        capacityStudentUnitMapper.updateById(capacityStudentUnit);
    }

    private CapacityStudentUnit packageCapacityStudentUnit(Student student, Long courseId, Long nextUnitId, Unit unit) {
        CapacityStudentUnit capacityStudentUnit = new CapacityStudentUnit();
        Course course = courseMapper.selectById(courseId);
        capacityStudentUnit.setCourseId(courseId);
        capacityStudentUnit.setStudentId(student.getId());
        capacityStudentUnit.setType(1);
        capacityStudentUnit.setUnitId(nextUnitId);
        capacityStudentUnit.setUnitName(unit.getUnitName());
        capacityStudentUnit.setCourseName(course.getCourseName());
        return capacityStudentUnit;
    }

    private void saveOpenUnitLog(Student student, Long unitId, Long nextUnitId) {
        // 保存开启单元日志记录
        OpenUnitLog openUnitLog = new OpenUnitLog();
        openUnitLog.setCreateTime(new Date());
        openUnitLog.setCurrentUnitId(unitId);
        openUnitLog.setNextUnitId(nextUnitId);
        openUnitLog.setStudentId(student.getId());
        try{
            openUnitLogMapper.insert(openUnitLog);
        } catch (Exception e) {
            log.error("学生 {}-{} 保存开启单元日志出错；当前单元 {}，下一单元 {}", student.getId(), student.getStudentName(), unitId, nextUnitId, e);
        }
    }

}
