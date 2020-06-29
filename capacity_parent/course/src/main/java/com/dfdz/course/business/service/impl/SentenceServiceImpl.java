package com.dfdz.course.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.course.business.service.SentenceService;
import com.zhidejiaoyu.common.mapper.SentenceMapper;
import com.zhidejiaoyu.common.mapper.UnitSentenceNewMapper;
import com.zhidejiaoyu.common.pojo.Sentence;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SentenceServiceImpl extends ServiceImpl<SentenceMapper, Sentence> implements SentenceService {

    @Resource
    private UnitSentenceNewMapper unitSentenceNewMapper;

    @Override
    public String selectSentenceChineseByUnitIdAndSentenceId(Long unitId, Long sentenceId) {
        return unitSentenceNewMapper.selectSentenceChineseByUnitIdAndSentenceId(unitId, sentenceId);
    }
}
