package com.dfdz.teacher.business.joinSchool.controller;

import com.dfdz.teacher.business.joinSchool.service.JoinSchoolService;
import com.zhidejiaoyu.common.dto.joinSchool.JoinSchoolDto;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RequestMapping("/joinSchool")
@RestController
public class JoinSchoolController {


    @Resource
    private JoinSchoolService joinSchoolService;


    @RequestMapping("/list")
    public Object list(@SpringQueryMap JoinSchoolDto dto) {
        return joinSchoolService.getList(dto);
    }


}
