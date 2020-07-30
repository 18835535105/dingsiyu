package com.dfdz.teacher.business.payCard.controller;

import com.dfdz.teacher.business.payCard.service.PayCardService;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;

@RestController
@RequestMapping("/payCard")
public class PayCardController {

    @Resource
    private PayCardService payCardService;


    /**
     * 一键充值
     *
     * @param id   学生id
     * @param type 有效期充值天数
     * @return
     */
    @ResponseBody
    @PostMapping("/pay")
    public Object pay(Long id, Integer type,String adminUUID) {
        if (id == null) {
            return ServerResponse.createByError(400, "请选择学生");
        }
        if (type == null) {
            return ServerResponse.createByError(400, "请填写充课月数");

        }
        // 如果充值日期不是31天也不是92天，初始化充值日期
        return payCardService.pay(id, type,adminUUID);
    }

    @RequestMapping("/addMoreStudent")
    @ResponseBody
    public Object addMoreStudent(Integer[] studentIds, Integer type,String adminUUID) {
        if (studentIds == null || studentIds.length == 0) {
            return ServerResponse.createByError(400, "请添加学生");
        }
        if (type == null && type<=0) {
            return ServerResponse.createByError(400, "请选择充课课时");
        }
        return payCardService.addAllStudent(Arrays.asList(studentIds), type,adminUUID);
    }



}
