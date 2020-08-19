package com.dfdz.course.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.course.business.service.CourseService;
import com.zhidejiaoyu.common.dto.testbeforestudy.GradeAndUnitIdDTO;
import com.zhidejiaoyu.common.mapper.CourseNewMapper;
import com.zhidejiaoyu.common.pojo.CourseNew;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: wuchenxi
 * @date: 2020/6/28 15:09:09
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseNewMapper, CourseNew> implements CourseService {
    @Resource
    private CourseNewMapper courseNewMapper;

    @Override
    public CourseNew getCourseById(String id) {
        return courseNewMapper.selectById(id);
    }

    @Override
    public Map<Long, Integer> countUnitByIds(List<Long> courseIds, int type) {
        Map<Long, Map<Long, Object>> longMapMap = courseNewMapper.countUnitByIds(courseIds, type);
        Map<Long, Integer> map = new HashMap<>(longMapMap.size());
        longMapMap.forEach((courseId, map1) -> map.put(courseId, Integer.parseInt(String.valueOf(map1.get("count")))));
        return map;
    }

    @Override
    public List<Long> getByGradeListAndVersionAndGrade(String version, List<String> gradeList, Integer type) {
        return courseNewMapper.selectByGradeListAndVersionAndGrade(version, gradeList, type);
    }

    @Override
    public Map<Long, Map<String, Object>> getByCourseNews(List<CourseNew> courseNews) {
        Map<Long, Map<String, Object>> longMapMap = courseNewMapper.selectByCourseNews(courseNews);
        if (longMapMap == null) {
            return Collections.emptyMap();
        }

        Map<Long, Map<String, Object>> map = new HashMap<>(longMapMap.size());
        longMapMap.forEach(map::put);
        return map;
    }

    @Override
    public List<CourseNew> getByIdsGroupByVersion(List<Long> courseIds) {
        return courseNewMapper.selectByIds(courseIds);
    }

    @Override
    public List<String> getAllVersion(String studyParagraph) {
        return courseNewMapper.selectVersionByStudyParagraph(studyParagraph);
    }

    @Override
    public List<Integer> selectCourseIdByVersionAndGradeAndLabel(String version, String grade, String label) {
        return courseNewMapper.selectCourse(version, grade, label);
    }

    @Override
    public List<CourseNew> selectExperienceCourses() {
        return courseNewMapper.selectExperienceCourses();
    }

    @Override
    public String getPhaseByUnitId(Long unitId) {
        return courseNewMapper.selectPhaseByUnitId(unitId);
    }

    @Override
    public List<GradeAndUnitIdDTO> getGradeAndLabelByUnitIds(List<Long> unitIds) {
        return courseNewMapper.selectGradeAndLabelByUnitIds(unitIds);
    }

    @Override
    public String selectGradeByCourseId(Long courseId) {
        return courseNewMapper.selectGradeByCourseId(courseId);
    }

    @Override
    public List<Map<String, Object>> selectIdAndVersionByStudentIdByPhase(Long studentId, String phase) {
        return courseNewMapper.selectIdAndVersionByStudentIdByPhase(studentId, phase);
    }

    @Override
    public Map<Long, Map<String, Object>> selectUnitsWordSum(long courseId) {
        return courseNewMapper.selectUnitsWordSum(courseId);
    }

    @Override
    public List<Long> getIdsByVersion(String version) {
        return courseNewMapper.selectIdsByVersion(version);
    }

    @Override
    public List<Long> getIdsByPhaseAndIds(List<String> phase, List<Long> courseIds) {
        return courseNewMapper.selectIdsByPhasesAndIds(phase, courseIds);
    }

    @Override
    public Map<Long, Map<String, Object>> selectGradeAndLabelByCourseIds(List<Long> courseIds) {
        return courseNewMapper.selectGradeAndLabelByCourseIds(courseIds);
    }

    @Override
    public CourseNew getByUnitId(Long unitId) {
        return courseNewMapper.selectByUnitId(unitId);
    }
}
