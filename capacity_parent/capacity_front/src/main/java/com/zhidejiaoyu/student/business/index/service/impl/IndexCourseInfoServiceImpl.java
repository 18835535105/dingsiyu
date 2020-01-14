package com.zhidejiaoyu.student.business.index.service.impl;

import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.CourseConfig;
import com.zhidejiaoyu.common.pojo.CourseNew;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.SyntaxCourse;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.index.dto.UnitInfoDTO;
import com.zhidejiaoyu.student.business.index.service.IndexCourseInfoService;
import com.zhidejiaoyu.student.business.index.vo.course.CourseInfoVO;
import com.zhidejiaoyu.student.business.index.vo.course.CourseVO;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.syntax.constant.GradeNameConstant;
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
@Service
public class IndexCourseInfoServiceImpl extends BaseServiceImpl<CourseConfigMapper, CourseConfig> implements IndexCourseInfoService {


    public static final String COUNT = "count";

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

    @Override
    public ServerResponse<CourseInfoVO> getStudyCourse(Integer type) {
        Student student = super.getStudent(HttpUtil.getHttpSession());

        // 查询学生可自由学习的课程
        List<CourseConfig> courseConfigs = courseConfigMapper.selectByUserIdAndTypeAndOneKeyLearn(student.getId(), 2, 2);
        if (CollectionUtils.isNotEmpty(courseConfigs)) {
            return this.packageCourse(student, courseConfigs, type);
        }

        // 说明没有针对学生的教材配置，查询校区的教材配置
        Integer schoolAdminId = TeacherInfoUtil.getSchoolAdminId(student);
        if (schoolAdminId != null) {
            courseConfigs = courseConfigMapper.selectByUserIdAndTypeAndOneKeyLearn((long) schoolAdminId, 1, 2);
            if (CollectionUtils.isNotEmpty(courseConfigs)) {
                return this.packageCourse(student, courseConfigs, type);
            }
        }

        // 说明没有针对校区的教材配置，查询总部的教材配置
        courseConfigs = courseConfigMapper.selectByUserIdAndTypeAndOneKeyLearn(1L, 1, 2);
        if (CollectionUtils.isNotEmpty(courseConfigs)) {
            return this.packageCourse(student, courseConfigs, type);
        }

        throw new ServiceException("未查询到学生的自由学习课程！");
    }

    @Override
    public ServerResponse<Object> getUnitInfo(UnitInfoDTO dto) {

        List<Map<String, Object>> map;
        if (dto.getType() == 3) {
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
     * @return
     */
    private ServerResponse<CourseInfoVO> packageCourse(Student student, List<CourseConfig> courseConfigs, int type) {

        CourseInfoVO courseInfoVO = new CourseInfoVO();
        courseInfoVO.setInGrade(student.getGrade());

        // 过滤出能够学习单词的课程id
        List<Long> courseIds = courseConfigs.stream()
                .filter(courseConfig -> courseConfig.getStudyModel().contains(String.valueOf(type)))
                .map(CourseConfig::getCourseId)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(courseIds)) {
            throw new ServiceException(300, "未查询到可以学习的课程！");
        }

        List<CourseNew> courseNews = courseNewMapper.selectBatchIds(courseIds);

        // 其他年级
        List<CourseVO> previousGrade = new ArrayList<>();
        // 当前年级
        List<CourseVO> currentGrade = new ArrayList<>();
        if (type == 3) {
            this.packageSyntaxInfoVO(student, courseIds, previousGrade, currentGrade);
        } else {
            // 各个课程下所有单元个数
            Map<Long, Map<Long, Object>> unitCountInCourse = courseNewMapper.countUnitByIds(courseIds, type);

            // 各个课程下已学习单元个数
            Map<Long, Map<Long, Object>> learnUnitCountInCourse = learnHistoryMapper.countUnitByStudentIdAndCourseIds(student.getId(), courseIds, type == 4 ? 3 : type);


            courseNews.forEach(courseNew -> packageVO(student, unitCountInCourse, learnUnitCountInCourse, previousGrade, currentGrade, courseNew));
        }

        courseInfoVO.setCurrentGrade(currentGrade);
        courseInfoVO.setPreviousGrade(previousGrade);

        return ServerResponse.createBySuccess(courseInfoVO);
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

        Map<Long, Map<Long, Object>> learnUnitCountInCourse = learnHistoryMapper.countUnitByStudentIdAndCourseIds(student.getId(), syntaxCourseIds, 3);

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
        long learnedUnitCount;
        learnedUnitCount = this.getUnitCount(learnUnitCountInCourse, courseId);

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
        String grade1 = null;
        String label1 = null;
        if (grade == null) {
            grade1 = "one";
        }
        if (GradeNameConstant.FIRST_GRADE.equals(grade)) {
            grade1 = "one";
        }
        if (GradeNameConstant.SECOND_GRADE.equals(grade)) {
            grade1 = "two";
        }
        if (GradeNameConstant.WRITE_GRADE.equals(grade)) {
            grade1 = "three";
        }
        if (GradeNameConstant.FOURTH_GRADE.equals(grade)) {
            grade1 = "four";
        }
        if (GradeNameConstant.FIFTH_GRADE.equals(grade)) {
            grade1 = "five";
        }
        if (GradeNameConstant.SIXTH_GRADE.equals(grade)) {
            grade1 = "six";
        }
        if (GradeNameConstant.SEVENTH_GRADE.equals(grade)) {
            grade1 = "seven";
        }
        if (GradeNameConstant.EIGHTH_GRADE.equals(grade)) {
            grade1 = "eight";
        }
        if (GradeNameConstant.NINTH_GRADE.equals(grade)) {
            grade1 = "nine";
        }

        if (GradeNameConstant.SENIOR_ONE.equals(grade)) {
            grade1 = "ten";
        }
        if (GradeNameConstant.SENIOR_TWO.equals(grade)) {
            grade1 = "eleven";
        }
        if (GradeNameConstant.SENIOR_THREE.equals(grade)) {
            grade1 = "twelve";
        }
        if (GradeNameConstant.VOLUME_1.equals(label)) {
            label1 = "up";
        }
        if (GradeNameConstant.VOLUME_2.equals(label)) {
            label1 = "down";
        }
        return grade1 + "-" + label1;
    }
}

