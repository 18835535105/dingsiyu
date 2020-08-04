package com.dfdz.teacher.feignclient;

import com.zhidejiaoyu.common.pojo.CourseNew;
import com.zhidejiaoyu.common.pojo.UnitNew;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@FeignClient(name = "course", path = "/course")
public interface CourseFeignClient {
    /**
     * 获取课程数据
     */
    /**
     * []
     * 根据id获取课程数据信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/getById/{id}", method = RequestMethod.GET)
    CourseNew getById(@PathVariable Long id);

    /**
     * 根据版本，年级，标签 查看courseId
     *
     * @param version
     * @param grade
     * @param label
     * @return
     */
    @RequestMapping(value = "selectCourseIdByVersionAndGradeAndLabel", method = RequestMethod.GET)
    List<Integer> selectCourseIdByVersionAndGradeAndLabel(@RequestParam String version, @RequestParam String grade, @RequestParam String label);


    @RequestMapping(value = "selectExperienceCourses", method = RequestMethod.GET)
    List<CourseNew> selectExperienceCourses();

    /**
     * 根据courseIds查询unit信息
     *
     * @param courseIds
     * @return
     */
    @RequestMapping(value = "/unit/selectByCourseIds", method = RequestMethod.GET)
    List<UnitNew> selectByCourseIds(@RequestBody List<Long> courseIds);

    /**
     * 查询课程添加单元数据
     *
     * @param version
     * @param smallGradeList
     * @return
     */
    @RequestMapping(value = "/unit/selectMapByGradeListAndVersionAndGrade", method = RequestMethod.GET)
    List<Map<String, Long>> selectMapByGradeListAndVersionAndGrade(@RequestParam String version, @RequestParam List<String> smallGradeList);

    /**
     * 获取当前课程中小于或等于当前单元的所有单元id
     *
     * @param courseId
     * @param unitId
     * @return
     */
    @RequestMapping(value = "/unit/selectMapLessOrEqualsCurrentIdByCourseIdAndUnitId", method = RequestMethod.GET)
    List<Map<String, Long>> selectMapLessOrEqualsCurrentIdByCourseIdAndUnitId(@RequestParam Long courseId, @RequestParam Long unitId);

    /**
     * 查询指定课程名的所有单元id
     *
     * @param courseNames
     * @return
     */
    @RequestMapping(value = "/unit/selectIdsMapByCourseNames", method = RequestMethod.GET)
    List<Map<String, Long>> selectIdsMapByCourseNames(@RequestBody List<String> courseNames);

    /**
     * 使用unitIds获取map
     * @param unitIds
     * @return
     */
    @RequestMapping(value = "/unit/selectMapByIds", method = RequestMethod.GET)
    List<Map<String, Object>> selectMapByIds(@RequestBody List<Long> unitIds);
}
