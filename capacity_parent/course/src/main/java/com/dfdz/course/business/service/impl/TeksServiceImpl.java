package com.dfdz.course.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.course.business.service.TeksService;
import com.zhidejiaoyu.common.mapper.TeksMapper;
import com.zhidejiaoyu.common.pojo.Teks;
import org.springframework.stereotype.Service;

@Service
public class TeksServiceImpl extends ServiceImpl<TeksMapper, Teks> implements TeksService {
}
