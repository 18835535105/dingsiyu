package com.zhidejiaoyu.student.business.test.service.impl;

import com.github.pagehelper.util.StringUtil;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.award.GoldChange;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.study.PointConstant;
import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.dto.testbeforestudy.GradeAndUnitIdDTO;
import com.zhidejiaoyu.common.dto.testbeforestudy.SaveSubjectsDTO;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.grade.GradeUtil;
import com.zhidejiaoyu.common.utils.grade.LabelUtil;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.pet.PetSayUtil;
import com.zhidejiaoyu.common.utils.pet.PetUrlUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.study.PriorityUtil;
import com.zhidejiaoyu.common.vo.TestResultVo;
import com.zhidejiaoyu.common.vo.testVo.beforestudytest.SubjectsVO;
import com.zhidejiaoyu.student.business.flow.FlowConstant;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.service.impl.TestServiceImpl;
import com.zhidejiaoyu.student.business.test.constant.TestConstant;
import com.zhidejiaoyu.student.business.test.service.BeforeStudyTestService;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wuchenxi
 */
@Slf4j
@Service
public class BeforeStudyTestServiceImpl extends BaseServiceImpl<StudentStudyPlanNewMapper, StudentStudyPlanNew> implements BeforeStudyTestService {

    @Resource
    private SchoolTimeMapper schoolTimeMapper;

    @Resource
    private CourseNewMapper courseNewMapper;

    @Resource
    private UnitNewMapper unitNewMapper;

    @Resource
    private VocabularyMapper vocabularyMapper;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private PetSayUtil petSayUtil;

    @Resource
    private TestRecordMapper testRecordMapper;

    @Resource
    private RedisOpt redisOpt;

    @Resource
    private StudentExpansionMapper studentExpansionMapper;

    @Resource
    private LearnNewMapper learnNewMapper;

    @Resource
    private StudentFlowNewMapper studentFlowNewMapper;

    @Resource
    private StudentStudyPlanNewMapper studentStudyPlanNewMapper;

    @Override
    public ServerResponse<List<SubjectsVO>> getSubjects() {

        HttpSession httpSession = HttpUtil.getHttpSession();
        httpSession.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        Student student = super.getStudent(httpSession);
        StudentExpansion studentExpansion = studentExpansionMapper.selectByStudentId(student.getId());

        // 当前月份
        DateTime dateTime = new DateTime();
        int monthOfYear = dateTime.getMonthOfYear();
        // 当前月的第几周
        int weekOfMonth = DateUtil.getWeekOfMonth(dateTime.toDate());

        List<String> gradeList = GradeUtil.smallThanCurrentAllPhase(studentExpansion.getPhase(), student.getGrade());

        ServerResponse<List<SubjectsVO>> response = this.getStudentPlanResult(student, monthOfYear, weekOfMonth, gradeList);
        if (response != null) {
            return response;
        }

        response = this.getSchoolPlanResult(student, monthOfYear, weekOfMonth, gradeList);
        if (response != null) {
            return response;
        }
        response = this.getBasePlanResult(monthOfYear, weekOfMonth, gradeList);
        if (response != null) {
            return response;
        }

        throw new ServiceException(500, "未查询到校区时间表！");
    }

    /**
     * 获取总部的测试题
     *
     * @param monthOfYear
     * @param gradeList
     * @return
     */
    public ServerResponse<List<SubjectsVO>> getBasePlanResult(int monthOfYear, int weekOfMonth, List<String> gradeList) {
        SchoolTime schoolTime;
        // 当前月中小于或等于当前周的最大周数据
        schoolTime = schoolTimeMapper.selectByUserIdAndTypeAndMonthAndWeek(1L, 1, monthOfYear, weekOfMonth, gradeList);
        if (schoolTime != null) {
            return this.getSubjectsResult(schoolTime);
        }

        // 小于或等于当前月的最大月、最大周数据
        schoolTime = schoolTimeMapper.selectByUserIdAndTypeAndMonthAndWeek(1L, 1, monthOfYear, null, gradeList);
        if (schoolTime != null) {
            return this.getSubjectsResult(schoolTime);
        }

        // 查看学生最大月、最大周数据
        schoolTime = schoolTimeMapper.selectByUserIdAndTypeAndMonthAndWeek(1L, 1, null, null, gradeList);
        if (schoolTime != null) {
            return this.getSubjectsResult(schoolTime);
        }
        return null;
    }

