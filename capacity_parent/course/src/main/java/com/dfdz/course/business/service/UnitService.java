package com.dfdz.course.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.Unit;
import com.zhidejiaoyu.common.pojo.UnitNew;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import java.util.List;
import java.util.Map;

public interface UnitService extends IService<UnitNew> {

    UnitNew getUnitById(Long id);

    /**
     * 根据类型查询单元最大group
     *
     * @param unitIds
     * @param type    1：单词；2：句型；3：语法；4：课文
     * @return
     */
    ServerResponse<Map<Long, Integer>> getMaxGroupByUnitIsdAndType(List<Long> unitIds, Integer type);

    /**
     * 根据courseIds查询unit信息
     * @param courseIds
     * @return
     */
    List<UnitNew> getByCourseNews(List<Long> courseIds);

    List<Map<String, Long>> selectMapByGradeListAndVersionAndGrade(String version, List<String> smallGradeList);

    List<Map<String, Long>> selectMapLessOrEqualsCurrentIdByCourseIdAndUnitId(Long courseId, Long unitId);

    List<Map<String, Long>> selectIdsMapByCourseNames(List<String> courseNames);

    List<Map<String, Object>> selectMapByIds(List<Long> unitIds);

    /**
     * 获取当前版本、年级的所有单元id
     *
     * @param version
     * @param gradeList
     * @return
     */
    List<Long> getUnitIdsByGradeListAndVersionAndGrade(String version, List<String> gradeList);

    /**
     * 获取当前课程中小于或等于当前单元的所有单元id
     *
     * @param courseId
     * @param unitId
     * @return
     */
    List<Long> getLessOrEqualsCurrentUnitIdByCourseIdAndUnitId(Long courseId, Long unitId);

    /**
     * 查询指定课程名的所有单元id
     *
     * @param courseNames
     * @return
     */
    List<Long> getUnitIdsByCourseNames(List<String> courseNames);

    UnitNew selectById(Long unitId);

    Integer selectMaxUnitIndexByCourseId(Long courseId);

    Integer selectCurrentUnitIndexByUnitId(Long unitId);

    Long selectNextUnitIndexByCourseId(Long courseId, Integer nextUnitIndex);

    Unit selectFirstUnitByCourseId(Long courseId);

    Integer countWordByCourse(Long courseId);

    Map<Long, Map<String, Object>> selectCountByUnitIds(List<Long> unitIds);

    int selectByUnitIdAndCourseId(Long unitId, Long courseId);

    UnitNew getNextSyntaxUnitByCourseId(Long unitId, Long courseId);

    UnitNew getSyntaxMaxUnitByCourseId(Long courseId);

    UnitNew getSyntaxUnitLikeJointName(String jointName);

    Map<Long, Map<String, Object>> selectUnitNameByUnitIds(List<Long> unitIds);
}
