package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.StudyFlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
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
    private StudentUnitMapper studentUnitMapper;

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
    
    @Resource
    private DurationMapper durationMapper;

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
                return openNextUnitAndReturn(courseId, unitId, session, student, studyFlow);
            } else if (Objects.equals(-1, studyFlow.getNextTrueFlow())) {
                // 进入流程2
                this.unlockNextUnit(student, courseId, unitId, session);
                return toAnotherFlow(student, 24);
            } else if (Objects.equals(-3, studyFlow.getNextTrueFlow())) {
                // 进入流程1
                this.unlockNextUnit(student, courseId, unitId, session);
                return toAnotherFlow(student, 11);
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
        Long unitId = student.getUnitId();
        Long studentId = student.getId();

        learnMapper.deleteByStudentIdAndUnitId(studentId, unitId);
        capacityPictureMapper.deleteByStudentIdAndUnitId(studentId, unitId);
        capacityMemoryMapper.deleteByStudentIdAndUnitId(studentId, unitId);
        capacityWriteMapper.deleteByStudentIdAndUnitId(studentId, unitId);
        capacityListenMapper.deleteByStudentIdAndUnitId(studentId, unitId);
    }

    /**
     * 进入其他流程
     *
     * @param student
     * @param flowId  其他流程初始流程id
     * @return
     */
    private ServerResponse<Object> toAnotherFlow(Student student, int flowId) {
        studentFlowMapper.updateFlowByStudentId(student.getId(), flowId);
        StudyFlow byPrimaryKey = studyFlowMapper.selectById(flowId);
        byPrimaryKey.setCourseId(student.getCourseId());
        byPrimaryKey.setUnitId(student.getUnitId());
        byPrimaryKey.setCourseName(student.getCourseName());
        byPrimaryKey.setUnitName(student.getUnitName());
        return ServerResponse.createBySuccess("true", byPrimaryKey);
    }

    /**
     * 开启下一单元并且返回需要学习的流程信息
     *
     * @param courseId
     * @param unitId
     * @param session
     * @param student
     * @param studyFlow
     * @return
     */
    private ServerResponse<Object> openNextUnitAndReturn(Long courseId, Long unitId, HttpSession session, Student student,
                                                         StudyFlow studyFlow) {
        // 开启下一单元
        unlockNextUnit(student, courseId, unitId, session);

        // 获取流程信息
        if ("流程1".equals(studyFlow.getFlowName())) {
            return this.toAnotherFlow(student, 11);
        } else {
            return this.toAnotherFlow(student, 24);
        }
    }

    /**
     * 删除精华版学习产生的数据, 重新学习精华版
     * @param studentId
     * @param courseId
     *//*
    private void deleteEssenceVersionLearn(long studentId, long courseId) {
        // 删除learn, test_record, 7个记忆追踪数据
        LearnExample learnExampler = new LearnExample();
        learnExampler.createCriteria().andCourseIdEqualTo(courseId).andStudentIdEqualTo(studentId);
        learnMapper.deleteByExample(learnExampler);

        TestRecordExample testRecordExampler = new TestRecordExample();
        testRecordExampler.createCriteria().andCourseIdEqualTo(courseId).andStudentIdEqualTo(studentId);
        testRecordMapper.deleteByExample(testRecordExampler);

        for (int model = 0; model < 7; model++) {
            capacityPictureMapper.deleteAllMemory(studentId, courseId, model);
        }

    }

    *//**
     * 升级规则, 精华版课程学完才进行升级规则
     * @param studentId
     * @param courseId
     * @param unitId
     * @param st
     * @return 1 = 升级, 0不升级
     *//*
    private int upgradeRules(long studentId, long courseId, long unitId, Student st, HttpSession session) {
        if(st.getVersion().contains("小学精华版") || st.getVersion().contains("初一精华版") ||
                st.getVersion().contains("中考精华版") || st.getVersion().contains("高一精华版") ||
                st.getVersion().contains("高考精华版")){
            // 1.判断是否该课程下的最后一单元
            long maxUnitId = unitMapper.getMaxUnitIdByCourseId(courseId);
            if(maxUnitId != unitId){
                return 0;
            }

            // 2.判断进度
            // 课程下已学单词数量
            int countWord = learnMapper.labelWordsQuantityByStudentIdAndCourseId(studentId, courseId);
            // 课程下已掌握单词
            int graspWord = learnMapper.labelGraspWordsByStudentIdAndCourseId(studentId, courseId);
            int word = countWord == 0 ? 0 : graspWord / countWord * 100;
            // 升级
            if(word >= 80){
                // 1.查询普通版最后一次学习的单元id,
                long finallyUnitId = studentUnitMapper.getFinallyLearnUnitByStudentIdAndCourseId(studentId, courseId);

                // 2.普通版执行解锁下一单元方法, 普通版的转精华版的时候普通版没有开启下一单元,所以需要在这里开启学习一下单元
                unlockNextUnit(st, courseId, finallyUnitId, session);

                // 3.删掉当前精华版学习数据
                deleteEssenceVersionLearn(studentId, courseId);
                return 1;
            }else{
                return 0;
            }
        }

        return 0;
    }

    *//**
     * 降级规则, 并把对应的精华版本更新到学生表, 根据课程判断掌握度进行降级
     *//*
    private int flowDropRules(long studentId, long courseId, Student student, HttpSession session){
        // 1.查询是否到达降级条件
        // 已学单元
        int learnUnit = learnMapper.countDISTINCTUnit(studentId);
        // 单元总数
        int countUnit = unitMapper.selectMaxUnitIndexByCourseId(courseId);
        int duty =  learnUnit / countUnit * 100;
        // 已学本课程下30%的单元
        if(duty >= 30){
            // 课程下已学单词数量
            int countWord = learnMapper.labelWordsQuantityByStudentIdAndCourseId(studentId, courseId);
            // 课程下已掌握单词
            int graspWord = learnMapper.labelGraspWordsByStudentIdAndCourseId(studentId, courseId);
            int word = countWord == 0 ? 0 : graspWord / countWord * 100;
            // 单词掌握率低于40%
            if(word < 40){
                // 降级!
                //Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
                String grade = student.getGrade(); // 年级
                String version = student.getVersion(); // 版本

                // 学生普通版本名
                String commonVersion = null;
                if(version.contains("-")){
                    commonVersion = version.substring(0, version.indexOf("-"));
                }else{
                    commonVersion = version;
                }

                // 记录需要修改的精华版本的版本名
                String creamVersion = commonVersion;

                if(grade.equals("八年级") || grade.equals("初二")){
                    if(version.contains("小学精华版")){
                        return 0;
                    }else if(!version.contains("初一精华版")){
                        // 把普通版本改为初一精华版
                        creamVersion += "-初一精华版";
                    }else{
                        // 把普通版本改为小学精华版
                        creamVersion += "-小学精华版";
                    }
                }else if(grade.equals("九年级") || grade.equals("初三")){
                    if(version.contains("中考精华版")){
                        return 0;
                    }else{
                        // 把普通版本改为中考精华版
                        creamVersion += "-中考精华版";
                    }
                }else if(grade.equals("高二")){
                    if(version.contains("中考精华版")){
                        return 0;
                    }else if(!version.contains("高一精华版")){
                        // 把普通版本改为高一精华版
                        creamVersion += "-高一精华版";
                    }else{
                        // 把普通版本改为中考精华版
                        creamVersion += "-中考精华版";
                    }
                }else if(grade.equals("高三")){
                    if (version.contains("高考精华版")){
                        return 0;
                    }else{
                        // 把普通版本改为高考精华版
                        creamVersion += "-高考精华版";
                    }
                }

                // 根据精华版本名查询课程信息  id, version, course_name
                Map course = courseMapper.getCourseByCreamVersionName(creamVersion);
                // 查询精华版第一个单元id和单元名  id, unit_name
                Map unit = unitMapper.getLimitOneByCourseId((long)course.get("id"));

                // 更新学生信息
                Student stu = new Student();
                stu.setId(studentId);
                stu.setVersion(course.get("version").toString());

                stu.setCourseId((long)course.get("id"));
                stu.setUnitId((long)unit.get("id"));
                stu.setUnitName(unit.get("unit_name").toString());
                stu.setCourseName(course.get("course_name").toString());

                stu.setSentenceCourseId((long)course.get("id"));
                stu.setSentenceCourseName(course.get("course_name").toString());
                stu.setSentenceUnitId(Integer.parseInt(unit.get("id")+""));
                stu.setSentenceUnitName(unit.get("unit_name").toString());

                studentMapper.updateByPrimaryKeySelective(stu);

                // 更新学生session
                Student student1 = studentMapper.selectByPrimaryKey(studentId);
                session.setAttribute(UserConstant.CURRENT_STUDENT, student1);
                return 1;
            }
        }
        return 0;
    }

    *//**
     * 当前流程学习完毕后开启下一单元
     *
     * @param courseId 课程id
     * @param unitId   单元id（当前单元闯关测试的单元id）
     * @return
     */
    private void unlockNextUnit(Student student, Long courseId, Long unitId, HttpSession session) {
        // 查看当前课程的最大单元和当前单元的index
        Integer maxUnitIndex = unitMapper.selectMaxUnitIndexByCourseId(courseId);
        Integer currentUnitIndex = unitMapper.selectCurrentUnitIndexByUnitId(unitId);
        Long nextUnitId = null;
        if (currentUnitIndex < maxUnitIndex) {
            // 查询 当前单元index+1 的单元id
            Integer nextUnitIndex = currentUnitIndex + 1;
            nextUnitId = unitMapper.selectNextUnitIndexByCourseId(courseId, nextUnitIndex);
            Unit unit = unitMapper.selectByPrimaryKey(nextUnitId);
            // 根据学生id，课程id和下一个单元id开启下个单元
            studentUnitMapper.updateStatus(student.getId(), courseId, nextUnitId);

            // 更新学生信息
            // 单词类
            student.setUnitId(nextUnitId);
            student.setUnitName(unit.getUnitName());
            student.setSentenceUnitName(unit.getUnitName());
            student.setSentenceUnitId(Integer.valueOf(nextUnitId.toString()));
            studentMapper.updateByPrimaryKeySelective(student);

        } else {
            // 本课程已学习完
            // 查找学生可学习的下一课程
            List<StudentUnit> studentUnits = studentUnitMapper.selectNextCourse(student, courseId);
            if (studentUnits.size() != 0) {
                StudentUnit studentUnit = studentUnits.get(0);
                List<Unit> units = unitMapper.selectUnitsByCourseId(studentUnit.getCourseId());
                Unit unit;
                if (units.size() > 0) {
                    unit = units.get(0);
                    nextUnitId = unit.getId();
                    // 更新学生信息
                    // 单词类
                    student.setUnitId(unit.getId());
                    student.setUnitName(unit.getUnitName());
                    student.setSentenceUnitName(unit.getUnitName());
                    student.setSentenceUnitId(Integer.valueOf(unit.getId().toString()));
                    studentMapper.updateByPrimaryKeySelective(student);
                }
            }
        }
        saveOpenUnitLog(student, unitId, nextUnitId);

        // 更新学生session
        Student student1 = studentMapper.selectByPrimaryKey(student.getId());
        session.setAttribute(UserConstant.CURRENT_STUDENT, student1);
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
