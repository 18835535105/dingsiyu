package com.dfdz.course.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.Vocabulary;

import java.util.List;
import java.util.Map;

public interface VocabularyService extends IService<Vocabulary> {

    Integer countLearnVocabularyByUnitIdAndGroup(Long unitId,Integer group);

    List<Long> getWordIdByUnitIdAndGroup(Long unitId, Integer group);

    String getWordChineseByUnitIdAndWordId(Long unitId, Long wordId);

    Vocabulary getOneWordNotInIdsNew(List<Long> wordIds, Long unitId, Integer group);

    Integer countWordPictureByUnitId(Long unitId, Integer group);

    Map<String, Object> selectStudyMap(Long unitId, List<Long> wordIds, Integer type, Integer group);
}
