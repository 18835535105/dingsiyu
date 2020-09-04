package com.zhidejiaoyu.student.business.index.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhidejiaoyu.common.constant.GradeNameConstant;
import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import com.zhidejiaoyu.common.utils.grade.GradeUtil;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.course.UnitStudyStateVO;
import com.zhidejiaoyu.common.vo.study.video.VideoCourseVO;
import com.zhidejiaoyu.common.vo.study.video.VideoUnitVO;
import com.zhidejiaoyu.student.business.feignclient.center.VideoFeignClient;
import com.zhidejiaoyu.student.business.feignclient.course.CourseFeignClient;
import com.zhidejiaoyu.student.business.feignclient.course.UnitFeignClient;
import com.zhidejiaoyu.student.business.index.dto.UnitInfoDTO;
import com.zhidejiaoyu.student.business.index.service.IndexCourseInfoService;
import com.zhidejiaoyu.student.business.index.vo.course.CourseInfoVO;
import com.zhidejiaoyu.student.business.index.vo.course.CourseVO;
import com.zhidejiaoyu.student.business.index.vo.course.VersionVO;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: wuchenxi
 * @date: 2019/12/27 13:41:41
 */
@Slf4j
@Service
public class IndexCourseInfoServiceImpl extends BaseServiceImpl<CourseConfigMapper, CourseConfig> implements IndexCourseInfoService {


    public static final String COUNT = "count";

    /**
     * 年级、label的映射
     */
    public static final Map<String, String> MAPPING = new HashMap<>(16);

    /**
     * 语法模块type值
     */
    private static final int SYNTAX_MODEL_TYPE = 3;

    /**
     * 视频
     */
    private static final int VIDEO_MODEL_TYPE = 6;

    /**
     * 金币试卷type值
     */
    private static final int GOLD_TEST = 5;

    @Resource
    private CourseConfigMapper courseConfigMapper;

    @Resource
    private LearnHistoryMapper learnHistoryMapper;

    @Resource
    private UnitNewMapper unitNewMapper;

    @Resource
    private TestRecordMapper testRecordMapper;

    private final CourseFeignClient courseFeignClient;

    @Resource
    private LearnNewMapper learnNewMapper;

    @Resource
    private UnitFeignClient unitFeignClient;

    @Resource
    private SchoolTimeMapper schoolTimeMapper;

    @Resource
    private StudentExpansionMapper studentExpansionMapper;

    @Resource
    private VideoFeignClient videoFeignClient;

    static {
        MAPPING.put(GradeNameConstant.FIRST_GRADE, "one");
        MAPPING.put(GradeNameConstant.SECOND_GRADE, "two");
        MAPPING.put(GradeNameConstant.THREE_GRADE, "three");
        MAPPING.put(GradeNameConstant.FOURTH_GRADE, "four");
        MAPPING.put(GradeNameConstant.FIFTH_GRADE, "five");
        MAPPING.put(GradeNameConstant.SIXTH_GRADE, "six");
        MAPPING.put(GradeNameConstant.SEVENTH_GRADE, "seven");
        MAPPING.put(GradeNameConstant.EIGHTH_GRADE, "eight");
        MAPPING.put(GradeNameConstant.NINTH_GRADE, "nine");
        MAPPING.put(GradeNameConstant.SENIOR_ONE, "ten");
        MAPPING.put(GradeNameConstant.SENIOR_TWO, "eleven");
        MAPPING.put(GradeNameConstant.SENIOR_THREE, "twelve");
        MAPPING.put(GradeNameConstant.HIGH, "gaozhong");

        MAPPING.put(GradeNameConstant.VOLUME_1, "up");
        MAPPING.put(GradeNameConstant.VOLUME_2, "down");
        // 全册
//        MAPPING.put(GradeNameConstant.FULL_VOLUME, "quance");
        // 必修
        MAPPING.put(GradeNameConstant.REQUIRED_ONE, "bixiu1");
        MAPPING.put(GradeNameConstant.REQUIRED_TWO, "bixiu2");
        MAPPING.put(GradeNameConstant.REQUIRED_THREE, "bixiu3");
        MAPPING.put(GradeNameConstant.REQUIRED_FOUR, "bixiu4");
        MAPPING.put(GradeNameConstant.REQUIRED_FIVE, "bixiu5");
        // 选修
        MAPPING.put(GradeNameConstant.ELECTIVE_SIX, "xuanxiu6");
        MAPPING.put(GradeNameConstant.ELECTIVE_SEVEN, "xuanxiu7");
        MAPPING.put(GradeNameConstant.ELECTIVE_EIGHT, "xuanxiu8");
    }

