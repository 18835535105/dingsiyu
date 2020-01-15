package com.zhidejiaoyu.student.pay.service;

import com.zhidejiaoyu.common.pojo.PayLog;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.BaseService;

/**
 * @author: wuchenxi
 * @date: 2020/1/9 11:00:00
 */
public interface PayLogService extends BaseService<PayLog> {
    /**
     * 判断学生是否已经充值
     *
     * @return true：已经充值；false：未充值
     */
    ServerResponse<Boolean> paid();
}
