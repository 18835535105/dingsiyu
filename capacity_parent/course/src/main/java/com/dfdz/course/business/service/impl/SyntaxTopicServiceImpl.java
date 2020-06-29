package com.dfdz.course.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.course.business.service.SyntaxTopicService;
import com.zhidejiaoyu.common.mapper.SyntaxTopicMapper;
import com.zhidejiaoyu.common.pojo.SyntaxTopic;
import org.springframework.stereotype.Service;

@Service
public class SyntaxTopicServiceImpl extends ServiceImpl<SyntaxTopicMapper, SyntaxTopic> implements SyntaxTopicService {
}
