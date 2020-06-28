package com.zhidejioayu.center.business.serverconfig.controller;

import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejioayu.center.business.serverconfig.service.ServerConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 服务器配置信息
 *
 * @author: wuchenxi
 * @date: 2020/6/28 17:02:02
 */
@RestController
@RequestMapping("/serverConfig")
public class ServerConfigController {

    @Resource
    private ServerConfigService serverConfigService;

    /**
     * 根据serverNo查询服务器信息
     *
     * @param serverNo
     * @return
     */
    @GetMapping("/getByServerNo")
    public ServerConfig getByServerNo(String serverNo) {
        return serverConfigService.getByServerNo(serverNo);
    }
}
