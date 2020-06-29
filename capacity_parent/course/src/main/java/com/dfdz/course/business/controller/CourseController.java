package com.dfdz.course.business.controller;

import com.dfdz.course.business.service.*;
import com.zhidejiaoyu.common.pojo.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: wuchenxi
 * @date: 2020/6/28 15:07:07
 */
@RestController
@RequestMapping("/course")
public class CourseController {

    @Resource
    private CourseService courseService;
    @Resource
    private UnitService unitService;

    /**
     * 根据courseId获取课程数据
     *
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    public CourseNew getById(@PathVariable String id) {
        return courseService.getById(id);
    }

    /**
     * 根据id获取单元数据
     *
     * @param id
     * @return
     */
    @GetMapping("/getUnitNewById/{id}")
    public UnitNew getUnitNewById(@PathVariable Long id) {
        return unitService.getById(id);
    }








}
