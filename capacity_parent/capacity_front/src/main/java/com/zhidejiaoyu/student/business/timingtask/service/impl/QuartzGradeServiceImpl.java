package com.zhidejiaoyu.student.business.timingtask.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.constant.FlowConstant;
import com.zhidejiaoyu.common.constant.GradeNameConstant;
import com.zhidejiaoyu.common.mapper.SchoolTimeMapper;
import com.zhidejiaoyu.common.mapper.StudentExpansionMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.StudentStudyPlanNewMapper;
import com.zhidejiaoyu.common.pojo.SchoolTime;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentExpansion;
import com.zhidejiaoyu.common.pojo.StudentStudyPlanNew;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import com.zhidejiaoyu.common.utils.grade.GradeUtil;
import com.zhidejiaoyu.common.utils.study.PriorityUtil;
import com.zhidejiaoyu.student.business.feignclient.course.VocabularyFeignClient;
import com.zhidejiaoyu.student.business.service.StudentExpansionService;
import com.zhidejiaoyu.student.business.student.service.StudentStudyPlanNewService;
import com.zhidejiaoyu.student.business.timingtask.service.BaseQuartzService;
import com.zhidejiaoyu.student.business.timingtask.service.QuartzGradeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 年级定时任务
 *
 * @author wuchenxi
 * @date 2020-09-02 16:03:03
 */
@Slf4j
@Service
public class QuartzGradeServiceImpl extends ServiceImpl<StudentMapper, Student> implements QuartzGradeService, BaseQuartzService {

    @Value("${quartz.port}")
    private int port;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private StudentStudyPlanNewMapper studentStudyPlanNewMapper;

    @Resource
    private SchoolTimeMapper schoolTimeMapper;

    @Resource
    private VocabularyFeignClient vocabularyFeignClient;

    @Resource
    private StudentStudyPlanNewService studentStudyPlanNewService;

    @Resource
    private StudentExpansionMapper studentExpansionMapper;

    @Resource
    private StudentExpansionService studentExpansionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 59 23 31 8 ? ")
    public void updateGrade() {
        if (checkPort(port)) {
            return;
        }

        log.info("开始升级学生年级...");
        List<StudentStudyPlanNew> studentStudyPlanNews = studentStudyPlanNewMapper.selectList(new LambdaQueryWrapper<StudentStudyPlanNew>()
                .groupBy(StudentStudyPlanNew::getStudentId));

        // 查询已经进行过摸底测试的学生
        List<Student> students = studentMapper.selectList(new LambdaQueryWrapper<Student>()
                // todo:临时去掉安仁的学生
                .notLike(Student::getSchoolName, "安仁")
                .in(Student::getId, studentStudyPlanNews.stream()
                        .map(StudentStudyPlanNew::getStudentId)
                        .collect(Collectors.toList())));

        List<Student> allStudents = studentMapper.selectList(new LambdaQueryWrapper<Student>()
                // todo:临时去掉安仁的学生
                .notLike(Student::getSchoolName, "安仁"));

        Map<Long, List<Student>> studentIdMap = students.stream().collect(Collectors.groupingBy(Student::getId));

        List<Student> updateStudentList = new ArrayList<>();
        List<StudentExpansion> studentExpansions = new ArrayList<>();
        allStudents.forEach(student -> {
            String grade = student.getGrade();
            String nextGrade = GradeUtil.getNextGrade(grade);
            if (studentIdMap.containsKey(student.getId())) {
                this.updateStudentStudyPlanNew(student, nextGrade);
            }

            if (Objects.equals(nextGrade, GradeNameConstant.SENIOR_ONE)){
                StudentExpansion studentExpansion = studentExpansionMapper.selectByStudentId(student.getId());
                studentExpansion.setPhase("高中");
                studentExpansions.add(studentExpansion);
            }

            if (Objects.equals(nextGrade, GradeNameConstant.SEVENTH_GRADE)) {
                StudentExpansion studentExpansion = studentExpansionMapper.selectByStudentId(student.getId());
                studentExpansion.setPhase("初中");
                studentExpansions.add(studentExpansion);
            }

            student.setGrade(nextGrade);
            log.info("学生{}-{}-{}由{}升级到{}", student.getId(), student.getAccount(), student.getStudentName(), grade, student.getGrade());
            updateStudentList.add(student);
        });

        if (CollectionUtils.isNotEmpty(studentExpansions)) {
            studentExpansionService.updateBatchById(studentExpansions);
        }

        this.updateBatchById(updateStudentList);
        log.info("学生年级升级完成。");
    }

