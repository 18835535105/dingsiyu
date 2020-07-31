package com.zhidejioayu.center.business.payCard.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.payCard.service.PayCardService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;

@RestController
@RequestMapping("/teacher/payCard")
public class PayCardController {

    @Resource
    private PayCardService payCardService;

    /**
     * 一键充值
     *
     * @param studentUUID   学生id
     * @param type 有效期充值天数
     * @return
     */
    @ResponseBody
    @PostMapping("/pay")
    public Object pay(String studentUUID, Integer type, String adminUUID) {
        if (studentUUID == null) {
            return ServerResponse.createByError(400, "请选择学生");
        }
        if (type == null) {
            return ServerResponse.createByError(400, "请填写充课月数");

        }
        // 如果充值日期不是31天也不是92天，初始化充值日期

        return payCardService.pay(studentUUID, type, adminUUID);
    }

    @RequestMapping("/addMoreStudent")
    @ResponseBody
    public Object addMoreStudent(String[] studentIds, Integer type,String adminUUID) {
        if (studentIds == null || studentIds.length == 0) {
            return ServerResponse.createByError(400, "请添加学生");
        }
        if (type == null && type<=0) {
            return ServerResponse.createByError(400, "请选择充课课时");
        }
        return payCardService.addAllStudent(studentIds, type,adminUUID);
    }
}