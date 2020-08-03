package com.zhidejioayu.center.business.payCard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.dto.wechat.qy.teacher.PayStudentsDTO;
import com.zhidejiaoyu.common.mapper.PayCardMapper;
import com.zhidejiaoyu.common.pojo.PayCard;
import com.zhidejioayu.center.business.feignclient.teacher.BaseTeacherPayCardFeignClient;
import com.zhidejioayu.center.business.feignclient.util.FeignClientUtil;
import com.zhidejioayu.center.business.payCard.service.PayCardService;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class PayCardServiceImpl extends ServiceImpl<PayCardMapper, PayCard> implements PayCardService {

    @Override
    public Object pay(String studentUUID, Integer type, String openId) {
        BaseTeacherPayCardFeignClient baseTeacherInfoFeignClient = FeignClientUtil.getBaseTeacherPayCardFeignClientByOpenId(openId);
        return baseTeacherInfoFeignClient.pay(studentUUID,type,openId);
    }

    @Override
    public Object addAllStudent(PayStudentsDTO dto) {
        BaseTeacherPayCardFeignClient baseTeacherInfoFeignClient = FeignClientUtil.getBaseTeacherPayCardFeignClientByOpenId(dto.getOpenId());
        return baseTeacherInfoFeignClient.addAllStudent(dto);
    }


}
