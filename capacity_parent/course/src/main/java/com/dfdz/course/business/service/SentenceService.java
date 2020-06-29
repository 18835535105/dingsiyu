package com.dfdz.course.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.Sentence;

public interface SentenceService extends IService<Sentence> {
    String selectSentenceChineseByUnitIdAndSentenceId(Long unitId, Long sentenceId);
}
