package com.dfdz.course.business.controller;

import com.dfdz.course.business.service.CourseService;
import com.dfdz.course.business.service.UnitService;
import com.zhidejiaoyu.common.pojo.CourseNew;
import com.zhidejiaoyu.common.pojo.UnitNew;
import com.zhidejiaoyu.common.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author: wuchenxi
 * @date: 2020/6/28 15:07:07
 */
@RestController
@RequestMapping("/course")
public class CourseController {

    @Resource
    private CourseService courseService;
    @Resource
    private UnitService unitService;

    /**
     * 根据courseId获取课程数据
     *
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    public CourseNew getById(@PathVariable String id) {
        return courseService.getCourseById(id);
    }

    /**
     * 根据id获取单元数据
     *
     * @param id
     * @return
     */
    @GetMapping("/getUnitNewById/{id}")
    public UnitNew getUnitNewById(@PathVariable Long id) {
        return unitService.getUnitById(id);
    }

    /**
     * 根据课程id批量获取课程信息
     *
     * @param courseIds
     * @return
     */
    @GetMapping("/getByIds")
    public List<CourseNew> getByIds(@RequestParam List<Long> courseIds) {
        if (CollectionUtils.isEmpty(courseIds)) {
            return Collections.emptyList();
        }
        return courseService.listByIds(courseIds);
    }

    /**
     * 获取学生可以学习的版本集合
     *
     * @param courseIds
     * @return
     */
    @GetMapping("/getByIdsGroupByVersion")
    List<CourseNew> getByIdsGroupByVersion(@RequestParam List<Long> courseIds) {
        if (CollectionUtils.isEmpty(courseIds)) {
            return Collections.emptyList();
        }
        return courseService.getByIdsGroupByVersion(courseIds);
    }

    /**
     * 统计各个课程下单元个数
     *
     * @param courseIds
     * @param type      1：单词；2：句型；3：语法；4：课文；5：金币试卷
     * @return
     */
    @GetMapping("/countUnitByIds")
    public Map<Long, Integer> countUnitByIds(@RequestParam List<Long> courseIds, @RequestParam int type) {
        if (CollectionUtils.isEmpty(courseIds)) {
            return Collections.emptyMap();
        }
        return courseService.countUnitByIds(courseIds, type);
    }

    /**
     * 当前版本中小于或等于当前年级的所有课程id
     *
     * @param version   版本
     * @param gradeList 年级
     * @param type      1：单词；2：句型；3：语法；4：课文；5：金币试卷
     * @return
     */
    @GetMapping("/getByGradeListAndVersionAndGrade")
    public List<Long> getByGradeListAndVersionAndGrade(@RequestParam String version, @RequestParam List<String> gradeList, @RequestParam Integer type) {
        if (StringUtil.isEmpty(version) || CollectionUtils.isEmpty(gradeList) || type == null) {
            return Collections.emptyList();
        }
        return courseService.getByGradeListAndVersionAndGrade(version, gradeList, type);
    }

    /**
     * 获取当前课程相关的语法数据
     *
     * @param courseNews
     * @return
     */
    @PostMapping("/getByCourseNews")
    public Map<Long, Map<String, Object>> getByCourseNews(@RequestBody List<CourseNew> courseNews) {
        return courseService.getByCourseNews(courseNews);
    }

}
