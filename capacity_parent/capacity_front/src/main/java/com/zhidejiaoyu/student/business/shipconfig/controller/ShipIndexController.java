package com.zhidejiaoyu.student.business.shipconfig.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipIndexService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 飞船配置首页
 *
 * @author: wuchenxi
 * @date: 2020/2/27 15:28:28
 */
@RestController
@RequestMapping("/shipIndex")
public class ShipIndexController {

    @Resource
    private ShipIndexService shipIndexService;

    /**
     * 首页数据
     *
     * @return
     */
    @GetMapping("/index")
    public ServerResponse<Object> index() {
        return shipIndexService.index();
    }

}
