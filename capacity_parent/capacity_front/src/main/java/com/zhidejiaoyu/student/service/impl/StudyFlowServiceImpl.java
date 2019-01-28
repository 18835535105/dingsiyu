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
@Transactional
@Slf4j
public class StudyFlowServiceImpl extends BaseServiceImpl<StudyFlowMapper, StudyFlow> implements StudyFlowService {

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
    public ServerResponse<Object> initializeNode(long studentId, long courseId, long unitId, long node, Long grade, HttpSession session) {
        /*// 获取当前节点信息
        StudyFlow flow = studyFlowMapper.selectByPrimaryKey(node);

        Student st = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);

        // 学习下一单元, 前端需要一个弹框提示!!!
        if("学习下一单元".equals(flow.getModelName()) || "0".equals(flow.getNextTrueFlow().toString())){

        	// 查询当前用户是否是新生, 有效时间小于2小时
        	int labelValidTimeByStudentId = durationMapper.labelValidTimeByStudentId(st.getId());
        	// 当前用户是新生继续17流程
        	if(labelValidTimeByStudentId < 2) {
        		// 开启下一单元
        		unlockNextUnit(st, courseId, unitId, session);
        		// 初始化17流程
        		studentFlowMapper.updateFlowByStudentId(studentId, 463);
        		// 获取流程信息
        		StudyFlow byPrimaryKey = studyFlowMapper.selectByPrimaryKey(463L);
        		Student studentCourseUnit = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        		byPrimaryKey.setCourseId(studentCourseUnit.getCourseId());
        		byPrimaryKey.setUnitId(studentCourseUnit.getUnitId());
        		byPrimaryKey.setCourseName(studentCourseUnit.getCourseName());
        		byPrimaryKey.setUnitName(studentCourseUnit.getUnitName());
        		return ServerResponse.createBySuccess("true", byPrimaryKey);
        	}


            // 执行一级二级标签初始化流程
            Map flowNameData = studyFlowName.getFlowName(studentId);
            // 流程名
            String flowName = flowNameData.get("flowName").toString();
            // 1=甲 2=乙 3=丙 4=丁
            String type = flowNameData.get("type").toString();

            int ii = 2; // 0代表正在学的是精华版课程, 1代表普通版本降级到了精华版本
            if("3".equals(type) || "4".equals(type)){
                // 降级规则, 差生进行判断
                ii = flowDropRules(studentId, courseId, st, session);
            }

            int upgrade = 0; // 1代表从精华版升级到普通版本
            if(ii != 1){
                // 升级规则, 差生进行判断
                upgrade = upgradeRules(studentId, courseId, unitId, st, session);
            }

            // 精华版升级到普通版本, 更新学生session
            if(upgrade == 1){
                st = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
            }

            // 精华版开启下一单元 / 下一课程
            if(st.getVersion().contains("小学精华版") || st.getVersion().contains("初一精华版") ||
                    st.getVersion().contains("中考精华版") || st.getVersion().contains("高一精华版") ||
                    st.getVersion().contains("高考精华版")){

                // 更新下一单元数据到学生表, 如果是最后一单元删掉精华版学习数据重新学习该精华版课程
                // 课程下最大单元id
                int maxUnitId = unitMapper.maxUnitIndexByCourseId(courseId);
                // 学习的是精华版最后一个单元
                if(maxUnitId == unitId){
                    // 删掉当前精华版学习数据
                    deleteEssenceVersionLearn(studentId, courseId);
                    // 把学生表单元改为该精华版的第一单元, 返回单元id,单元名
                    Map minUnit = unitMapper.getLimitOneByCourseId(courseId);
                    unitId =Long.valueOf(minUnit.get("id").toString());
                    Student stu = new Student();
                    stu.setUnitId(unitId);
                    stu.setUnitName(minUnit.get("unit_name").toString());
                    stu.setSentenceUnitId(new Long(unitId).intValue());
                    stu.setSentenceUnitName(minUnit.get("unit_name").toString());
                }else{
                    // 获取下一单元id, 单元名   返回字段:id, unit_name
                    Map nextUnit = unitMapper.nextUnitId(courseId, unitId);
                    // 更新下一单元信息到学生表
                    Student stu = new Student();
                    stu.setUnitId((long)nextUnit.get("id"));
                    stu.setUnitName(nextUnit.get("unit_name").toString());
                    stu.setSentenceUnitId(Integer.parseInt(nextUnit.get("id")+""));
                    stu.setSentenceUnitName(nextUnit.get("unit_name").toString());
                    studentMapper.updateByPrimaryKeySelective(stu);
                }
                // 更新学生session
                Student student1 = studentMapper.selectByPrimaryKey(studentId);
                session.setAttribute(UserConstant.CURRENT_STUDENT, student1);
            }else{
                // 普通版执行解锁下一单元方法
                unlockNextUnit(st, courseId, unitId, session);
            }

            // 根据流程名获取第一条流程数据
            StudyFlow flowInfo =  studyFlowMapper.getNodeByNodeName(flowName);

            // 判断是否需要课程前测
            int maxUnitId = unitMapper.maxUnitIndexByCourseId(courseId);
            if (maxUnitId == unitId){
                // 需要进行课程前测, 节点保存到中间表
                int i = studentFlowMapper.updateFlowByStudentId(studentId, flowInfo.getId());
            }else{
                // 跳过课程前测, 把下一节点保存到中间表, 并返回给页面
                flowInfo = studyFlowMapper.selectByPrimaryKey(flowInfo.getNextTrueFlow().longValue());
                int i = studentFlowMapper.updateFlowByStudentId(studentId, flowInfo.getId());
            }
            flowInfo.setNextFalseFlow(null);
            flowInfo.setNextTrueFlow(null);

            // 下一节点需要学习的课程id,和单元id
            Student studentCourseUnit = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
            flowInfo.setCourseId(studentCourseUnit.getCourseId());
            flowInfo.setUnitId(studentCourseUnit.getUnitId());
            flowInfo.setCourseName(studentCourseUnit.getCourseName());
            flowInfo.setUnitName(studentCourseUnit.getUnitName());

            return ServerResponse.createBySuccess("true", flowInfo); // 开启下一单元
            // return ServerResponse.createBySuccess(flowInfo);
        }

        // 下一节点id
        long nodeId = 0;
        // 判断测试类型
        Integer type = flow.getType();
        // 判断五种测试结果
        if(grade != null && type != null){
            if(type == 1){
                // 优秀
                if (grade >= 90){
                    nodeId = flow.getNextTrueFlow();
                }else{
                    nodeId = flow.getNextFalseFlow();
                }
            }else if (type == 2){
                // 通过
                if (grade >= 80){
                    nodeId = flow.getNextTrueFlow();
                }else{
                    nodeId = flow.getNextFalseFlow();
                }
            }else if (type == 3){
                // 生词大于=3
                int newWord = learnMapper.countNewWordByStudentIdAndUnitId(studentId, unitId);
                if(newWord >= 3){
                    nodeId = flow.getNextTrueFlow();
                }else{
                    nodeId = flow.getNextFalseFlow();
                }
            }else if (type == 4){
                // 是否存在生词
                int newWord = learnMapper.countNewWordByStudentIdAndUnitId(studentId, unitId);
                if(newWord != 0){
                    nodeId = flow.getNextTrueFlow();
                }else{
                    nodeId = flow.getNextFalseFlow();
                }
            }else{
                // 满分
                if (grade >= 100){
                    nodeId = flow.getNextTrueFlow();
                }else{
                    nodeId = flow.getNextFalseFlow();
                }
            }
        }else{
            nodeId = flow.getNextTrueFlow();
        }

        // 获取下一节信息
        StudyFlow nextNode = studyFlowMapper.selectByPrimaryKey(nodeId);
        // 将下一节点id更改到student_flow表
        int i = studentFlowMapper.updateFlowByStudentId(studentId, nextNode.getId());
        nextNode.setNextFalseFlow(null);
        nextNode.setNextTrueFlow(null);

        // 下一节点需要学习的课程id,和单元id
        Student studentCourseUnit = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        nextNode.setCourseId(studentCourseUnit.getCourseId());
        nextNode.setUnitId(studentCourseUnit.getUnitId());
        nextNode.setCourseName(studentCourseUnit.getCourseName());
        nextNode.setUnitName(studentCourseUnit.getUnitName());

        return ServerResponse.createBySuccess("false", nextNode); // 继续学习, 不开启下一单元*/
        return null;
    }

