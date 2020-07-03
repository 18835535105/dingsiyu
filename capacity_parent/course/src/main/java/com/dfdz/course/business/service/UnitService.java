package com.dfdz.course.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.UnitNew;
import com.zhidejiaoyu.common.pojo.Vocabulary;

public interface UnitService extends IService<UnitNew> {

    UnitNew getUnitById(Long id);
}
