package com.zhidejiaoyu.student.business.timingtask.controller;


import com.zhidejiaoyu.student.business.timingtask.service.QueryCurrentDayOfStudyService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/queryCurrentDayOfStudy")
public class QueryCurrentDayOfStudyController {

    @Resource
    private QueryCurrentDayOfStudyService queryCurrentDayOfStudyService;

    @RequestMapping("/getCurr")
    public void getCurr(){
         queryCurrentDayOfStudyService.saveCurrentDayOfStudy();
    }


}
