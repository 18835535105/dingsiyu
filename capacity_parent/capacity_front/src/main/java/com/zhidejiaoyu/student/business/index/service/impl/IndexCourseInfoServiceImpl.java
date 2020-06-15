package com.zhidejiaoyu.student.business.index.service.impl;

import com.zhidejiaoyu.common.constant.GradeNameConstant;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.CourseConfig;
import com.zhidejiaoyu.common.pojo.CourseNew;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.SyntaxCourse;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import com.zhidejiaoyu.common.utils.grade.GradeUtil;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.index.dto.UnitInfoDTO;
import com.zhidejiaoyu.student.business.index.service.IndexCourseInfoService;
import com.zhidejiaoyu.student.business.index.vo.course.CourseInfoVO;
import com.zhidejiaoyu.student.business.index.vo.course.CourseVO;
import com.zhidejiaoyu.student.business.index.vo.course.VersionVO;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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
     * 金币试卷type值
     */
    private static final int GOLD_TEST = 5;

    @Resource
    private CourseConfigMapper courseConfigMapper;

    @Resource
    private CourseNewMapper courseNewMapper;

    @Resource
    private LearnHistoryMapper learnHistoryMapper;

    @Resource
    private UnitNewMapper unitNewMapper;

    @Resource
    private SyntaxCourseMapper syntaxCourseMapper;

    @Resource
    private SyntaxUnitMapper syntaxUnitMapper;

    @Resource
    private TestRecordMapper testRecordMapper;

    static {
        MAPPING.put(GradeNameConstant.FIRST_GRADE, "one");
        MAPPING.put(GradeNameConstant.SECOND_GRADE, "two");
        MAPPING.put(GradeNameConstant.WRITE_GRADE, "three");
        MAPPING.put(GradeNameConstant.FOURTH_GRADE, "four");
        MAPPING.put(GradeNameConstant.FIFTH_GRADE, "five");
        MAPPING.put(GradeNameConstant.SIXTH_GRADE, "six");
        MAPPING.put(GradeNameConstant.SEVENTH_GRADE, "seven");
        MAPPING.put(GradeNameConstant.EIGHTH_GRADE, "eight");
        MAPPING.put(GradeNameConstant.NINTH_GRADE, "nine");
        MAPPING.put(GradeNameConstant.SENIOR_ONE, "gaoyi");
        MAPPING.put(GradeNameConstant.SENIOR_TWO, "gaoer");
        MAPPING.put(GradeNameConstant.SENIOR_THREE, "gaosan");
        MAPPING.put(GradeNameConstant.HIGH, "gaozhong");

        MAPPING.put(GradeNameConstant.VOLUME_1, "up");
        MAPPING.put(GradeNameConstant.VOLUME_2, "down");
        // 全册
        MAPPING.put(GradeNameConstant.FULL_VOLUME, "quance");
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

    @Override
    public ServerResponse<Object> getStudyCourse(Integer type, Long courseId) {
        Student student = super.getStudent(HttpUtil.getHttpSession());

        // 查询学生可自由学习的课程
        List<CourseConfig> courseConfigs = courseConfigMapper.selectByUserIdAndTypeAndOneKeyLearn(student.getId(), 2);
        if (CollectionUtils.isNotEmpty(courseConfigs)) {
            return this.packageCourse(student, courseConfigs, type, courseId);
        }

        // 说明没有针对学生的教材配置，查询校区的教材配置
        Integer schoolAdminId = TeacherInfoUtil.getSchoolAdminId(student);
        if (schoolAdminId != null) {
            courseConfigs = courseConfigMapper.selectByUserIdAndTypeAndOneKeyLearn((long) schoolAdminId, 1);
            if (CollectionUtils.isNotEmpty(courseConfigs)) {
                return this.packageCourse(student, courseConfigs, type, courseId);
            }
        }

        // 说明没有针对校区的教材配置，查询总部的教材配置
        courseConfigs = courseConfigMapper.selectByUserIdAndTypeAndOneKeyLearn(1L, 1);
        if (CollectionUtils.isNotEmpty(courseConfigs)) {
            return this.packageCourse(student, courseConfigs, type, courseId);
        }

        throw new ServiceException("未查询到学生的自由学习课程！");
    }

    @Override
    public ServerResponse<Object> getUnitInfo(UnitInfoDTO dto) {

        List<Map<String, Object>> map;
        if (dto.getType() == SYNTAX_MODEL_TYPE) {
            map = syntaxUnitMapper.selectIdAndNameByCourseId(dto.getCourseId());
        } else {
            map = unitNewMapper.selectIdAndNameByCourseId(dto.getCourseId(), dto.getType());
        }

        return ServerResponse.createBySuccess(map);
    }


    /**
     * 封装响应结果
     *
     * @param student
     * @param courseConfigs
     * @param type
     * @param courseId      用于查询版本名称
     * @return
     */
    private ServerResponse<Object> packageCourse(Student student, List<CourseConfig> courseConfigs, int type, Long courseId) {

        // 过滤出能够学习单词的课程id
        List<Long> courseIds = courseConfigs.stream()
                .filter(courseConfig -> courseConfig.getStudyModel().contains(String.valueOf(type)))
                .map(CourseConfig::getCourseId)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(courseIds)) {
            log.warn("学生[{}-{}-{}]未查询到可以学习的课程！type=[{}]", student.getId(), student.getAccount(), student.getStudentName(), type);
            return ServerResponse.createByError(300, "未查询到可以学习的课程！");
        }

        // 获取学生可以学习的版本集合
        List<CourseNew> canStudyCourseNews = courseNewMapper.selectByIds(courseIds);

        List<VersionVO> versionVos = this.getVersionVos(student, canStudyCourseNews);

        // 判断配置的课程中是否有学生所在的版本
        long count = canStudyCourseNews.stream().filter(courseNew -> Objects.equals(student.getVersion(), courseNew.getVersion())).count();
        String grade = student.getGrade();
        List<Long> smallCourseIds = this.getSmallCourseIds(courseId, count, versionVos, grade);


        // 其他年级
        List<CourseVO> previousGrade = new ArrayList<>();
        // 当前年级
        List<CourseVO> currentGrade = new ArrayList<>();

        if (type == SYNTAX_MODEL_TYPE) {
            this.packageSyntaxInfoVO(student, courseIds, previousGrade, currentGrade);
        } else {

            if (CollectionUtils.isEmpty(smallCourseIds)) {
                return ServerResponse.createBySuccess(CourseInfoVO.builder()
                        .currentGrade(null)
                        .previousGrade(null)
                        .versions(versionVos)
                        .InGrade(grade)
                        .build());
            }

            // 各个课程下所有单元个数
            Map<Long, Map<Long, Object>> unitCountInCourse = courseNewMapper.countUnitByIds(courseIds, type);

            Map<Long, Map<Long, Object>> learnUnitCountInCourse;
            Long studentId = student.getId();
            if (type == GOLD_TEST) {
                // 各个课程下已参与过金币试卷测试单元个数
                learnUnitCountInCourse = testRecordMapper.countGoldTestByStudentIdAndCourseIds(studentId, courseIds);
            } else {
                // 各个课程下已学习单元个数
                learnUnitCountInCourse = learnHistoryMapper.countUnitByStudentIdAndCourseIds(studentId, courseIds, type);
            }

            List<CourseNew> courseNews = courseNewMapper.selectBatchIds(smallCourseIds);
            // 匹配不到年级和上下册的课程不展示
            courseNews.forEach(courseNew -> packageVO(student, unitCountInCourse, learnUnitCountInCourse, previousGrade, currentGrade, courseNew));
        }

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
     * @param count
     * @param versionVos
     * @param grade      学生所在年级
     * @return
     */
    private List<Long> getSmallCourseIds(Long courseId, long count, List<VersionVO> versionVos, String grade) {
        List<VersionVO> versionVOList = new ArrayList<>();
        List<Long> smallCourseIds = new ArrayList<>();
        if (courseId == null) {
            // 首次进入首页面
            versionVos.forEach(versionVO -> {
                String version = versionVO.getVersion();
                List<String> gradeList = GradeUtil.smallThanCurrentAllPhase(version, grade);

                if (CollectionUtils.isNotEmpty(gradeList)) {
                    // 当前版本中小于或等于当前年级的所有课程id
                    List<Long> courseIds = courseNewMapper.selectByGradeListAndVersionAndGrade(version, gradeList);
                    if (CollectionUtils.isEmpty(courseIds)) {
                        return;
                    }
                    /*
                    如果有学生所在的版本，将学生所在版本置为默认选中版本
                    如果没有学生所在版本，将第一个有课程信息的版本置为选中版本
                     */
                    boolean flag = (count == 0 && CollectionUtils.isEmpty(smallCourseIds)) || versionVO.getSelected();
                    if (flag) {
                        smallCourseIds.addAll(courseIds);
                    }
                }
                if (CollectionUtils.isNotEmpty(gradeList)) {
                    versionVOList.add(versionVO);
                }
            });

            versionVos.clear();
            if (count == 0 && CollectionUtils.isNotEmpty(versionVOList)) {
                versionVOList.get(0).setSelected(true);
            }
        } else {
            // 查询指定课程数据
            CourseNew courseNew = courseNewMapper.selectById(courseId);
            String finalTargetVersion = courseNew.getVersion();
            versionVos.forEach(versionVO -> {
                String version = versionVO.getVersion();
                boolean flag = Objects.equals(finalTargetVersion, version);
                versionVO.setSelected(flag);
                if (flag) {
                    versionVOList.add(versionVO);
                    List<String> gradeList = GradeUtil.smallThanCurrentAllPhase(finalTargetVersion, grade);
                    smallCourseIds.addAll(courseNewMapper.selectByGradeListAndVersionAndGrade(finalTargetVersion, gradeList));
                    return;
                }

                List<String> gradeList = GradeUtil.smallThanCurrentAllPhase(version, grade);

                if (CollectionUtils.isNotEmpty(gradeList)) {
                    // 当前版本中小于或等于当前年级的所有课程id
                    List<Long> courseIds = courseNewMapper.selectByGradeListAndVersionAndGrade(version, gradeList);
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
        Map<Long, Map<String, Object>> syntaxCourseMap = syntaxCourseMapper.selectByCourseNewIds(courseIds);

        List<Long> syntaxCourseIds = new ArrayList<>();
        // 各个课程下所有单元个数
        Map<Long, Map<Long, Object>> unitCountInCourse = new HashMap<>(16);

        syntaxCourseMap.forEach((courseId, map) -> {
            syntaxCourseIds.add(courseId);

            Map<Long, Object> map1 = new HashMap<>(16);
            map1.put(courseId, map.get("unitCount"));
            unitCountInCourse.put(courseId, map1);
        });

        Map<Long, Map<Long, Object>> learnUnitCountInCourse = learnHistoryMapper.countUnitByStudentIdAndCourseIds(student.getId(), syntaxCourseIds, SYNTAX_MODEL_TYPE);

        syntaxCourseMap.forEach((courseId, map) -> {
            // 添加返回年级及英文年级选项
            String grade = map.get("grade").toString();
            String label = map.get("label").toString();

            SyntaxCourse syntaxCourse = SyntaxCourse.builder()
                    .id(courseId)
                    .grade(grade)
                    .label(label)
                    .build();

            this.packageVO(student, unitCountInCourse, learnUnitCountInCourse, previousGrade, currentGrade, syntaxCourse);
        });
    }

    /**
     * @param student
     * @param unitCountInCourse
     * @param learnUnitCountInCourse
     * @param previousGrade
     * @param currentGrade
     * @param object
     */
    private void packageVO(Student student, Map<Long, Map<Long, Object>> unitCountInCourse,
                           Map<Long, Map<Long, Object>> learnUnitCountInCourse,
                           List<CourseVO> previousGrade, List<CourseVO> currentGrade, Object object) {
        String grade;
        Long courseId;
        // 单元总个数
        long totalUnitCount;
        CourseVO.CourseVOBuilder courseVoBuilder;
        if (object instanceof CourseNew) {
            CourseNew courseNew = (CourseNew) object;

            grade = StringUtils.isNotBlank(courseNew.getGradeExt()) ? courseNew.getGradeExt() : courseNew.getGrade();
            courseId = courseNew.getId();
            courseVoBuilder = CourseVO.builder()
                    .courseId(courseId)
                    .grade(courseNew.getGrade() + "（" + courseNew.getLabel() + "）")
                    .englishGrade(getGradeAndLabelEnglishName(grade, courseNew.getLabel()));

            totalUnitCount = this.getUnitCount(unitCountInCourse, courseId) * 2;
        } else {
            SyntaxCourse syntaxCourse = (SyntaxCourse) object;

            grade = syntaxCourse.getGrade();
            courseId = syntaxCourse.getId();
            courseVoBuilder = CourseVO.builder()
                    .courseId(courseId)
                    .grade(syntaxCourse.getGrade() + "（" + syntaxCourse.getLabel() + "）")
                    .englishGrade(getGradeAndLabelEnglishName(grade, syntaxCourse.getLabel()));
            totalUnitCount = Long.parseLong(unitCountInCourse.get(courseId).get(courseId).toString()) * 2;
        }

        // 已学单元总个数
        long learnedUnitCount = this.getUnitCount(learnUnitCountInCourse, courseId);

        if (learnedUnitCount == 0) {
            courseVoBuilder.combatProgress(0).battle(1);
        } else if (totalUnitCount == learnedUnitCount) {
            courseVoBuilder.combatProgress(100).battle(3);
        } else {
            courseVoBuilder.combatProgress((int) (learnedUnitCount * 1.0 / totalUnitCount * 100)).battle(2);
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

