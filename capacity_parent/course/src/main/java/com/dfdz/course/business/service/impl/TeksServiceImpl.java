package com.dfdz.course.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.course.business.service.TeksService;
import com.zhidejiaoyu.common.mapper.TeksMapper;
import com.zhidejiaoyu.common.mapper.TeksNewMapper;
import com.zhidejiaoyu.common.pojo.Teks;
import com.zhidejiaoyu.common.pojo.TeksNew;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TeksServiceImpl extends ServiceImpl<TeksMapper, Teks> implements TeksService {

    @Resource
    private TeksNewMapper teksNewMapper;

    @Override
    public List<TeksNew> selTeksByUnitIdAndGroup(Long unitId, Integer group) {
        return teksNewMapper.selTeksByUnitIdAndGroup(unitId, group);
    }

    @Override
    public List<TeksNew> getTwentyTeks() {
        return teksNewMapper.getTwentyTeks();
    }
}
