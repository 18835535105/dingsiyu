package com.zhidejioayu.center.business.joinSchool.controller;

import com.zhidejiaoyu.common.pojo.JoinSchool;
import com.zhidejioayu.center.business.joinSchool.service.JoinSchoolService;
import com.zhidejiaoyu.common.vo.joinSchool.JoinSchoolDto;
import org.springframework.web.bind.annotation.GetMapping;
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
    @RequestMapping(value = "/selSchoolByAddress", produces = "application/json;charset=utf-8")
    @ResponseBody
    public Object selSchoolByAddress(String address) {
        return joinSchoolService.selSchoolByAddress(address);
    }

    /**
     * 根据地址搜索学校信息
     *
     * @param joinSchool 输入的地址
     * @return
     */
    @RequestMapping(value = "/add", produces = "application/json;charset=utf-8")
    @ResponseBody
    public Object add(JoinSchool joinSchool) {
        return joinSchoolService.addService(joinSchool);
    }


    /**
     * 根据地址搜索学校信息
     *
     * @param joinSchoolId 输入的地址
     * @return
     */
    @RequestMapping(value = "/selectById", produces = "application/json;charset=utf-8")
    @ResponseBody
    public JoinSchool selectById(String joinSchoolId) {
        return joinSchoolService.selectById(joinSchoolId);
    }

    /**
     * 获取列表
     */
    @GetMapping(value = "/list", produces = "application/json;charset=utf-8")
    @ResponseBody
    public Object list(JoinSchoolDto joinSchoolDto) {
        return joinSchoolService.selListJoinSchool(joinSchoolDto);
    }

    /**
     * 修改joinSchool
     */
    @GetMapping(value = "/updateJoinSchool", produces = "application/json;charset=utf-8")
    @ResponseBody
    public Object updateJoinSchool(String uuid, String joinSchoolId) {
        return joinSchoolService.updateJoinSchool(uuid, joinSchoolId);
    }
}
