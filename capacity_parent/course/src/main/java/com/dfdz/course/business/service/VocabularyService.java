package com.dfdz.course.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.Vocabulary;

import java.util.List;
import java.util.Map;

public interface VocabularyService extends IService<Vocabulary> {

    Integer countLearnVocabularyByUnitIdAndGroup(Long unitId, Integer group);

    List<Long> getWordIdByUnitIdAndGroup(Long unitId, Integer group);

    String getWordChineseByUnitIdAndWordId(Long unitId, Long wordId);

    Vocabulary getOneWordNotInIdsNew(List<Long> wordIds, Long unitId, Integer group);

    Integer countWordPictureByUnitId(Long unitId, Integer group);

    Map<String, Object> selectStudyMap(Long unitId, List<Long> wordIds, Integer type, Integer group);

    String getVocabularyChinsesByWordId(String word);

    /**
     * 查询单词及单词读音
     *
     * @param words
     * @return
     */
    List<Map<String, String>> getWordAndReadUrlByWords(List<String> words);

    List<Vocabulary> getVocabularyByUnitId(Long unitId);

    List<String> selectChineseByNotVocabularyIds(List<Long> vocabularyIds);

    List<Vocabulary> getVocabularyMapByVocabularys(List<Long> vocabularyIds);
}