    public IndexCourseInfoServiceImpl(CourseFeignClient courseFeignClient) {
        this.courseFeignClient = courseFeignClient;
    }

    @Override
    public ServerResponse<Object> getStudyCourse(Integer type, Long courseId) {
        Student student = super.getStudent(HttpUtil.getHttpSession());

        if (type == VIDEO_MODEL_TYPE) {
            return this.packageVideoCourse(student);
        }

        List<Long> courseIds = this.getCourseIds(type, student);

        if (CollectionUtils.isNotEmpty(courseIds)) {
            StudentExpansion studentExpansion = studentExpansionMapper.selectByStudentId(student.getId());
            // 查询小于当前年级的所有课程
            List<Long> finalCourseIds = new ArrayList<>(courseFeignClient.getIdsByPhasesAndIds(this.getPhaseList(studentExpansion), courseIds));
            // 查询当前学段正常应该学习的课程
            finalCourseIds.addAll(courseFeignClient.getIdsByPhasesAndIds(Collections.singletonList(studentExpansion.getPhase()), courseIds));
            if (CollectionUtils.isNotEmpty(finalCourseIds)) {
                return this.packageCourse(student, finalCourseIds, type, courseId);
            }
        }

        throw new ServiceException("未查询到学生的自由学习课程！");
    }

    /**
     * 封装视频课程数据
     *
     * @param student
     * @return
     */
    private ServerResponse<Object> packageVideoCourse(Student student) {

        List<String> grades = GradeUtil.smallThanCurrentGrade(student.getGrade());

        int month = new DateTime().monthOfYear().get();
        List<CourseVO> currentGrade;
        List<CourseVO> previousGrade;

        if (month < 9) {
            // 显示到下一个年级的上册
            String nextGrade = GradeUtil.getNextGrade(student.getGrade());
            List<VideoCourseVO> videoVos = videoFeignClient.getVideoCourse(grades, nextGrade);
            currentGrade = getCurrentGrade(student, videoVos);

            previousGrade = getPreviousGrade(student, videoVos);
        } else {
            // 显示到当前年级的下册
            List<VideoCourseVO> videoVos = videoFeignClient.getVideoCourse(grades, null);
            currentGrade = getCurrentGrade(student, videoVos);

            previousGrade = getPreviousGrade(student, videoVos);
        }

        return ServerResponse.createBySuccess(CourseInfoVO.builder()
                .currentGrade(currentGrade)
                .previousGrade(previousGrade)
                .versions(null)
                .InGrade(student.getGrade())
                .build());
    }

    private List<CourseVO> getPreviousGrade(Student student, List<VideoCourseVO> videoVos) {
        List<CourseVO> previousGrade;
        previousGrade = videoVos.stream()
                .filter(videoCourseVO -> !Objects.equals(videoCourseVO.getGrade(), student.getGrade()))
                .map(this::packageCourseVO).collect(Collectors.toList());
        return previousGrade;
    }

    private CourseVO packageCourseVO(VideoCourseVO videoCourseVO) {
        CourseVO courseVO = new CourseVO();
        courseVO.setBattle(0);
        courseVO.setCombatProgress(0);
        courseVO.setVideoId(videoCourseVO.getId());
        courseVO.setEnglishGrade(getGradeAndLabelEnglishName(videoCourseVO.getGrade(), videoCourseVO.getLabel()));
        courseVO.setGrade(videoCourseVO.getGrade());
        return courseVO;
    }

    private List<CourseVO> getCurrentGrade(Student student, List<VideoCourseVO> videoVos) {
        List<CourseVO> currentGrade;
        currentGrade = videoVos.stream()
                .filter(videoCourseVO -> Objects.equals(videoCourseVO.getGrade(), student.getGrade()))
                .map(this::packageCourseVO).collect(Collectors.toList());
        return currentGrade;
    }

