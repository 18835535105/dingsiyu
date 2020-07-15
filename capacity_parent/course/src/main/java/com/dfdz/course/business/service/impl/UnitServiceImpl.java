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
}