    private void updateStudentStudyPlanNew(Student student, String nextGrade) {

        SchoolTime schoolTime = schoolTimeMapper.selectMinUnitIdByUserIdAndTypeAndGrade(student.getId(), 2, nextGrade);
        if (schoolTime != null) {
            this.saveStudentStudyPlanNew(student, nextGrade, schoolTime);
            return;
        }

        Integer schoolAdminId = TeacherInfoUtil.getSchoolAdminId(student);
        schoolTime = schoolTimeMapper.selectMinUnitIdByUserIdAndTypeAndGrade((long) schoolAdminId, 1, nextGrade);
        if (schoolTime != null) {
            this.saveStudentStudyPlanNew(student, nextGrade, schoolTime);
            return;
        }

        schoolTime = schoolTimeMapper.selectMinUnitIdByUserIdAndTypeAndGrade(1L, 1, nextGrade);
        if (schoolTime != null) {
            this.saveStudentStudyPlanNew(student, nextGrade, schoolTime);
        }
    }

    private void saveStudentStudyPlanNew(Student student, String nextGrade, SchoolTime schoolTime) {
        int timePriority = PriorityUtil.BASE_TIME_PRIORITY;
        int basePriority = PriorityUtil.getBasePriority(nextGrade, nextGrade, GradeNameConstant.VOLUME_1);

        StudentStudyPlanNew.StudentStudyPlanNewBuilder studentStudyPlanNewBuilder = StudentStudyPlanNew.builder()
                .studentId(student.getId())
                .complete(1)
                .courseId(schoolTime.getCourseId())
                .currentStudyCount(1)
                .errorLevel(0)
                .group(0)
                .timeLevel(PriorityUtil.BASE_TIME_PRIORITY)
                .totalStudyCount(1)
                .updateTime(new Date())
                .unitId(schoolTime.getUnitId());

        long l = vocabularyFeignClient.countByUnitId(schoolTime.getUnitId());

        List<StudentStudyPlanNew> studentStudyPlanNews = new ArrayList<>();
        if (l > 0) {
            // 如果当前单元没有单词，不初始化该单元单词、句型、课文的学习计划
            StudentStudyPlanNew easyStudentStudyPlan = studentStudyPlanNewBuilder
                    .easyOrHard(1)
                    .baseLevel(basePriority)
                    .flowId(FlowConstant.BEFORE_GROUP_GAME_EASY)
                    .finalLevel(basePriority + timePriority)
                    .build();
            studentStudyPlanNews.add(easyStudentStudyPlan);

            StudentStudyPlanNew hardStudentStudyPlan = studentStudyPlanNewBuilder
                    .easyOrHard(2)
                    .baseLevel(basePriority - PriorityUtil.HARD_NUM)
                    .flowId(FlowConstant.BEFORE_GROUP_GAME_HARD)
                    .finalLevel(basePriority - PriorityUtil.HARD_NUM + timePriority)
                    .build();
            studentStudyPlanNews.add(hardStudentStudyPlan);
        }

        StudentStudyPlanNew goldTestStudyPlan = studentStudyPlanNewBuilder
                .easyOrHard(3)
                .baseLevel(basePriority - PriorityUtil.HARD_NUM - PriorityUtil.GOLD_TEST_NUM)
                .flowId(FlowConstant.GOLD_TEST)
                .finalLevel(basePriority - PriorityUtil.HARD_NUM - PriorityUtil.GOLD_TEST_NUM + timePriority)
                .build();
        studentStudyPlanNews.add(goldTestStudyPlan);

        studentStudyPlanNewService.saveBatch(studentStudyPlanNews);
    }

}
