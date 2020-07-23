package com.dfdz.course.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.CourseNew;

/**
 * @author: wuchenxi
 * @date: 2020/6/28 15:09:09
 */
public interface CourseService extends IService<CourseNew> {
    CourseNew getCourseById(String id);
}
