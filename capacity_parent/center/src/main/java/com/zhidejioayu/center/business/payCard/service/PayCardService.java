package com.zhidejioayu.center.business.payCard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.PayCard;

public interface PayCardService  extends IService<PayCard> {
    Object pay(String studentUUID, Integer type, String adminUUID);

    Object addAllStudent(String[] studentIds, Integer type, String adminUUID);
}
