package com.zhidejiaoyu.student.business.index.service.impl;

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
import com.zhidejiaoyu.student.business.feignclient.course.CourseFeignClient;
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
    private TestRecordMapper testRecordMapper;

    @Resource
    private CourseFeignClient courseFeignClient;

    @Resource
    private LearnNewMapper learnNewMapper;

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
        Long studentId = super.getStudentId();
        Integer type = dto.getType();
        List<UnitStudyStateVO> vos = unitNewMapper.selectIdAndNameByCourseId(dto.getCourseId(), type);
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

        ServerResponse<Map<Long, Integer>> maxGroupResponse = courseFeignClient.getMaxGroupByUnitIsdAndType(unitIds, type);
        if (maxGroupResponse.getStatus() != ResponseCode.SUCCESS.getCode()) {
            return ServerResponse.createByError(maxGroupResponse.getStatus(), maxGroupResponse.getMsg());
        }
        Map<Long, Integer> maxGroup = maxGroupResponse.getData();
        vos.forEach(vo -> {
            if (collect.containsKey(vo.getUnitId())) {
                List<LearnHistory> histories1 = collect.get(vo.getUnitId());
                Stream<LearnHistory> learnHistoryStream = histories1.stream().filter(learnHistory -> learnHistory.getEasyOrHard() == 1);
                if (learnHistoryStream.count() == 0) {
                    // 说明正常难度还没学习
                    vo.setEasyState(3);
                }

                LearnHistory easyHistory = histories1.stream().filter(learnHistory -> learnHistory.getEasyOrHard() == 1)
                        .max(Comparator.comparing(LearnHistory::getGroup))
                        .orElse(new LearnHistory());
                if (Objects.equals(easyHistory.getGroup(), maxGroup.get(vo.getUnitId()))) {
                    vo.setEasyState(1);
                }

                Stream<LearnHistory> learnHistoryStream1 = histories1.stream().filter(learnHistory -> learnHistory.getEasyOrHard() == 2);
                if (learnHistoryStream1.count() == 0) {
                    // 说明进阶难度还没学习
                    vo.setHardState(3);
                }
                LearnHistory hardHistory = histories1.stream().filter(learnHistory -> learnHistory.getEasyOrHard() == 2)
                        .max(Comparator.comparing(LearnHistory::getGroup))
                        .orElse(new LearnHistory());
                if (Objects.equals(hardHistory.getGroup(), maxGroup.get(vo.getUnitId()))) {
                    vo.setHardState(1);
                }

                if (vo.getEasyState() == null) {
                    vo.setEasyState(3);
                }
                if (vo.getHardState() == null) {
                    vo.setHardState(3);
                }

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
            courseNews.forEach(courseNew -> packageVO(student, unitCountInCourse, learnUnitCountInCourse, previousGrade, currentGrade, courseNew));
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
     * 获取跟课程配置中同年级、上下册的语法数据
     *
     * @param student
     * @param courseIds
     * @param previousGrade 其他年级
     * @param currentGrade  当前年级
     */
    private void packageSyntaxInfoVO(Student student, List<Long> courseIds, List<CourseVO> previousGrade, List<CourseVO> currentGrade) {
        // 获取所有语法课程数据
        List<CourseNew> courseNews = courseNewMapper.selectBatchIds(courseIds);
        Map<Long, Map<String, Object>> syntaxCourseMap = courseNewMapper.selectByCourseNews(courseNews);

        List<Long> syntaxCourseIds = new ArrayList<>();
        // 各个课程下所有单元个数
        Map<Long, Map<Long, Object>> unitCountInCourse = new HashMap<>(16);

        syntaxCourseMap.forEach((courseId, map) -> {
            syntaxCourseIds.add(courseId);

            Map<Long, Object> map1 = new HashMap<>(16);
            map1.put(courseId, map.get(COUNT));
            unitCountInCourse.put(courseId, map1);
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
    private void packageVO(Student student, Map<Long, Map<Long, Object>> unitCountInCourse,
                           Map<Long, Map<Long, Object>> learnUnitCountInCourse,
                           List<CourseVO> previousGrade, List<CourseVO> currentGrade, CourseNew courseNew) {
        String grade = StringUtils.isNotBlank(courseNew.getGradeExt()) ? courseNew.getGradeExt() : courseNew.getGrade();
        Long courseId = courseNew.getId();

        CourseVO.CourseVOBuilder courseVoBuilder = CourseVO.builder()
                .courseId(courseId)
                .grade(courseNew.getGrade() + "（" + courseNew.getLabel() + "）")
                .englishGrade(getGradeAndLabelEnglishName(grade, courseNew.getLabel()));

        // 单元总个数
        long totalUnitCount;
        if (Objects.equals(courseNew.getStatus(), -1)) {
            // 语法
            totalUnitCount = Long.parseLong(unitCountInCourse.get(courseId).get(courseId).toString()) * 2;
        } else {
            totalUnitCount = this.getUnitCount(unitCountInCourse, courseId) * 2;
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