    /**
     * 获取学校的测试题
     *
     * @param student
     * @param monthOfYear
     * @param weekOfMonth
     * @param gradeList
     * @return
     */
    public ServerResponse<List<SubjectsVO>> getSchoolPlanResult(Student student, int monthOfYear, int weekOfMonth, List<String> gradeList) {
        SchoolTime schoolTime;
        Integer schoolAdminId = TeacherInfoUtil.getSchoolAdminId(student);

        if (schoolAdminId != null) {
            // 当前月中小于或等于当前周的最大周数据
            schoolTime = schoolTimeMapper.selectByUserIdAndTypeAndMonthAndWeek((long) schoolAdminId, 1, monthOfYear, weekOfMonth, gradeList);
            if (schoolTime != null) {
                return this.getSubjectsResult(schoolTime);
            }

            // 小于或等于当前月的最大月、最大周数据
            schoolTime = schoolTimeMapper.selectByUserIdAndTypeAndMonthAndWeek((long) schoolAdminId, 1, monthOfYear, null, gradeList);
            if (schoolTime != null) {
                return this.getSubjectsResult(schoolTime);
            }

            // 查看学生最大月、最大周数据
            schoolTime = schoolTimeMapper.selectByUserIdAndTypeAndMonthAndWeek((long) schoolAdminId, 1, null, null, gradeList);
            if (schoolTime != null) {
                return this.getSubjectsResult(schoolTime);
            }
        }
        return null;
    }

    /**
     * 获取学生的测试题
     *
     * @param student
     * @param monthOfYear
     * @param weekOfMonth
     * @param gradeList
     * @return
     */
    public ServerResponse<List<SubjectsVO>> getStudentPlanResult(Student student, int monthOfYear, int weekOfMonth, List<String> gradeList) {
        // 当前月中小于或等于当前周的最大周数据
        SchoolTime schoolTime = schoolTimeMapper.selectByUserIdAndTypeAndMonthAndWeek(student.getId(), 2, monthOfYear, weekOfMonth, gradeList);
        if (schoolTime != null) {
            return this.getSubjectsResult(schoolTime);
        }

        // 小于或等于当前月的最大月、最大周数据
        schoolTime = schoolTimeMapper.selectByUserIdAndTypeAndMonthAndWeek(student.getId(), 2, monthOfYear, null, gradeList);
        if (schoolTime != null) {
            return this.getSubjectsResult(schoolTime);
        }

        // 查看学生最大月、最大周数据
        schoolTime = schoolTimeMapper.selectByUserIdAndTypeAndMonthAndWeek(student.getId(), 2, null, null, gradeList);
        if (schoolTime != null) {
            return this.getSubjectsResult(schoolTime);
        }
        return null;
    }

