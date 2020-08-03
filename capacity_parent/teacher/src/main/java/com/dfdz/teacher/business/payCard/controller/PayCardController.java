package com.dfdz.teacher.business.payCard.controller;

import com.dfdz.teacher.business.payCard.service.PayCardService;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/payCard")
public class PayCardController {

    @Resource
    private PayCardService payCardService;


    /**
     * 一键充值
     *
     * @param studentUUID   学生uuid
     * @param type 有效期充值天数
     * @return
     */
    @ResponseBody
    @PostMapping("/pay")
    public Object pay(String studentUUID, Integer type,String adminUUID) {
        if (studentUUID == null) {
            return ServerResponse.createByError(400, "请选择学生");
        }
        if (type == null) {
            return ServerResponse.createByError(400, "请填写充课月数");

        }
        // 如果充值日期不是31天也不是92天，初始化充值日期
        return payCardService.pay(studentUUID, type,adminUUID);
    }

    @RequestMapping("/addMoreStudent")
    @ResponseBody
    public Object addMoreStudent(List<String> studentIds, Integer type, String adminUUID) {
        if (studentIds == null || studentIds.size() == 0) {
            return ServerResponse.createByError(400, "请添加学生");
        }
        if (type == null && type<=0) {
            return ServerResponse.createByError(400, "请选择充课课时");
        }
        return payCardService.addAllStudent(studentIds, type,adminUUID);
    }

    /**
     * 获取学管队长币
     *
     * @param openId    学管openId
     * @return
     */
    @GetMapping("/getCapacityCoin")
    public ServerResponse<Object> getCapacityIcon(@RequestParam String openId) {
        return payCardService.getCapacityCoin(openId);
    }

}
