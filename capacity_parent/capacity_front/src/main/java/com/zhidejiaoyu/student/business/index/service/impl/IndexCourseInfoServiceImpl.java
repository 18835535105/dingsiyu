package com.zhidejiaoyu.student.business.index.service.impl;

import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.CourseConfigMapper;
import com.zhidejiaoyu.common.mapper.CourseNewMapper;
import com.zhidejiaoyu.common.mapper.LearnHistoryMapper;
import com.zhidejiaoyu.common.pojo.CourseConfig;
import com.zhidejiaoyu.common.pojo.CourseNew;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.index.service.IndexCourseInfoService;
import com.zhidejiaoyu.student.business.index.vo.course.CourseInfoVO;
import com.zhidejiaoyu.student.business.index.vo.course.CourseVO;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.syntax.constant.GradeNameConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: wuchenxi
 * @date: 2019/12/27 13:41:41
 */
@Service
public class IndexCourseInfoServiceImpl extends BaseServiceImpl<CourseConfigMapper, CourseConfig> implements IndexCourseInfoService {


    public static final String COURSE_ID = "courseId";
    public static final String COUNT = "count";
    @Resource
    private CourseConfigMapper courseConfigMapper;

    @Resource
    private CourseNewMapper courseNewMapper;

    @Resource
    private LearnHistoryMapper learnHistoryMapper;

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
        // 各个课程下所有单元个数
        Map<Long, Map<Long, Integer>> unitCountInCourse = courseNewMapper.countUnitByIds(courseIds, type);

        // 各个课程下已学习单元个数
        Map<Long, Map<Long, Integer>> learnUnitCountInCourse = learnHistoryMapper.countUnitByCourseIds(courseIds, type);

        // 其他年级
        List<CourseVO> previousGrade = new ArrayList<>();
        // 当前年级
        List<CourseVO> currentGrade = new ArrayList<>();
        courseNews.forEach(courseNew -> {

            String grade = StringUtils.isNotBlank(courseNew.getGradeExt()) ? courseNew.getGradeExt() : courseNew.getGrade();

            CourseVO.CourseVOBuilder courseVoBuilder = CourseVO.builder()
                    .courseId(courseNew.getId())
                    .grade(courseNew.getGrade() + "（" + courseNew.getLabel() + "）");

            String englishGrade = this.getGradeAndLabelEnglishName(grade);
            String englishLabel = this.getGradeAndLabelEnglishName(courseNew.getLabel());

            int totalUnitCount = 0;
            if (unitCountInCourse != null && unitCountInCourse.get(COURSE_ID) != null
                    && unitCountInCourse.get(COURSE_ID).get(COUNT) != null) {
                totalUnitCount = unitCountInCourse.get(COURSE_ID).get(COUNT);
            }

            int learnedUnitCount = 0;
            if (learnUnitCountInCourse != null && learnUnitCountInCourse.get(COURSE_ID) != null
                    && learnUnitCountInCourse.get(COURSE_ID).get(COUNT) != null) {
                learnedUnitCount = learnUnitCountInCourse.get(COURSE_ID).get(COUNT);
            }

            if (learnedUnitCount == 0) {
                courseVoBuilder.combatProgress(0).battle(1);
            } else if (totalUnitCount == learnedUnitCount) {
                courseVoBuilder.combatProgress(100).battle(3);
            } else {
                courseVoBuilder.combatProgress((int) (learnedUnitCount * 1.0 / totalUnitCount * 100)).battle(2);
            }

            courseVoBuilder.englishGrade(englishGrade + "-" + englishLabel);

            if (Objects.equals(grade, student.getGrade())) {
                // 当前年级
                currentGrade.add(courseVoBuilder.build());
            } else {
                // 不是当前年级
                previousGrade.add(courseVoBuilder.build());
            }
        });

        courseInfoVO.setCurrentGrade(currentGrade);
        courseInfoVO.setPreviousGrade(previousGrade);

        return ServerResponse.createBySuccess(courseInfoVO);
    }

    /**
     * 将年级转换为英文
     *
     * @param grade
     * @return
     */
    private String getGradeAndLabelEnglishName(String grade) {
        if (grade == null) {
            return "one";
        }
        if (GradeNameConstant.FIRST_GRADE.equals(grade)) {
            return "one";
        }
        if (GradeNameConstant.SECOND_GRADE.equals(grade)) {
            return "two";
        }
        if (GradeNameConstant.WRITE_GRADE.equals(grade)) {
            return "three";
        }
        if (GradeNameConstant.FOURTH_GRADE.equals(grade)) {
            return "four";
        }
        if (GradeNameConstant.FIFTH_GRADE.equals(grade)) {
            return "five";
        }
        if (GradeNameConstant.SIXTH_GRADE.equals(grade)) {
            return "six";
        }
        if (GradeNameConstant.SEVENTH_GRADE.equals(grade)) {
            return "seven";
        }
        if (GradeNameConstant.EIGHTH_GRADE.equals(grade)) {
            return "eight";
        }
        if (GradeNameConstant.NINTH_GRADE.equals(grade)) {
            return "nine";
        }

        if (GradeNameConstant.SENIOR_ONE.equals(grade)) {
            return "ten";
        }
        if (GradeNameConstant.SENIOR_TWO.equals(grade)) {
            return "eleven";
        }
        if (GradeNameConstant.SENIOR_THREE.equals(grade)) {
            return "twelve";
        }
        if (GradeNameConstant.VOLUME_1.equals(grade)) {
            return "up";
        }
        if (GradeNameConstant.VOLUME_2.equals(grade)) {
            return "down";
        }
        return null;
    }
}

