package com.zhidejiaoyu.student.business.pay.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.controller.BaseController;
import com.zhidejiaoyu.student.business.pay.service.PayLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 充值记录相关
 *
 * @author: wuchenxi
 * @date: 2020/1/9 10:59:59
 */
@RestController
@RequestMapping("/payLog")
public class PayLogController extends BaseController {

    @Resource
    private PayLogService payLogService;

    /**
     * 判断学生是否已经充值
     *
     * @return true：已经充值；false：未充值
     */
    @GetMapping("/paid")
    public ServerResponse<Boolean> paid() {
        return payLogService.paid();
    }
}
