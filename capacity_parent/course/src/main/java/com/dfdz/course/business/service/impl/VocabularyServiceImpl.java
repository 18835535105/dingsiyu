package com.dfdz.course.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.course.business.service.VocabularyService;
import com.zhidejiaoyu.common.mapper.VocabularyMapper;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import org.springframework.stereotype.Service;

@Service
public class VocabularyServiceImpl extends ServiceImpl<VocabularyMapper, Vocabulary> implements VocabularyService {
}