    private List<Long> getCourseIds(Integer type, Student student) {
        Integer schoolAdminId = TeacherInfoUtil.getSchoolAdminId(student);

        List<Long> courseIds;

        // 判断学校有没有为学生单独配置课程，如果有单独配置，取学生配置的课程和校区配置的自由学习课程
        int count = courseConfigMapper.countByUserIdAndType(student.getId(), 2);
        if (count > 0) {
            if (log.isDebugEnabled()) {
                log.debug("学生有单独配置的自由学习课程！");
            }
            courseIds = courseConfigMapper.selectCourseIdsByUserIdAndType(student.getId(), 2);
            List<Long> configCourseIds = courseConfigMapper.selectCourseIdsByUserIdAndTypeAndOneKeyLearn(schoolAdminId, 1, 2);
            if (CollectionUtils.isNotEmpty(configCourseIds)) {
                if (log.isDebugEnabled()) {
                    log.debug("校区有单独配置的自由学习课程！");
                }
                courseIds.addAll(configCourseIds);
            }
            return courseIds;
        }

        if (log.isDebugEnabled()) {
            log.debug("学生没有单独配置的自由学习课程！");
        }
        // 查询一键学习半年后需要学习的全部课程
        Integer schoolTimeCount = schoolTimeMapper.selectCount(new LambdaQueryWrapper<SchoolTime>().eq(SchoolTime::getUserId, schoolAdminId));
        if (schoolTimeCount == null || schoolTimeCount == 0) {
            // 说明校区没有配置学习时间，查询校区的课程配置
            List<CourseConfig> courseConfigs = courseConfigMapper.selectByUserIdAndType((long) schoolAdminId, 1);
            courseConfigs.addAll(courseConfigMapper.selectByUserIdAndType((long) schoolAdminId, 2));
            courseIds = courseConfigs.stream().filter(courseConfig -> courseConfig.getStudyModel().contains(String.valueOf(type))).map(CourseConfig::getCourseId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(courseIds)) {
                return courseIds;
            }
        }

        if (schoolTimeCount != null && schoolTimeCount > 0) {
            // 校区配置了学习时间
            if (log.isDebugEnabled()) {
                log.debug("校区有单独配置的一键学习课程！");
            }

            courseIds = this.getSchoolTimeCourseIds(schoolAdminId, student.getGrade());
            if (CollectionUtils.isEmpty(courseIds) && Objects.equals(student.getGrade(), GradeNameConstant.SENIOR_THREE)) {
                courseIds = this.getSchoolTimeCourseIds(schoolAdminId, "高二");
            }

            // 查询校区配置的自由学习课程
            List<CourseConfig> courseConfigs = courseConfigMapper.selectList(new LambdaQueryWrapper<CourseConfig>().eq(CourseConfig::getUserId, schoolAdminId)
                    .eq(CourseConfig::getOneKeyLearn, 2));
            if (CollectionUtils.isNotEmpty(courseConfigs)) {
                if (log.isDebugEnabled()) {
                    log.debug("校区有单独配置的自由学习课程！");
                }
                Map<Long, Long> map = new HashMap<>(16);
                courseIds.forEach(id -> map.put(id, id));
                courseIds.addAll(courseConfigs.stream()
                        .filter(courseConfig -> !map.containsKey(courseConfig.getCourseId()))
                        .map(CourseConfig::getCourseId).collect(Collectors.toList()));
            }
            return courseIds;
        }

        if (log.isDebugEnabled()) {
            log.debug("校区没有单独配置的课程！");
        }
        return this.getSchoolTimeCourseIds(1, student.getGrade());
    }

    /**
     * 获取小于当前学段的所有学段
     *
     * @param studentExpansion
     * @return
     */
    private List<String> getPhaseList(StudentExpansion studentExpansion) {
        String phase = studentExpansion.getPhase();

        if (Objects.equals(phase, "高中")) {
            return Arrays.asList("小学", "初中");
        }

        return Collections.singletonList("小学");
    }

