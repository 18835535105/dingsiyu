package com.dfdz.course.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.Sentence;
import com.zhidejiaoyu.common.pojo.TeksNew;

import java.util.List;
import java.util.Map;

public interface SentenceService extends IService<Sentence> {
    String selectSentenceChineseByUnitIdAndSentenceId(Long unitId, Long sentenceId);

    List<Long> getSentenceIdsByUnitIdAndGroup(Long unitId, Integer group);

    Sentence getReplaceTeks(String sentence);

    Integer countSentenceByUnitIdAndGroup(Long unitId, Integer group);

    Sentence selectOneWordNotInIdsNew(List<Long> wordIds, Long unitId, Integer group);

    List<Map<String, Object>> selectSentenceAndChineseByUnitIdAndGroup(Long unitId, Integer group);

    List<Sentence> selectByUnitIdAndGroup(Long unitId, Integer group);

    List<Sentence> selectRoundSentence(Long courseId);
}
