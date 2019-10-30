package com.zhidejiaoyu.student.syntax.service.impl;

import com.zhidejiaoyu.common.mapper.SyntaxTopicMapper;
import com.zhidejiaoyu.common.pojo.SyntaxTopic;
import com.zhidejiaoyu.student.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.syntax.service.SyntaxService;
import org.springframework.stereotype.Service;

@Service
public class SyntaxServiceImpl extends BaseServiceImpl<SyntaxTopicMapper, SyntaxTopic> implements SyntaxService {
}
