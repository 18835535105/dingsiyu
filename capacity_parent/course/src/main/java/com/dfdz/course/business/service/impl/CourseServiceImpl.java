package com.dfdz.course.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.course.business.service.CourseService;
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
}
