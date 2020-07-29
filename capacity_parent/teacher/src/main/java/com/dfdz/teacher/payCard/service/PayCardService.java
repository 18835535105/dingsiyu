package com.dfdz.teacher.payCard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.PayCard;

import java.util.List;

public interface PayCardService extends IService<PayCard> {
    Object pay(Long id, Integer type,String adminUUID);

    Object addAllStudent(List<Integer> asList, Integer type,String adminUUID);

    Object getCardNum(String adminUUId);
}
