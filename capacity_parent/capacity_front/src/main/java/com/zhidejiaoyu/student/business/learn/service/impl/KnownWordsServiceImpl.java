package com.zhidejiaoyu.student.business.learn.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.mapper.KnownWordsMapper;
import com.zhidejiaoyu.common.pojo.KnownWords;
import com.zhidejiaoyu.student.business.learn.service.KnownWordsService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 记录学生的熟词信息，用于每周活动统计学生熟词数量 服务实现类
 * </p>
 *
 * @author zdjy
 * @since 2020-05-28
 */
@Service
public class KnownWordsServiceImpl extends ServiceImpl<KnownWordsMapper, KnownWords> implements KnownWordsService {

}
