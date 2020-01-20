package com.zhidejiaoyu.student.pay.service.impl;

import com.zhidejiaoyu.common.mapper.PayLogMapper;
import com.zhidejiaoyu.common.pojo.PayLog;
import com.zhidejiaoyu.common.utils.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.common.redis.PayLogRedisOpt;
import com.zhidejiaoyu.student.pay.service.PayLogService;
import com.zhidejiaoyu.student.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/1/9 11:00:00
 */
@Service
public class PayLogServiceImpl extends BaseServiceImpl<PayLogMapper, PayLog> implements PayLogService {

    @Resource
    private PayLogRedisOpt payLogRedisOpt;

    @Override
    public ServerResponse<Boolean> paid() {
        Long studentId = super.getStudentId(HttpUtil.getHttpSession());
        boolean isPaid = payLogRedisOpt.isPaid(studentId);
        return ServerResponse.createBySuccess(isPaid);
    }
}
