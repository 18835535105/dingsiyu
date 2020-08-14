package com.zhidejiaoyu.student.business.feignclient.course;

import com.zhidejiaoyu.common.pojo.Unit;
import com.zhidejiaoyu.common.pojo.UnitNew;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "course", path = "/course/unit")
public interface UnitFeignClient extends CourseFeignClient {
    /**
     * 查询指定课程名的所有单元id
     *
     * @param courseNames
     * @return
     */
    @GetMapping("/getUnitIdsByCourseNames")
    List<Long> getUnitIdsByCourseNames(@RequestParam List<String> courseNames);

    /**
     * 批量查询单元信息
     *
     * @param unitIds
     * @return
     */
    @GetMapping("/getUnitNewsByIds")
    List<UnitNew> getUnitNewsByIds(@RequestParam List<Long> unitIds);

    /**
     * 获取当前版本、年级的所有单元id
     *
     * @param version
     * @param gradeList
     * @return
     */
    @GetMapping("/getUnitIdsByGradeListAndVersionAndGrade")
    List<Long> getUnitIdsByGradeListAndVersionAndGrade(@RequestParam String version, @RequestParam List<String> gradeList);

    /**
     * 获取当前课程中小于或等于当前单元的所有单元id
     *
     * @param courseId
     * @param unitId
     * @return
     */
    @GetMapping("/getLessOrEqualsCurrentIdByCourseIdAndUnitId")
    List<Long> getLessOrEqualsCurrentUnitIdByCourseIdAndUnitId(@RequestParam Long courseId, @RequestParam Long unitId);

    /**
     * 根据类型查询单元最大group
     *
     * @param unitIds
     * @param type    1：单词；2：句型；4：课文
     * @return
     */
    @GetMapping("/getMaxGroupByUnitIsdAndType")
    ServerResponse<Map<Long, Integer>> getMaxGroupByUnitIsdAndType(@RequestParam List<Long> unitIds, @RequestParam Integer type);

    /**
     * 根据unitId获取unit信息
     *
     * @param unitId
     * @return
     */
    @GetMapping("/selectById")
    UnitNew selectById(@RequestParam Long unitId);

    /**
     * 根据courseId获取最大单元index
     *
     * @param courseId
     * @return
     */
    @GetMapping("/selectMaxUnitIndexByCourseId")
    Integer selectMaxUnitIndexByCourseId(@RequestParam Long courseId);

    @GetMapping("/selectCurrentUnitIndexByUnitId")
    Integer selectCurrentUnitIndexByUnitId(@RequestParam Long unitId);

    @GetMapping("/selectNextUnitIndexByCourseId")
    Long selectNextUnitIndexByCourseId(@RequestParam Long courseId, @RequestParam Integer nextUnitIndex);

    @GetMapping("/selectFirstUnitByCourseId")
    Unit selectFirstUnitByCourseId(@RequestParam Long courseId);

    @GetMapping("/countWordByCourse")
    Integer countWordByCourse(@RequestParam Long courseId);

    @GetMapping("/selectCountByUnitIds")
    Map<Long, Map<String, Object>> selectCountByUnitIds(@RequestParam List<Long> unitIds);

    @GetMapping("/selectByUnitIdAndCourseId")
    int selectByUnitIdAndCourseId(@RequestParam Long unitId, @RequestParam Long courseId);

    @GetMapping("/selectUnitNameByUnitIds")
    Map<Long, Map<String, Object>> selectUnitNameByUnitIds(@RequestParam List<Long> unitIds);
}
