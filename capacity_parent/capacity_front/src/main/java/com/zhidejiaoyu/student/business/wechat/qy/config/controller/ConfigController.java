package com.zhidejiaoyu.student.business.wechat.qy.config.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.wechat.publicaccount.auth.service.PublicAccountService;
import com.zhidejiaoyu.student.business.wechat.qy.constant.QyConfigConstant;
import com.zhidejiaoyu.student.business.wechat.util.JsApiTicketUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 前端配置
 *
 * @author: wuchenxi
 * @date: 2020/6/10 11:18:18
 */
@RestController
@RequestMapping("/qy/config")
public class ConfigController {

    @Resource
    private PublicAccountService publicAccountService;

    /**
     * 获取JS-SDK配置数据
     *
     * @param url 当前页面路径
     * @return
     */
    @GetMapping("/getConfig")
    public ServerResponse<Object> getConfig(String url) {
        return publicAccountService.getConfig(url, QyConfigConstant.APP_ID, JsApiTicketUtil.getQyJsApiTicket());
    }
}
