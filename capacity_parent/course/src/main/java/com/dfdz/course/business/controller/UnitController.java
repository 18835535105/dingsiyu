package com.dfdz.course.business.controller;

import com.dfdz.course.business.service.UnitService;
import com.zhidejiaoyu.common.pojo.UnitNew;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 单元相关
 *
 * @author: wuchenxi
 * @date: 2020/7/15 16:43:43
 */
@Slf4j
@RestController
@RequestMapping("/course/unit")
public class UnitController {

    @Resource
    private UnitService unitService;

    /**
     * 根据类型查询单元最大group
     *
     * @param unitIds
     * @param type    1：单词；2：句型；4：课文
     * @return
     */
    @GetMapping("/getMaxGroupByUnitIsdAndType")
    public ServerResponse<Map<Long, Integer>> getMaxGroupByUnitIsdAndType(@RequestParam List<Long> unitIds, @RequestParam Integer type) {
        if (CollectionUtils.isEmpty(unitIds)) {
            return ServerResponse.createByError(400, "unitIds can't be null!");
        }
        if (type == null) {
            return ServerResponse.createByError(400, "type can't be null!");
        }
        return unitService.getMaxGroupByUnitIsdAndType(unitIds, type);
    }

    /**
     * 根据courseIds查询unit信息
     *
     * @param courseIds
     * @return
     */
    @GetMapping(value = "/selectByCourseIds")
    public List<UnitNew> selectByCourseIds(@RequestParam List<Long> courseIds) {
        return unitService.getByCourseNews(courseIds);
    }

    /**
     * 查询课程添加单元数据
     *
     * @param version
     * @param smallGradeList
     * @return
     */
    @GetMapping(value = "/selectMapByGradeListAndVersionAndGrade")
    public List<Map<String, Long>> selectMapByGradeListAndVersionAndGrade(@RequestParam String version, @RequestParam List<String> smallGradeList) {
        return unitService.selectMapByGradeListAndVersionAndGrade(version, smallGradeList);
    }

    /**
     * 获取当前课程中小于或等于当前单元的所有单元id
     *
     * @param courseId
     * @param unitId
     * @return
     */
    @GetMapping(value = "/unit/selectMapLessOrEqualsCurrentIdByCourseIdAndUnitId")
    public List<Map<String, Long>> selectMapLessOrEqualsCurrentIdByCourseIdAndUnitId(@RequestParam Long courseId, @RequestParam Long unitId) {
        return unitService.selectMapLessOrEqualsCurrentIdByCourseIdAndUnitId(courseId, unitId);
    }

    /**
     * 查询指定课程名的所有单元id
     *
     * @param courseNames
     * @return
     */
    @GetMapping(value = "/unit/selectIdsMapByCourseNames")
    public List<Map<String, Long>> selectIdsMapByCourseNames(@RequestParam List<String> courseNames) {
        return unitService.selectIdsMapByCourseNames(courseNames);
    }

    /**
     * 使用unitIds获取map
     * @param unitIds
     * @return
     */
    @RequestMapping(value = "/unit/selectMapByIds", method = RequestMethod.GET)
    public List<Map<String, Object>> selectMapByIds(@RequestParam List<Long> unitIds){
        return unitService.selectMapByIds(unitIds);
    }

    /**
     * 批量查询单元信息
     *
     * @param unitIds
     * @return
     */
    @GetMapping("/getUnitNewsByIds")
    public List<UnitNew> getUnitNewsByIds(@RequestParam List<Long> unitIds) {
        if (CollectionUtils.isEmpty(unitIds)) {
            return Collections.emptyList();
        }
        return unitService.listByIds(unitIds);
    }

    /**
     * 获取当前版本、年级的所有单元id
     *
     * @param version
     * @param gradeList
     * @return
     */
    @GetMapping("/getUnitIdsByGradeListAndVersionAndGrade")
    public List<Long> getUnitIdsByGradeListAndVersionAndGrade(@RequestParam String version, @RequestParam List<String> gradeList) {
        if (StringUtil.isEmpty(version)) {
            log.warn("version 参数可能异常！ version={}", version);
            return Collections.emptyList();
        }
        if (CollectionUtils.isEmpty(gradeList)) {
            log.warn("gradeList 参数可能异常！ gradeList=null or emptyList");
            return Collections.emptyList();
        }
        return unitService.getUnitIdsByGradeListAndVersionAndGrade(version, gradeList);
    }

    /**
     * 获取当前课程中小于或等于当前单元的所有单元id
     *
     * @param courseId
     * @param unitId
     * @return
     */
    @GetMapping("/getLessOrEqualsCurrentIdByCourseIdAndUnitId")
    public List<Long> getLessOrEqualsCurrentUnitIdByCourseIdAndUnitId(Long courseId, Long unitId) {
        if (courseId == null || unitId == null) {
            return Collections.emptyList();
        }
        return unitService.getLessOrEqualsCurrentUnitIdByCourseIdAndUnitId(courseId, unitId);
    }

    /**
     * 查询指定课程名的所有单元id
     *
     * @param courseNames
     * @return
     */
    @GetMapping("/getUnitIdsByCourseNames")
    public List<Long> getUnitIdsByCourseNames(@RequestParam List<String> courseNames) {
        if (CollectionUtils.isEmpty(courseNames)) {
            return Collections.emptyList();
        }
        return unitService.getUnitIdsByCourseNames(courseNames);
    }
}
