package com.dfdz.course.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.Teks;
import com.zhidejiaoyu.common.pojo.TeksNew;

import java.util.List;

public interface TeksService extends IService<Teks> {

    List<TeksNew> selTeksByUnitIdAndGroup(Long unitId, Integer group);

    List<TeksNew> getTwentyTeks();

    TeksNew getReplaceTeks(String sentence);
}