    @Override
    public ServerResponse<Object> getNode(Long courseId, Long unitId, Long nodeId, Long grade, String isTrueFlow, HttpSession session) {
        Student student = getStudent(session);
        StudyFlow studyFlow = studyFlowMapper.selectById(nodeId);

        if (studyFlow != null) {
            // 学习下一单元, 前端需要一个弹框提示
            if (studyFlow.getNextTrueFlow() == 0) {
                // 开启下一单元并且返回需要学习的流程信息
                return openNextUnitAndReturn(unitId, session, student, studyFlow);
            } else if (Objects.equals(-1, studyFlow.getNextTrueFlow())) {
                // 进入流程2
                return toAnotherFlow(student, 24);
            } else if (Objects.equals(-3, studyFlow.getNextTrueFlow())) {
                // 进入流程1
                // 判断当前单元流程1学习次数，如果没有学习，初始化同一单元的流程1；如果已学习初始化下一单元的流程1
                int count = learnMapper.countByStudentIdAndFlow(student.getId(), unitId, "流程1");
                if (count == 0) {
                    return toAnotherFlow(student, 11);
                } else {
                    String s = this.unlockNextUnit(student, unitId, session, studyFlow);
                    if (s != null) {
                        toAnotherFlow(student, 11);
                        return ServerResponse.createBySuccess(300, s);
                    } else {
                        return toAnotherFlow(student, 11);
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
                    ServerResponse<Object> x = judgeNextNode(student, grade, isTrueFlow, studyFlow);
                    if (x != null) {
                        return x;
                    }
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
        }

        if (Objects.equals(2, studyFlow.getType())) {
            // 分数>=80分走 nextTrue 流程，否则走 nextFalse 流程
            if (grade != null) {
                if (grade >= 80) {
                    return toAnotherFlow(student, studyFlow.getNextTrueFlow());
                } else {
                    return toAnotherFlow(student, studyFlow.getNextFalseFlow());
                }
            }
        } else if (Objects.equals(1, studyFlow.getType())) {
            // 分数>=60分走 nextTrue 流程，否则走 nextFalse 流程
            if (grade != null) {
                if (grade >= 60) {
                    return toAnotherFlow(student, studyFlow.getNextTrueFlow());
                } else {
                    return toAnotherFlow(student, studyFlow.getNextFalseFlow());
                }
            }
        } else {
            // 判断是否进行单词好声音
            return ServerResponse.createBySuccess("true", studyFlow);
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
            // 50 <= 分数 < 80分走 nextTrue 流程，分数 < 50 走 nextFalse 流程
            if (grade >= 50 && grade < 80) {
                return toAnotherFlow(student, flow.getNextTrueFlow());
            } else if (grade < 50) {
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
        byPrimaryKey.setCourseId(capacityStudentUnit.getCourseId());
        byPrimaryKey.setUnitId(capacityStudentUnit.getUnitId());
        byPrimaryKey.setCourseName(capacityStudentUnit.getCourseName());
        byPrimaryKey.setUnitName(capacityStudentUnit.getUnitName());
        return ServerResponse.createBySuccess("true", byPrimaryKey);
    }

    /**
     * 开启下一单元并且返回需要学习的流程信息
     *
     * @param unitId
     * @param session
     * @param student
     * @param studyFlow
     * @return
     */
    private ServerResponse<Object> openNextUnitAndReturn(Long unitId, HttpSession session, Student student,
                                                         StudyFlow studyFlow) {
        // 开启下一单元
        String s = unlockNextUnit(student, unitId, session, studyFlow);
        // 获取流程信息

        if (s != null) {
            if ("流程1".equals(studyFlow.getFlowName())) {
                this.toAnotherFlow(student, 11);
            } else {
                this.toAnotherFlow(student, 24);
            }
            // 分配单元已学习完
            return ServerResponse.createBySuccess(300, s);
        }

        if ("流程1".equals(studyFlow.getFlowName())) {
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
     * @return
     */
    private String unlockNextUnit(Student student, Long unitId, HttpSession session, StudyFlow studyFlow) {
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
                this.toAnotherFlow(student, 11);
            } else {
                this.toAnotherFlow(student, 24);
            }

            // 判断学生是否能获取课程证书,每个课程智能获取一个课程证书
            int count = ccieMapper.countCourseCcieByCourseId(studentId, capacityStudentUnit.getCourseId());
            if (count == 0) {
                int unitCount = unitMapper.countByCourseId(studentStudyPlan.getCourseId());
                int learnedUnitCount = learnMapper.countLearnedUnitByCourseId(studentStudyPlan.getCourseId(), studentId);
                if (learnedUnitCount >= unitCount) {
                    // 课程学习完毕，奖励学生课程证书
                    ccieUtil.saveCourseCcie(student);
                }
            }

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

                learnMapper.updateTypeToLearned(studentId, 1, startUnit, endUnit);
                capacityPictureMapper.deleteByStudentIdAndUnitId(studentId, startUnit, endUnit);
                capacityMemoryMapper.deleteByStudentIdAndUnitId(studentId, startUnit, endUnit);
                capacityWriteMapper.deleteByStudentIdAndUnitId(studentId, startUnit, endUnit);
                capacityListenMapper.deleteByStudentIdAndUnitId(studentId, startUnit, endUnit);

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
