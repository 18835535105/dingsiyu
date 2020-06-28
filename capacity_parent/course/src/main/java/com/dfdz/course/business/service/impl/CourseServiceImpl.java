package com.dfdz.course.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.course.business.service.CourseService;
import com.zhidejiaoyu.common.mapper.CourseNewMapper;
import com.zhidejiaoyu.common.pojo.CourseNew;
import org.springframework.stereotype.Service;

/**
 * @author: wuchenxi
 * @date: 2020/6/28 15:09:09
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseNewMapper, CourseNew> implements CourseService {
}