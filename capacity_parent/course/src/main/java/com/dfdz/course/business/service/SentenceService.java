package com.dfdz.course.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.Sentence;
import com.zhidejiaoyu.common.pojo.TeksNew;

import java.util.List;

public interface SentenceService extends IService<Sentence> {
    String selectSentenceChineseByUnitIdAndSentenceId(Long unitId, Long sentenceId);

    List<Long> getSentenceIdsByUnitIdAndGroup(Long unitId, Integer group);

    Sentence getReplaceTeks(String sentence);

    Integer countSentenceByUnitIdAndGroup(Long unitId, Integer group);

    Sentence selectOneWordNotInIdsNew(List<Long> wordIds, Long unitId, Integer group);
}