    private List<Long> getSchoolTimeCourseIds(Integer userId, String grade) {
        if (Objects.equals(grade, GradeNameConstant.SENIOR_THREE)) {
            int count = schoolTimeMapper.countByGrade(grade);
            if (count == 0) {
                // 没有配置高三的内容，取高一高二所有内容
                List<String> gradeList = GradeUtil.smallThanCurrentGrade(grade);
                List<SchoolTime> schoolTimes = schoolTimeMapper.selectList(new LambdaQueryWrapper<SchoolTime>()
                        .eq(SchoolTime::getUserId, userId)
                        .in(SchoolTime::getGrade, gradeList));
                if (CollectionUtils.isNotEmpty(schoolTimes)) {
                    return schoolTimes.stream().map(SchoolTime::getCourseId).distinct().collect(Collectors.toList());
                }
            }
        }

        int i = new DateTime().monthOfYear().get();
        int month = new DateTime().plusMonths(6).monthOfYear().get();

        List<SchoolTime> schoolTimes = schoolTimeMapper.selectAfterSixMonth(userId, grade, i >= 6 ? 12 : month);

        List<SchoolTime> currentSchoolTimes;
        // 如果当前时间是8月份之后，说明是第一学期，当前学习的是上册的课程
        if (i > 8) {
            List<SchoolTime> schoolTimes1 = Collections.singletonList(schoolTimes.stream()
                    .filter(schoolTime -> Objects.equals(schoolTime.getGrade(), grade))
                    .max((o1, o2) -> (int) (o2.getId() - o1.getId()))
                    .orElse(new SchoolTime()));
            currentSchoolTimes = new ArrayList<>(schoolTimes1);
        } else {
            currentSchoolTimes = new ArrayList<>(schoolTimes);
        }

        SchoolTime schoolTime = schoolTimeMapper.selectNextByUserIdAndId(userId, currentSchoolTimes.stream()
                .max((o1, o2) -> (int) (o1.getId() - o2.getId()))
                .orElse(new SchoolTime()).getId());

        List<String> gradeList = GradeUtil.smallThanCurrentGrade(grade);
        // 查询小于当前年级的所以课程计划
        List<SchoolTime> smallSchoolTimes = schoolTimeMapper.selectSmallThanCurrentGrade(userId, gradeList);
        currentSchoolTimes.addAll(smallSchoolTimes);
        List<Long> courseIds = currentSchoolTimes.stream().map(SchoolTime::getCourseId).distinct().collect(Collectors.toList());

        if (schoolTime == null) {
            return courseIds;
        }

        courseIds.add(schoolTime.getCourseId());

        return courseIds.stream().distinct().collect(Collectors.toList());
    }


    @Override
    public ServerResponse<Object> getUnitInfo(UnitInfoDTO dto) {
        Long studentId = super.getStudentId();
        Integer type = dto.getType();

        if (type == VIDEO_MODEL_TYPE) {
            return this.getVideoUnitInfo(dto);
        }

        Long courseId = Long.parseLong(dto.getCourseId());
        List<UnitStudyStateVO> vos = unitNewMapper.selectIdAndNameByCourseId(courseId, type);
        List<Long> unitIds = vos.stream().map(UnitStudyStateVO::getUnitId).collect(Collectors.toList());

        if (goldTestUnitStudyState(studentId, type, vos, unitIds)) {
            return ServerResponse.createBySuccess(vos);
        }

        List<LearnHistory> histories = learnHistoryMapper.selectByStudentAndUnitIdsAndType(studentId, unitIds, type);
        Map<Long, List<LearnHistory>> collect = histories.stream().collect(Collectors.groupingBy(LearnHistory::getUnitId));

        int modelType = type;
        if (type == 3) {
            modelType = 4;
        } else if (type == 4) {
            modelType = 3;
        }
        List<LearnNew> learnNews = learnNewMapper.selectByStudentIdAndUnitIdsAndModelType(studentId, unitIds, modelType);
        Map<Long, List<LearnNew>> learnNewCollect = learnNews.stream().collect(Collectors.groupingBy(LearnNew::getUnitId));

        if (syntaxUnitStudyState(type, vos, collect, learnNewCollect)) {
            return ServerResponse.createBySuccess(vos);
        }

        ServerResponse<Map<Long, Integer>> maxGroupResponse = unitFeignClient.getMaxGroupByUnitIsdAndType(unitIds, type);
        if (maxGroupResponse.getStatus() != ResponseCode.SUCCESS.getCode()) {
            return ServerResponse.createByError(maxGroupResponse.getStatus(), maxGroupResponse.getMsg());
        }
        Map<Long, Integer> maxGroup = maxGroupResponse.getData();
        vos.forEach(vo -> {
            if (collect.containsKey(vo.getUnitId())) {
                List<LearnHistory> histories1 = collect.get(vo.getUnitId());
                packageEasyState(maxGroup, vo, histories1);

                packageHardSate(maxGroup, vo, histories1);

            } else if (learnNewCollect.containsKey(vo.getUnitId())) {
                // 查看学习记录中是否有当前单元记录
                for (LearnNew learnNew : learnNewCollect.get(vo.getUnitId())) {
                    if (learnNew.getEasyOrHard() == 1) {
                        vo.setEasyState(2);
                    } else {
                        vo.setHardState(2);
                    }
                }
            }
            if (vo.getEasyState() == null) {
                vo.setEasyState(3);
            }
            if (vo.getHardState() == null) {
                vo.setHardState(3);
            }
        });
        return ServerResponse.createBySuccess(vos);
    }

