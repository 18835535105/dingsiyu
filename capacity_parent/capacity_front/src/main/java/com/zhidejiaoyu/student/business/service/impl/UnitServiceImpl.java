package com.zhidejiaoyu.student.business.service.impl;

import com.zhidejiaoyu.common.mapper.UnitMapper;
import com.zhidejiaoyu.common.pojo.Unit;
import com.zhidejiaoyu.student.business.service.UnitService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @Date: 2019/10/18 09:38
 */
@Service
public class UnitServiceImpl extends BaseServiceImpl<UnitMapper, Unit> implements UnitService {

    @Resource
    private UnitMapper unitMapper;

    @Override
    public Long selectCourseIdById(Long unitId) {
        if (unitId == null) {
            return null;
        }
        return unitMapper.selectCourseIdByUnitId(unitId);
    }
}
