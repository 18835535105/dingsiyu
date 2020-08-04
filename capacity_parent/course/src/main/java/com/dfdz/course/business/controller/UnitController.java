package com.dfdz.course.business.controller;

import com.dfdz.course.business.service.UnitService;
import com.zhidejiaoyu.common.pojo.UnitNew;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.apache.commons.collections.CollectionUtils;
import org.omg.CORBA.ServerRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 单元相关
 *
 * @author: wuchenxi
 * @date: 2020/7/15 16:43:43
 */
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
    public List<UnitNew> selectByCourseIds(@RequestBody List<Long> courseIds) {
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
    public List<Map<String, Long>> selectIdsMapByCourseNames(@RequestBody List<String> courseNames) {
        return unitService.selectIdsMapByCourseNames(courseNames);
    }

    /**
     * 使用unitIds获取map
     * @param unitIds
     * @return
     */
    @RequestMapping(value = "/unit/selectMapByIds", method = RequestMethod.GET)
    public List<Map<String, Object>> selectMapByIds(@RequestBody List<Long> unitIds){
        return unitService.selectMapByIds(unitIds);
    }
}
