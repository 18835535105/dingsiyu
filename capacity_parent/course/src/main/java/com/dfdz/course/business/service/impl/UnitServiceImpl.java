package com.dfdz.course.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.course.business.service.UnitService;
import com.zhidejiaoyu.common.mapper.UnitNewMapper;
import com.zhidejiaoyu.common.pojo.UnitNew;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UnitServiceImpl extends ServiceImpl<UnitNewMapper, UnitNew> implements UnitService {

    @Resource
    private UnitNewMapper unitNewMapper;

    @Override
    public UnitNew getUnitById(Long id) {
        UnitNew unitNew = unitNewMapper.selectById(id);
        return unitNew;
    }
}