    @Override
    @GoldChangeAnnotation
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> saveSubjects(SaveSubjectsDTO dto) {
        HttpSession httpSession = HttpUtil.getHttpSession();
        Student student = super.getStudent(httpSession);

        if (redisOpt.isRepeatSubmit(student.getId(), super.getStartTime())) {
            throw new ServiceException("重复提交摸底测试记录！");
        }

        // 测试结果
        List<SaveSubjectsDTO.Result> resultList = dto.getResultList();

        // 推送课程
        this.pushCourse(resultList, student);

        // 奖励的金币数
        Integer point = dto.getPoint();
        int awardGold = GoldChange.getWordUnitTestGold(student, point);
        // 奖励的能量数
        int energy = super.getEnergy(student, point, 0);

        student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), awardGold));
        studentMapper.updateById(student);

        TestResultVo vo = new TestResultVo();

        TestRecord testRecord = this.saveTestRecord(student, dto, awardGold);
        String msg = TestServiceImpl.getTestMessage(student, vo, testRecord, PointConstant.FIFTY, petSayUtil);

        vo.setMsg(msg);
        vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, point, GenreConstant.UNIT_TEST));
        vo.setGold(awardGold);
        vo.setEnergy(energy);

        super.getLevel(httpSession);

        httpSession.removeAttribute(TimeConstant.BEGIN_START_TIME);
        return ServerResponse.createBySuccess(vo);
    }

    /**
     * 保存测试记录
     *
     * @param student
     * @param dto
     * @param awardGold
     * @return
     */
    public TestRecord saveTestRecord(Student student, SaveSubjectsDTO dto, int awardGold) {
        TestRecord testRecord = new TestRecord();
        testRecord.setAwardGold(awardGold);
        testRecord.setGenre(GenreConstant.TEST_BEFORE_STUDY);
        testRecord.setStudyModel(GenreConstant.TEST_BEFORE_STUDY);

        List<SaveSubjectsDTO.Result> resultList = dto.getResultList();
        testRecord.setQuantity(resultList.size() * 3);
        testRecord.setStudentId(student.getId());
        int errorCount = resultList.stream().mapToInt(SaveSubjectsDTO.Result::getErrorCount).sum();
        testRecord.setRightCount(testRecord.getQuantity() - errorCount);
        testRecord.setErrorCount(errorCount);
        testRecord.setTestStartTime(super.getStartTime());
        testRecord.setTestEndTime(new Date());
        testRecord.setPoint(dto.getPoint());
        Long unitId = resultList.get(0).getUnitId();
        testRecord.setUnitId(unitId);
        testRecord.setCourseId(unitNewMapper.selectById(unitId).getCourseId());
        StudentExpansion studentExpansion = studentExpansionMapper.selectByStudentId(student.getId());
        if (studentExpansion == null) {
            String phase = courseNewMapper.selectPhaseByUnitId(unitId);
            testRecord.setExplain(phase);
        } else {
            testRecord.setExplain(studentExpansion.getPhase());
        }
        testRecordMapper.insert(testRecord);
        return testRecord;
    }

    /**
     * 推送课程
     *
     * @param resultList
     * @param student
     * @return 最终优先级最大的记录
     */
    public void pushCourse(List<SaveSubjectsDTO.Result> resultList, Student student) {
        Map<Long, List<SaveSubjectsDTO.Result>> unitIdMap = resultList.stream()
                .collect(Collectors.groupingBy(SaveSubjectsDTO.Result::getUnitId));

        // 学生可学习的所有单元id
        List<Long> unitIds = (List<Long>) HttpUtil.getHttpSession().getAttribute(TestConstant.TEST_BEFORE_STUDY_UNIT_IDS);
        if (unitIds == null) {
            throw new ServiceException(500, "学生没有可学习的单元！");
        }

        // 匹配单元对应的年级
        Map<String, Map<Long, String>> unitIdAndGradeAndLabel = this.getUnitIdAndGrade(unitIds);
        Map<Long, String> unitIdAndGrade = unitIdAndGradeAndLabel.get("gradeMap");
        Map<Long, String> unitIdAndLabel = unitIdAndGradeAndLabel.get("labelMap");

        // 匹配单元对应的课程id
        Map<Long, Long> unitIdAndCourseId = this.getUnitIdAndCourseId(unitIds);

        List<StudentStudyPlanNew> studentStudyPlanNews = new ArrayList<>(resultList.size());

        String grade = student.getGrade();
        Date updateTime = new Date();
        int timePriority = PriorityUtil.BASE_TIME_PRIORITY;
        Set<String> phaseSet = new HashSet<>();
        for (Long unitId : unitIds) {

            String unitGrade = unitIdAndGrade.get(unitId);
            String unitLabel = unitIdAndLabel.get(unitId);
            timePriority = this.getTimePriority(timePriority, phaseSet, unitGrade);
            int basePriority = this.getBasePriority(unitIdMap, unitGrade, unitLabel, grade, unitId);
            int errorPriority = this.getErrorPriority(unitIdMap, unitGrade, grade, unitId, basePriority);

            StudentStudyPlanNew.StudentStudyPlanNewBuilder studentStudyPlanNewBuilder = StudentStudyPlanNew.builder()
                    .studentId(student.getId())
                    .complete(1)
                    .courseId(unitIdAndCourseId.get(unitId))
                    .currentStudyCount(1)
                    .errorLevel(errorPriority)
                    .group(0)
                    .timeLevel(timePriority)
                    .totalStudyCount(1)
                    .updateTime(updateTime)
                    .unitId(unitId);

            StudentStudyPlanNew easyStudentStudyPlan = studentStudyPlanNewBuilder
                    .easyOrHard(1)
                    .baseLevel(basePriority)
                    .flowId(FlowConstant.BEFORE_GROUP_GAME_EASY)
                    .finalLevel(basePriority + errorPriority + timePriority)
                    .build();
            studentStudyPlanNews.add(easyStudentStudyPlan);

            StudentStudyPlanNew hardStudentStudyPlan = studentStudyPlanNewBuilder
                    .easyOrHard(2)
                    .baseLevel(basePriority - PriorityUtil.HARD_NUM)
                    .flowId(FlowConstant.BEFORE_GROUP_GAME_HARD)
                    .finalLevel(basePriority - PriorityUtil.HARD_NUM + errorPriority + timePriority)
                    .build();
            studentStudyPlanNews.add(hardStudentStudyPlan);
        }

        this.saveBatch(studentStudyPlanNews);

        // 初始化学生学习流程
        StudentStudyPlanNew maxStudentStudyPlanNew = studentStudyPlanNewMapper.selectMaxFinalLevelByLimit(student.getId(), 1).get(0);

        // 初始化学习内容
        LearnNew learnNew = this.initLearn(student, maxStudentStudyPlanNew);

        this.initStudentFlow(student, maxStudentStudyPlanNew, learnNew);
    }

    public LearnNew initLearn(Student student, StudentStudyPlanNew maxStudentStudyPlanNew) {
        LearnNew learnNew = LearnNew.builder()
                .courseId(maxStudentStudyPlanNew.getCourseId())
                .updateTime(new Date())
                .unitId(maxStudentStudyPlanNew.getUnitId())
                .studentId(student.getId())
                .group(1)
                .easyOrHard(maxStudentStudyPlanNew.getEasyOrHard())
                .modelType(1)
                .build();
        learnNewMapper.insert(learnNew);
        return learnNew;
    }

    public void initStudentFlow(Student student, StudentStudyPlanNew maxStudentStudyPlanNew, LearnNew learnNew) {
        studentFlowNewMapper.insert(StudentFlowNew.builder()
                .currentFlowId(maxStudentStudyPlanNew.getFlowId())
                .learnId(learnNew.getId())
                .studentId(student.getId())
                .updateTime(new Date())
                .type(1)
                .build());
    }

    /**
     * 获取时间优先级
     *
     * @param timePriority
     * @param phaseSet
     * @param unitGrade
     * @return
     */
    public int getTimePriority(int timePriority, Set<String> phaseSet, String unitGrade) {
        if (!phaseSet.contains(unitGrade)) {
            phaseSet.add(unitGrade);
            return PriorityUtil.BASE_TIME_PRIORITY;
        } else {
            return timePriority + PriorityUtil.BASE_TIME_PRIORITY;
        }
    }

    public int getBasePriority(Map<Long, List<SaveSubjectsDTO.Result>> unitIdMap, String unitGrade, String unitLabel,
                               String grade, Long unitId) {
        if (!unitIdMap.containsKey(unitId)) {
            return PriorityUtil.getBasePriority(grade, unitGrade, unitLabel, 0);
        }
        // 答错个数
        long errorCount = unitIdMap.get(unitId).get(0).getErrorCount();
        return PriorityUtil.getBasePriority(grade, unitGrade, unitLabel, (int) errorCount);
    }

    /**
     * 获取变化优先级
     *
     * @param unitIdMap
     * @param unitGrade    测试题所在年级
     * @param grade        学生当前所在年级
     * @param unitId
     * @param basePriority
     * @return
     */
    public int getErrorPriority(Map<Long, List<SaveSubjectsDTO.Result>> unitIdMap, String unitGrade,
                                String grade, Long unitId, int basePriority) {
        if (!unitIdMap.containsKey(unitId)) {
            return PriorityUtil.getErrorPriority(0, basePriority, grade, unitGrade);
        }
        // 答错个数
        long errorCount = unitIdMap.get(unitId).get(0).getErrorCount();
        return PriorityUtil.getErrorPriority((int) errorCount, basePriority, grade, unitGrade);
    }

    /**
     * 匹配单元对应的课程id
     *
     * @param unitIds
     * @return
     */
    public Map<Long, Long> getUnitIdAndCourseId(List<Long> unitIds) {
        List<UnitNew> unitNews = unitNewMapper.selectBatchIds(unitIds);
        Map<Long, Long> unitIdAndCourseId = new HashMap<>(16);
        unitNews.forEach(unitNew -> unitIdAndCourseId.put(unitNew.getId(), unitNew.getCourseId()));
        return unitIdAndCourseId;
    }

    /**
     * 匹配单元对应的年级
     *
     * @param unitIds
     * @return key:labelMap->单元id对应的上下册；gradeMap->单元id对应的年级
     * value:key—->当前单元id；value：当前单元对应的年级或者上下册
     */
    public Map<String, Map<Long, String>> getUnitIdAndGrade(List<Long> unitIds) {
        List<GradeAndUnitIdDTO> gradeAndUnitIdDTOList = courseNewMapper.selectGradeAndLabelByUnitIds(unitIds);

        Map<String, Map<Long, String>> unitIdAndGradeAndLabel = new HashMap<>(16);

        Map<Long, String> unitIdAndGrade = new HashMap<>(16);
        Map<Long, String> unitIdAndLabel = new HashMap<>(16);

        gradeAndUnitIdDTOList.forEach(gradeAndUnitIdDTO -> {
            unitIdAndGrade.put(gradeAndUnitIdDTO.getUnitId(),
                    StringUtil.isNotEmpty(gradeAndUnitIdDTO.getGradeExt()) ? gradeAndUnitIdDTO.getGradeExt() : gradeAndUnitIdDTO.getGrade());

            unitIdAndLabel.put(gradeAndUnitIdDTO.getUnitId(),
                    StringUtils.isNotEmpty(gradeAndUnitIdDTO.getLabelExt()) ? gradeAndUnitIdDTO.getLabelExt() : gradeAndUnitIdDTO.getLabel());
        });
        unitIdAndGradeAndLabel.put("labelMap", unitIdAndLabel);
        unitIdAndGradeAndLabel.put("gradeMap", unitIdAndGrade);
        return unitIdAndGradeAndLabel;
    }

    /**
     * 获取摸底测试题
     *
     * @param schoolTime
     * @return
     */
    private ServerResponse<List<SubjectsVO>> getSubjectsResult(SchoolTime schoolTime) {
        CourseNew courseNew = courseNewMapper.selectById(schoolTime.getCourseId());
        if (courseNew == null) {
            log.error("未查询到id为[{}]的课程！", schoolTime.getCourseId());
            throw new ServiceException(500, "未查询到课程！");
        }
        List<String> gradeList = GradeUtil.smallThanCurrent(courseNew.getVersion(), schoolTime.getGrade());

        // 查询小于当前年级的所有单元，等于当前年级小于或等于当前单元的所有单元
        List<Long> unitIds = this.getUnitIds(schoolTime, courseNew, gradeList);
        HttpUtil.getHttpSession().setAttribute(TestConstant.TEST_BEFORE_STUDY_UNIT_IDS, unitIds);

        // 取题
        List<SubjectsVO> subjectsVos = vocabularyMapper.selectSubjectsVOByUnitIds(unitIds);
        Map<Long, List<SubjectsVO>> collect = subjectsVos.stream().collect(Collectors.groupingBy(SubjectsVO::getUnitId));
        List<SubjectsVO> result = new ArrayList<>();

        // 每个单元出题数量
        final int maxSize = 3;
        collect.forEach((unitId, subjectVos) -> {
            Collections.shuffle(subjectVos);
            List<SubjectsVO> voList = subjectVos.stream().limit(maxSize).collect(Collectors.toList());
            voList.forEach(vo -> vo.setReadUrl(GetOssFile.getPublicObjectUrl(vo.getReadUrl())));
            result.addAll(voList);
        });
        return ServerResponse.createBySuccess(result);
    }

    /**
     * 获取小于当前版本但前年级的所有单元ID和当前版本当前年级小于或等于当前单元的所有单元id
     *
     * @param schoolTime
     * @param courseNew
     * @param gradeList  小于或者等于当前年级的所有年级集合
     * @return
     */
    public List<Long> getUnitIds(SchoolTime schoolTime, CourseNew courseNew, List<String> gradeList) {
        List<Long> unitIds = new ArrayList<>();
        int size = gradeList.size();
        if (size > 1) {
            List<String> smallGradeList = gradeList.subList(0, size - 1);
            // 当前版本中小于当前年级的所有单元id
            unitIds.addAll(unitNewMapper.selectByGradeListAndVersionAndGrade(courseNew.getVersion(), smallGradeList));
        }
        // 当前版本中等于当前年级小于或者等于当前单元的所有单元id
        unitIds.addAll(this.getUnitIdsLessThanCurrentUnitId(schoolTime.getCourseId(), schoolTime.getUnitId()));

        return unitIds;
    }

    /**
     * 获取当前课程中小于或者等于当前单元的所有单元id
     *
     * @param courseId
     * @param unitId
     * @return
     */
    private List<Long> getUnitIdsLessThanCurrentUnitId(Long courseId, Long unitId) {
        CourseNew courseNew = courseNewMapper.selectById(courseId);
        String label = courseNew.getLabel();
        List<String> lessLabels = LabelUtil.getLessThanCurrentLabel(label);

        // 说明这个课程只有一个标签
        if (lessLabels.size() == 1 && lessLabels.get(0).equals(label)) {
            return unitNewMapper.selectLessOrEqualsCurrentIdByCourseIdAndUnitId(courseId, unitId);
        }

        StringBuilder stringBuilder = new StringBuilder();
        List<String> courseNames = lessLabels.stream().map(lessLabel -> {
            stringBuilder.setLength(0);
            return stringBuilder.append(courseNew.getVersion()).append("(").append(courseNew.getGrade()).append("-").append(lessLabel).append(")").toString();
        }).collect(Collectors.toList());

        List<Long> resultList = unitNewMapper.selectIdsByCourseNames(courseNames);
        resultList.addAll(unitNewMapper.selectLessOrEqualsCurrentIdByCourseIdAndUnitId(courseId, unitId));
        return resultList;
    }


    public static void main(String[] args) {
        // 当前月份
        System.out.println(new DateTime().getMonthOfYear());

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
        System.out.println(weekOfMonth);
    }
}