    /**
     * 获取语法单元数据
     *
     * @param dto
     * @return
     */
    private ServerResponse<Object> getVideoUnitInfo(UnitInfoDTO dto) {
        Student student = super.getStudent();

        List<VideoUnitVO> videoUnitVos = videoFeignClient.getVideoUnitInfo(student.getUuid(), dto.getCourseId());
        return ServerResponse.createBySuccess(videoUnitVos);
    }

    private void packageHardSate(Map<Long, Integer> maxGroup, UnitStudyStateVO vo, List<LearnHistory> histories1) {
        Stream<LearnHistory> learnHistoryStream = histories1.stream().filter(learnHistory -> learnHistory.getEasyOrHard() == 2);
        LearnHistory hardHistory = learnHistoryStream
                .max(Comparator.comparing(LearnHistory::getGroup))
                .orElse(new LearnHistory());
        if (Objects.equals(hardHistory.getGroup(), maxGroup.get(vo.getUnitId()))) {
            vo.setHardState(1);
            return;
        }
        long count = histories1.stream().filter(learnHistory -> learnHistory.getEasyOrHard() == 2).count();
        if (count > 0) {
            vo.setHardState(2);
        }
    }

    private void packageEasyState(Map<Long, Integer> maxGroup, UnitStudyStateVO vo, List<LearnHistory> histories1) {
        LearnHistory easyHistory = histories1.stream().filter(learnHistory -> learnHistory.getEasyOrHard() == 1)
                .max(Comparator.comparing(LearnHistory::getGroup))
                .orElse(new LearnHistory());
        if (Objects.equals(easyHistory.getGroup(), maxGroup.get(vo.getUnitId()))) {
            vo.setEasyState(1);
            return;
        }
        long count = histories1.stream().filter(learnHistory -> learnHistory.getEasyOrHard() == 1).count();
        if (count > 0) {
            vo.setEasyState(2);
        }

    }

    /**
     * 语法模块各个单元学习状态
     *
     * @param type
     * @param vos
     * @param collect
     * @param learnNewCollect
     * @return
     */
    private boolean syntaxUnitStudyState(Integer type, List<UnitStudyStateVO> vos, Map<Long, List<LearnHistory>> collect, Map<Long, List<LearnNew>> learnNewCollect) {
        if (type != 3) {
            return false;
        }
        // 语法
        vos.forEach(vo -> {
            if (collect.containsKey(vo.getUnitId())) {
                // 学习历史记录中有的单元说明是学习完的
                List<LearnHistory> histories1 = collect.get(vo.getUnitId());
                histories1.forEach(history1 -> {
                    if (Objects.equals(history1.getEasyOrHard(), 1)) {
                        vo.setEasyState(1);
                    }
                    if (Objects.equals(history1.getEasyOrHard(), 2)) {
                        vo.setHardState(1);
                    }
                });
            } else if (learnNewCollect.containsKey(vo.getUnitId())) {
                // 学习历史中没有的单元，需要去学习表中查看是否正在学习
                List<LearnNew> learnNews1 = learnNewCollect.get(vo.getUnitId());
                learnNews1.forEach(learnNew -> {
                    if (Objects.equals(learnNew.getEasyOrHard(), 1)) {
                        vo.setEasyState(2);
                    }
                    if (Objects.equals(learnNew.getEasyOrHard(), 2)) {
                        vo.setHardState(2);
                    }
                });
            }

            if (vo.getEasyState() == null) {
                vo.setEasyState(3);
            }
            if (vo.getHardState() == null) {
                vo.setHardState(3);
            }
        });

        return true;

    }

