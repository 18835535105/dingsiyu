package com.zhidejiaoyu.student.business.timingtask.controller;

import com.zhidejiaoyu.student.business.timingtask.service.QuartzStudyCalendarService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/3/31 15:44:44
 */
@RestController
@RequestMapping("/quartzStudyCalendar")
public class QuartzStudyCalendarController {

    @Resource
    private QuartzStudyCalendarService quartzStudyCalendarService;
}
