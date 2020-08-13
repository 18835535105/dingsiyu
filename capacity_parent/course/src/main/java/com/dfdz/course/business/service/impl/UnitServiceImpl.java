package com.dfdz.course.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.course.business.service.UnitService;
import com.zhidejiaoyu.common.mapper.UnitNewMapper;
import com.zhidejiaoyu.common.mapper.simple.SimpleUnitMapper;
import com.zhidejiaoyu.common.pojo.Unit;
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
    @Resource
    private SimpleUnitMapper simpleUnitMapper;

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

    @Override
    public UnitNew selectById(Long unitId) {
        return unitNewMapper.selectById(unitId);
    }

    @Override
    public Integer selectMaxUnitIndexByCourseId(Long courseId) {
        return unitNewMapper.selectMaxUnitIndexByCourseId(courseId);
    }

    @Override
    public Integer selectCurrentUnitIndexByUnitId(Long unitId) {
        return simpleUnitMapper.selectCurrentUnitIndexByUnitId(unitId);
    }

    @Override
    public Long selectNextUnitIndexByCourseId(Long courseId, Integer nextUnitIndex) {
        return simpleUnitMapper.selectNextUnitIndexByCourseId(courseId, nextUnitIndex);
    }

    @Override
    public Unit selectFirstUnitByCourseId(Long courseId) {
        return simpleUnitMapper.selectFirstUnitByCourseId(courseId);
    }

    @Override
    public Integer countWordByCourse(Long courseId) {
        return simpleUnitMapper.countWordByCourse(courseId);
    }

    @Override
    public Map<Long, Map<String, Object>> selectCountByUnitIds(List<Long> unitIds) {
        return unitNewMapper.selectCountByUnitIds(unitIds);
    }

    @Override
    public int selectByUnitIdAndCourseId(Long unitId, Long courseId) {
        return unitNewMapper.selectByUnitIdAndCourseId(unitId, courseId);
    }

    @Override
    public UnitNew getNextSyntaxUnitByCourseId(Long unitId, Long courseId) {
        return unitNewMapper.selectNextSyntaxUnitByCourseId(unitId, courseId);
    }

    @Override
    public UnitNew getSyntaxMaxUnitByCourseId(Long courseId) {
        return unitNewMapper.selectSyntaxMaxUnitByCourseId(courseId);
    }

    @Override
    public UnitNew getSyntaxUnitLikeJointName(String jointName) {
        return unitNewMapper.selectSyntaxUnitLikeJointName(jointName);
    }
}
