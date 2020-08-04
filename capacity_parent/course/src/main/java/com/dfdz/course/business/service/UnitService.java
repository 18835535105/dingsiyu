package com.dfdz.course.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.UnitNew;
import com.zhidejiaoyu.common.pojo.Vocabulary;
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
}
