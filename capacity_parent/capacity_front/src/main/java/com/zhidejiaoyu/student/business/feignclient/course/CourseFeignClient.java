package com.zhidejiaoyu.student.business.feignclient.course;

import com.zhidejiaoyu.common.dto.testbeforestudy.GradeAndUnitIdDTO;
import com.zhidejiaoyu.common.pojo.CourseNew;
import com.zhidejiaoyu.common.pojo.UnitNew;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author: wuchenxi
 * @date: 2020/6/28 15:12:12
 */
@FeignClient(name = "course", path = "/course/course")
public interface CourseFeignClient {


    @RequestMapping(value = "/selectIdAndVersionByStudentIdByPhase", method = RequestMethod.GET)
    List<Map<String, Object>> selectIdAndVersionByStudentIdByPhase(@RequestParam Long studentId, @RequestParam String phase);

    /**
     * 查询课程下各个单元单词数
     *
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/selectUnitsWordSum", method = RequestMethod.GET)
    Map<Long, Map<String, Object>> selectUnitsWordSum(@RequestParam long courseId);

    /**
     * []
     * 根据id获取课程数据信息
     *
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    CourseNew getById(@PathVariable Long id);

    /**
     * 通过单元id查询当前课程所属学段
     *
     * @param unitId
     * @return
     */
    @GetMapping("/getPhaseByUnitId/{unitId}")
    String getPhaseByUnitId(@PathVariable Long unitId);

    /**
     * 根据单元id查询单元与年级的关系
     *
     * @param unitIds
     * @return
     */
    @GetMapping("/getGradeAndLabelByUnitIds")
    List<GradeAndUnitIdDTO> getGradeAndLabelByUnitIds(@RequestParam List<Long> unitIds);

    /**
     * 批量获取课程信息
     *
     * @param courseIds
     * @return
     */
    @GetMapping("/getByIds")
    List<CourseNew> getByIds(@RequestParam List<Long> courseIds);

    /**
     * 获取学生可以学习的版本集合
     *
     * @param courseIds
     * @return
     */
    @GetMapping("/getByIdsGroupByVersion")
    List<CourseNew> getByIdsGroupByVersion(@RequestParam List<Long> courseIds);

    /**
     * 各个课程下所有单元个数
     *
     * @param courseIds
     * @param type      1：单词；2：句型；3：语法；4：课文；5：金币试卷
     * @return
     */
    @GetMapping("/countUnitByIds")
    Map<Long, Integer> countUnitByIds(@RequestParam List<Long> courseIds, @RequestParam int type);

    /**
     * 当前版本中小于或等于当前年级的所有课程id
     *
     * @param version   版本
     * @param gradeList 年级
     * @param type      1：单词；2：句型；3：语法；4：课文；5：金币试卷
     * @return
     */
    @GetMapping("/getByGradeListAndVersionAndGrade")
    List<Long> getByGradeListAndVersionAndGrade(@RequestParam String version, @RequestParam List<String> gradeList, @RequestParam Integer type);

    /**
     * 获取当前课程的语法数据
     *
     * @param courseNews
     * @return
     */
    @PostMapping("/getByCourseNews")
    Map<Long, Map<String, Object>> getByCourseNews(@RequestBody List<CourseNew> courseNews);

    /**
     * 获取单元数据
     */
    /**
     * 根据id获取单元信息
     *
     * @param id
     * @return
     */
    @GetMapping("/getUnitNewById/{id}")
    UnitNew getUnitNewById(@PathVariable Long id);


    @GetMapping("/selectGradeByCourseId")
    String selectGradeByCourseId(@RequestParam Long courseId);

    /**
     * 根据id获取当前课程的年级
     *
     * @param courseId
     * @return
     */
    @GetMapping("/getGradeById")
    String getGradeById(@RequestParam Long courseId);

    /**
     * 根据单元id查询课程信息
     *
     * @param unitId
     * @return
     */
    @GetMapping("/getByUnitId")
    CourseNew getByUnitId(@RequestParam Long unitId);

    /**
     * 获取版本下所有课程id
     *
     * @param version
     * @return
     */
    @GetMapping("/getIdsByVersion")
    List<Long> getIdsByVersion(@RequestParam String version);

    /**
     * 从课程ids中过滤出指定学段的课程id
     *
     * @param phase
     * @param courseIds
     * @return
     */
    @GetMapping("/getIdsByPhaseAndIds")
    List<Long> getIdsByPhaseAndIds(@RequestParam String phase, @RequestParam List<Long> courseIds);
}