    /**
     * 判断金币试卷各个单元学习状态
     *
     * @param studentId
     * @param type
     * @param vos
     * @param unitIds
     * @return
     */
    private boolean goldTestUnitStudyState(Long studentId, Integer type, List<UnitStudyStateVO> vos, List<Long> unitIds) {
        if (type == 5) {
            // 金币试卷
            List<TestRecord> testRecords = testRecordMapper.selectListByUnitIdsAndGenre(unitIds, studentId, GenreConstant.GOLD_TEST);
            Map<Long, List<TestRecord>> collect = testRecords.stream().collect(Collectors.groupingBy(TestRecord::getUnitId));
            for (UnitStudyStateVO unitStudyStateVO : vos) {
                if (collect.containsKey(unitStudyStateVO.getUnitId())) {
                    unitStudyStateVO.setEasyState(1);
                } else {
                    unitStudyStateVO.setEasyState(3);
                }
            }
            return true;
        }
        return false;
    }


    /**
     * 封装响应结果
     *
     * @param student
     * @param courseIds
     * @param type
     * @param courseId  用于查询版本名称
     * @return
     */
    private ServerResponse<Object> packageCourse(Student student, List<Long> courseIds, int type, Long courseId) {

        if (CollectionUtils.isEmpty(courseIds)) {
            log.warn("学生[{}-{}-{}]未查询到可以学习的课程！type=[{}]", student.getId(), student.getAccount(), student.getStudentName(), type);
            return ServerResponse.createByError(300, "未查询到可以学习的课程！");
        }

        List<CourseNew> courseNews = courseFeignClient.getByIds(courseIds);

        // 获取学生可以学习的版本集合
        List<CourseNew> canStudyCourseNews = courseFeignClient.getByIdsGroupByVersion(courseIds);

        List<VersionVO> versionVos = this.getVersionVos(student, canStudyCourseNews);

        // 判断配置的课程中是否有学生所在的版本
        long count = canStudyCourseNews.stream().filter(courseNew -> Objects.equals(student.getVersion(), courseNew.getVersion())).count();
        String grade = student.getGrade();
        List<Long> smallCourseIds = this.getSmallCourseIds(courseId, count, versionVos, type, courseNews);

        // 其他年级
        List<CourseVO> previousGrade = new ArrayList<>();
        // 当前年级
        List<CourseVO> currentGrade = new ArrayList<>();

        if (type == SYNTAX_MODEL_TYPE) {
            this.packageSyntaxInfoVO(student, courseIds, previousGrade, currentGrade);
        } else {
            // 各个课程下所有单元个数
            Map<Long, Integer> unitCountInCourse = courseFeignClient.countUnitByIds(courseIds, type);

            Map<Long, Map<Long, Object>> learnUnitCountInCourse;
            Long studentId = student.getId();
            if (type == GOLD_TEST) {
                // 各个课程下已参与过金币试卷测试单元个数
                learnUnitCountInCourse = testRecordMapper.countGoldTestByStudentIdAndCourseIds(studentId, courseIds);
            } else {
                // 各个课程下已学习单元个数
                learnUnitCountInCourse = learnHistoryMapper.countUnitByStudentIdAndCourseIds(studentId, courseIds, type);
            }

            courseFeignClient.getByIds(smallCourseIds).forEach(courseNew -> packageVO(student, unitCountInCourse, learnUnitCountInCourse, previousGrade, currentGrade, courseNew));
        }
        currentGrade.sort((currentGrade1, currentGrade2) -> (int) (currentGrade1.getCourseId() - currentGrade2.getCourseId()));
        previousGrade.sort((previousGrade1, previousGrade2) -> (int) (previousGrade1.getCourseId() - previousGrade2.getCourseId()));
        return ServerResponse.createBySuccess(CourseInfoVO.builder()
                .currentGrade(currentGrade)
                .previousGrade(previousGrade)
                .versions(versionVos)
                .InGrade(grade)
                .build());
    }

