package com.dfdz.course.business.controller;

import com.dfdz.course.business.service.CourseService;
import com.zhidejiaoyu.common.pojo.CourseNew;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/6/28 15:07:07
 */
@RestController
@RequestMapping("/course")
public class CourseController {

    @Resource
    private CourseService courseService;

    @GetMapping("/getById/{id}")
    public CourseNew getById(@PathVariable String id) {
        return courseService.getById(id);
    }
}
