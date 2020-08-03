package com.zhidejioayu.center.business.payCard.controller;

import com.zhidejiaoyu.common.dto.wechat.qy.teacher.PayStudentsDTO;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.feignclient.teacher.BaseTeacherPayCardFeignClient;
import com.zhidejioayu.center.business.feignclient.util.FeignClientUtil;
import com.zhidejioayu.center.business.payCard.service.PayCardService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/teacher/payCard")
public class PayCardController {

    @Resource
    private PayCardService payCardService;

    /**
     * 一键充值
     *
     * @param studentUUID 学生id
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
        if(StringUtil.isEmpty(openId)) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }
        // 如果充值日期不是31天也不是92天，初始化充值日期

        return payCardService.pay(studentUUID, type, openId);
    }

    @RequestMapping("/addMoreStudent")
    @ResponseBody
    public Object addMoreStudent(@Valid PayStudentsDTO dto) {
        List<String> studentIds = dto.getStudentIds();
        if (studentIds.isEmpty()) {
            return ServerResponse.createByError(400, "请添加学生");
        }
        return payCardService.addAllStudent(dto);
    }

    /**
     * 获取学管队长币
     *
     * @param openId    学管openId
     * @return
     */
    @GetMapping("/getCapacityCoin")
    public ServerResponse<Object> getCapacityCoin(String openId) {
        if(StringUtil.isEmpty(openId)) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }
        BaseTeacherPayCardFeignClient baseTeacherPayCardFeignClientByOpenId = FeignClientUtil.getBaseTeacherPayCardFeignClientByOpenId(openId);
        return baseTeacherPayCardFeignClientByOpenId.getCapacityCoin(openId);
    }
}
