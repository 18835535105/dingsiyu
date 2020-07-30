package com.zhidejioayu.center.business.payCard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.mapper.PayCardMapper;
import com.zhidejiaoyu.common.pojo.PayCard;
import com.zhidejioayu.center.business.feignclient.teacher.BaseTeacherPayCardFeignClient;
import com.zhidejioayu.center.business.feignclient.util.FeignClientUtil;
import com.zhidejioayu.center.business.payCard.service.PayCardService;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class PayCardServicImpl extends ServiceImpl<PayCardMapper, PayCard> implements PayCardService {

    @Override
    public Object pay(String studentUUID, Integer type, String adminUUID) {
        BaseTeacherPayCardFeignClient baseTeacherInfoFeignClient = FeignClientUtil.getTeacherPayCardFeignClientByUuid(adminUUID);
        return baseTeacherInfoFeignClient.pay(studentUUID,type,adminUUID);
    }

    @Override
    public Object addAllStudent(String[] studentIds, Integer type, String adminUUID) {
        BaseTeacherPayCardFeignClient baseTeacherInfoFeignClient = FeignClientUtil.getTeacherPayCardFeignClientByUuid(adminUUID);
        return baseTeacherInfoFeignClient.addAllStudent(Arrays.asList(studentIds),type,adminUUID);
    }


}
