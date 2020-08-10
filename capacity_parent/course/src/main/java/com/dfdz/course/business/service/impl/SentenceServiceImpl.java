package com.dfdz.course.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.course.business.service.SentenceService;
import com.zhidejiaoyu.common.mapper.SentenceMapper;
import com.zhidejiaoyu.common.mapper.UnitSentenceNewMapper;
import com.zhidejiaoyu.common.pojo.Sentence;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class SentenceServiceImpl extends ServiceImpl<SentenceMapper, Sentence> implements SentenceService {

    @Resource
    private UnitSentenceNewMapper unitSentenceNewMapper;
    @Resource
    private SentenceMapper sentenceMapper;

    @Override
    public String selectSentenceChineseByUnitIdAndSentenceId(Long unitId, Long sentenceId) {
        return unitSentenceNewMapper.selectSentenceChineseByUnitIdAndSentenceId(unitId, sentenceId);
    }

    @Override
    public List<Long> getSentenceIdsByUnitIdAndGroup(Long unitId, Integer group) {
        return unitSentenceNewMapper.selectSentenceIdByUnitIdAndGroup(unitId, group);
    }

    @Override
    public Sentence getReplaceTeks(String sentence) {
        return sentenceMapper.replaceSentence(sentence);
    }

    @Override
    public Integer countSentenceByUnitIdAndGroup(Long unitId, Integer group) {
        return unitSentenceNewMapper.countByUnitIdAndGroup(unitId, group);
    }

    @Override
    public Sentence selectOneWordNotInIdsNew(List<Long> wordIds, Long unitId, Integer group) {
        return sentenceMapper.selectOneWordNotInIdsNew(wordIds, unitId, group);
    }

    @Override
    public List<Map<String, Object>> selectSentenceAndChineseByUnitIdAndGroup(Long unitId, Integer group) {
        return unitSentenceNewMapper.selectSentenceAndChineseByUnitIdAndGroup(unitId, group);
    }

    @Override
    public List<Sentence> selectByUnitIdAndGroup(Long unitId, Integer group) {
        return sentenceMapper.selectByUnitIdAndGroup(unitId,group);
    }

    @Override
    public List<Sentence> selectRoundSentence(Long courseId) {
        return sentenceMapper.selectRoundSentence(courseId);
    }
}
