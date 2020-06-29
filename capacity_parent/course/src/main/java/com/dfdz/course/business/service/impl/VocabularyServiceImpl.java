package com.dfdz.course.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.course.business.service.VocabularyService;
import com.zhidejiaoyu.common.mapper.UnitVocabularyNewMapper;
import com.zhidejiaoyu.common.mapper.VocabularyMapper;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class VocabularyServiceImpl extends ServiceImpl<VocabularyMapper, Vocabulary> implements VocabularyService {

    @Resource
    private UnitVocabularyNewMapper unitVocabularyNewMapper;

    @Resource
    private VocabularyMapper vocabularyMapper;

    @Override
    public Integer countLearnVocabularyByUnitIdAndGroup(Long unitId,Integer group){
        return unitVocabularyNewMapper.countByUnitIdAndGroup(unitId, group);
    }

    @Override
    public List<Long> getWordIdByUnitIdAndGroup(Long unitId, Integer group) {
        return unitVocabularyNewMapper.selectWordIdByUnitIdAndGroup(unitId,group);
    }

    @Override
    public String getWordChineseByUnitIdAndWordId(Long unitId, Long wordId) {
        return unitVocabularyNewMapper.selectWordChineseByUnitIdAndWordId(unitId, wordId);
    }

    @Override
    public Vocabulary getOneWordNotInIdsNew(List<Long> wordIds, Long unitId, Integer group) {
        return vocabularyMapper.selectOneWordNotInIdsNew(wordIds,unitId,group);
    }

    @Override
    public Integer countWordPictureByUnitId(Long unitId, Integer group) {
        return unitVocabularyNewMapper.countWordPictureByUnitId(unitId, group);
    }


}
