package com.dfdz.course.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.course.business.service.UnitService;
import com.zhidejiaoyu.common.mapper.UnitNewMapper;
import com.zhidejiaoyu.common.pojo.UnitNew;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UnitServiceImpl extends ServiceImpl<UnitNewMapper, UnitNew> implements UnitService {

    @Resource
    private UnitNewMapper unitNewMapper;

    @Override
    public UnitNew getUnitById(Long id) {
        return unitNewMapper.selectById(id);
    }

    @Override
    public ServerResponse<Map<Long, Integer>> getMaxGroupByUnitIsdAndType(List<Long> unitIds, Integer type) {
        Map<Long, Map<Long, Integer>> unitGroupMap = unitNewMapper.selectMaxGroupByUnitIsdAndType(unitIds, type);
        Map<Long, Integer> unitGroup = new HashMap<>(unitGroupMap.size());

        unitIds.forEach(id -> unitGroup.put(id, unitGroupMap.get(id).get("group")));
        return ServerResponse.createBySuccess(unitGroup);
    }

    @Override
    public List<UnitNew> getByCourseNews(List<Long> courseIds) {
        return unitNewMapper.selectByCourseIds(courseIds);
    }

    @Override
    public List<Map<String, Long>> selectMapByGradeListAndVersionAndGrade(String version, List<String> smallGradeList) {
        return unitNewMapper.selectMapByGradeListAndVersionAndGrade(version, smallGradeList);
    }

    @Override
    public List<Map<String, Long>> selectMapLessOrEqualsCurrentIdByCourseIdAndUnitId(Long courseId, Long unitId) {
        return unitNewMapper.selectMapLessOrEqualsCurrentIdByCourseIdAndUnitId(courseId, unitId);
    }

    @Override
    public List<Map<String, Long>> selectIdsMapByCourseNames(List<String> courseNames) {
        return unitNewMapper.selectIdsMapByCourseNames(courseNames);
    }

    @Override
    public List<Map<String, Object>> selectMapByIds(List<Long> unitIds) {
        return unitNewMapper.selectByIds(unitIds);
    }

    @Override
    public List<Long> getUnitIdsByGradeListAndVersionAndGrade(String version, List<String> gradeList) {
        return unitNewMapper.selectByGradeListAndVersionAndGrade(version, gradeList);
    }

    @Override
    public List<Long> getLessOrEqualsCurrentUnitIdByCourseIdAndUnitId(Long courseId, Long unitId) {
        return unitNewMapper.selectLessOrEqualsCurrentIdByCourseIdAndUnitId(courseId, unitId);
    }

    @Override
    public List<Long> getUnitIdsByCourseNames(List<String> courseNames) {
        return unitNewMapper.selectIdsByCourseNames(courseNames);
    }
}
