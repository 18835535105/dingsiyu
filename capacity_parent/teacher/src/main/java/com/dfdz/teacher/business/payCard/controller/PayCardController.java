package com.dfdz.teacher.business.payCard.controller;

import com.dfdz.teacher.business.payCard.service.PayCardService;
import com.zhidejiaoyu.common.dto.wechat.qy.teacher.PayStudentsDTO;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/payCard")
public class PayCardController {

    @Resource
    private PayCardService payCardService;


    /**
     * 一键充值
     *
     * @param studentUUID 学生uuid
     * @param type        有效期充值天数
     * @return
     */
    @ResponseBody
    @PostMapping("/pay")
    public Object pay(String studentUUID, Integer type, String openId) {
        if (studentUUID == null) {
            return ServerResponse.createByError(400, "请选择学生");
        }
        if (type == null) {
            return ServerResponse.createByError(400, "请填写充课月数");

        }
        // 如果充值日期不是31天也不是92天，初始化充值日期
        return payCardService.pay(studentUUID, type, openId);
    }

    @RequestMapping("/addMoreStudent")
    @ResponseBody
    public Object addMoreStudent(@RequestBody PayStudentsDTO dto) {
        return payCardService.addAllStudent(dto);
    }

    /**
     * 获取学管队长币
     *
     * @param openId 学管openId
     * @return
     */
    @GetMapping("/getCapacityCoin")
    public ServerResponse<Object> getCapacityIcon(@RequestParam String openId) {
        return payCardService.getCapacityCoin(openId);
    }

}
