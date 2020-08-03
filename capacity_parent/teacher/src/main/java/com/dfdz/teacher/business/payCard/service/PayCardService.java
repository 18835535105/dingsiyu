package com.dfdz.teacher.business.payCard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.PayCard;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import java.util.List;

public interface PayCardService extends IService<PayCard> {
    Object pay(String studentUUID, Integer type,String adminUUID);

    Object addAllStudent(List<String> studentIds, Integer type,String adminUUID);

    /**
     * 获取学管队长币
     *
     * @param openId    学管openId
     * @return
     */
    ServerResponse<Object> getCapacityCoin(String openId);
}