    /**
     * 获取当前版本小于或等于学生年级的课程id
     * 获取最终页面被选中的版本信息（只展示有课程信息的版本）
     *
     * @param courseId
     * @param count      配置的课程中含有有学生所在的版本个数
     * @param versionVos
     * @param type       学习模块
     * @param courseNews
     * @return
     */
    private List<Long> getSmallCourseIds(Long courseId, long count, List<VersionVO> versionVos, Integer type, List<CourseNew> courseNews) {
        Map<Long, Long> courseIdMap = new HashMap<>(16);
        courseNews.forEach(courseNew -> courseIdMap.put(courseNew.getId(), courseNew.getId()));
        List<VersionVO> versionVOList = new ArrayList<>();
        List<Long> smallCourseIds = new ArrayList<>();
        if (courseId == null) {
            // 首次进入首页面
            versionVos.forEach(versionVO -> {
                String version = versionVO.getVersion();
                List<String> gradeList = courseNews.stream()
                        .filter(courseNew -> Objects.equals(courseNew.getVersion(), version))
                        .map(CourseNew::getGrade).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(gradeList)) {
                    // 当前版本中小于或等于当前年级的所有课程id
                    List<Long> courseIds = courseFeignClient.getByGradeListAndVersionAndGrade(version, gradeList, type);
                    if (CollectionUtils.isEmpty(courseIds)) {
                        return;
                    }

                    /*
                    如果有学生所在的版本，将学生所在版本置为默认选中版本
                    如果没有学生所在版本，将第一个有课程信息的版本置为选中版本
                     */
                    boolean flag = (count == 0 && CollectionUtils.isEmpty(smallCourseIds)) || versionVO.getSelected();
                    if (flag) {
                        courseIds.forEach(id -> {
                            if (courseIdMap.containsKey(id)) {
                                smallCourseIds.add(id);
                            }
                        });
                    }
                    versionVOList.add(versionVO);
                }
            });

            versionVos.clear();
            if (count == 0 && CollectionUtils.isNotEmpty(versionVOList)) {
                versionVOList.get(0).setSelected(true);
            }
        } else {
            // 查询指定课程数据
            String finalTargetVersion = courseFeignClient.getById(courseId).getVersion();
            versionVos.forEach(versionVO -> {
                String version = versionVO.getVersion();
                boolean flag = Objects.equals(finalTargetVersion, version);
                versionVO.setSelected(flag);
                if (flag) {
                    versionVOList.add(versionVO);
                    List<String> gradeList = courseNews.stream()
                            .filter(courseNew -> Objects.equals(courseNew.getVersion(), finalTargetVersion))
                            .map(CourseNew::getGrade).collect(Collectors.toList());

                    List<Long> courseIds = courseFeignClient.getByGradeListAndVersionAndGrade(finalTargetVersion, gradeList, type);
                    if (CollectionUtils.isNotEmpty(courseIds)) {
                        courseIds.forEach(id -> {
                            if (courseIdMap.containsKey(id)) {
                                smallCourseIds.add(id);
                            }
                        });
                    }
                    return;
                }

                List<String> gradeList = courseNews.stream()
                        .filter(courseNew -> Objects.equals(courseNew.getVersion(), version))
                        .map(CourseNew::getGrade).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(gradeList)) {
                    // 当前版本中小于或等于当前年级的所有课程id
                    List<Long> courseIds = courseFeignClient.getByGradeListAndVersionAndGrade(version, gradeList, type);
                    if (CollectionUtils.isEmpty(courseIds)) {
                        return;
                    }
                }
                if (CollectionUtils.isNotEmpty(gradeList)) {
                    versionVOList.add(versionVO);
                }
            });

