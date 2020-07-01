package com.zhidejioayu.center.business.joinSchool.controller;

import com.zhidejioayu.center.business.joinSchool.service.JoinSchoolService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 加盟校相关功能
 *
 * @author zdjy
 * @Date 2018-11-07 10:30:01
 */
@RestController
@RequestMapping("/joinSchool")
public class JoinSchoolController {

    @Resource
    private JoinSchoolService joinSchoolService;

    /**
     * 根据地址搜索学校信息
     *
     * @param address 输入的地址
     * @return
     */
    @RequestMapping(value = "/selSchoolByAddress")
    @ResponseBody
    public Object selSchoolByAddress(String address) {
        return joinSchoolService.selSchoolByAddress(address);
    }
}