            versionVos.clear();
        }
        versionVos.addAll(versionVOList);
        return smallCourseIds;
    }

    /**
     * 封装最终响应的版本集合
     *
     * @param student
     * @param canStudyCourseNews
     * @return
     */
    private List<VersionVO> getVersionVos(Student student, List<CourseNew> canStudyCourseNews) {
        return canStudyCourseNews.stream().map(courseNew -> {
            if (Objects.equals(courseNew.getVersion(), student.getVersion())) {
                return VersionVO.builder()
                        .selected(true)
                        .courseId(courseNew.getId())
                        .version(courseNew.getVersion())
                        .build();
            }
            return VersionVO.builder()
                    .selected(false)
                    .courseId(courseNew.getId())
                    .version(courseNew.getVersion())
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 封装语法课程响应数据
     *
     * @param student
     * @param courseIds
     * @param previousGrade 其他年级
     * @param currentGrade  当前年级
     */
    private void packageSyntaxInfoVO(Student student, List<Long> courseIds, List<CourseVO> previousGrade, List<CourseVO> currentGrade) {
        // 获取所有语法课程数据
        List<CourseNew> courseNews = courseFeignClient.getByIds(courseIds);
        Map<Long, Map<String, Object>> syntaxCourseMap = courseFeignClient.getByCourseNews(courseNews);

        if (syntaxCourseMap.size() == 0) {
            log.info("未查询到学段为[{}]的语法课程！", courseNews.size() > 0 ? courseNews.get(0).getStudyParagraph() : "");
            throw new ServiceException("未查询到学生的自由学习课程！");
        }

        List<Long> syntaxCourseIds = new ArrayList<>();
        // 各个课程下所有单元个数
        Map<Long, Integer> unitCountInCourse = new HashMap<>(16);

        syntaxCourseMap.forEach((courseId, map) -> {
            syntaxCourseIds.add(courseId);
            unitCountInCourse.put(courseId, Integer.parseInt(String.valueOf(map.get(COUNT))));
        });

        Map<Long, Map<Long, Object>> learnUnitCountInCourse = learnHistoryMapper.countUnitByStudentIdAndCourseIds(student.getId(), syntaxCourseIds, SYNTAX_MODEL_TYPE);

        syntaxCourseMap.forEach((courseId, map) -> {
            // 添加返回年级及英文年级选项
            String grade = map.get("grade").toString();
            String label = map.get("label").toString();

            CourseNew courseNew = CourseNew.builder()
                    .id(courseId)
                    .grade(grade)
                    .label(label)
                    .status(-1)
                    .build();

            this.packageVO(student, unitCountInCourse, learnUnitCountInCourse, previousGrade, currentGrade, courseNew);
        });
    }

    /**
     * @param student
     * @param unitCountInCourse
     * @param learnUnitCountInCourse
     * @param previousGrade
     * @param currentGrade
     * @param courseNew
     */
    private void packageVO(Student student, Map<Long, Integer> unitCountInCourse,
                           Map<Long, Map<Long, Object>> learnUnitCountInCourse,
                           List<CourseVO> previousGrade, List<CourseVO> currentGrade, CourseNew courseNew) {
        String grade = StringUtils.isNotBlank(courseNew.getGradeExt()) ? courseNew.getGradeExt() : courseNew.getGrade();
        Long courseId = courseNew.getId();

        // 单元总个数
        long totalUnitCount = unitCountInCourse.get(courseId) == null ? 0 : unitCountInCourse.get(courseId) * 2;

        // 已学单元总个数
        long learnedUnitCount = this.getUnitCount(learnUnitCountInCourse, courseId);

        CourseVO.CourseVOBuilder courseVoBuilder = CourseVO.builder()
                .courseId(courseId)
                .grade(courseNew.getGrade() + "（" + courseNew.getLabel() + "）")
                .englishGrade(getGradeAndLabelEnglishName(grade, courseNew.getLabel()));
        if (learnedUnitCount == 0) {
            courseVoBuilder.combatProgress(0).battle(1);
        } else if (totalUnitCount == learnedUnitCount) {
            courseVoBuilder.combatProgress(100).battle(3);
        } else {
            courseVoBuilder.combatProgress(Math.min((int) (learnedUnitCount * 1.0 / totalUnitCount * 100), 100)).battle(2);
        }

        if (Objects.equals(grade, student.getGrade())) {
            // 当前年级
            currentGrade.add(courseVoBuilder.build());
        } else {
            // 不是当前年级
            previousGrade.add(courseVoBuilder.build());
        }
    }

    @SuppressWarnings("all")
    private long getUnitCount(Map<Long, Map<Long, Object>> unitCountInCourse, Long courseId) {
        long unitCount = 0;
        if (unitCountInCourse != null && unitCountInCourse.get(courseId) != null
                && unitCountInCourse.get(courseId).get(COUNT) != null) {
            unitCount = Long.parseLong(unitCountInCourse.get(courseId).get(COUNT).toString());
        }
        return unitCount;
    }

    /**
     * 将年级转换为英文
     *
     * @param grade
     * @return
     */
    public static String getGradeAndLabelEnglishName(String grade, String label) {
        return MAPPING.get(grade) + "-" + MAPPING.get(label);
    }
}

