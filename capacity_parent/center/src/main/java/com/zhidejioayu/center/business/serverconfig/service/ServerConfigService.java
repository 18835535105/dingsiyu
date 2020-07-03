package com.zhidejioayu.center.business.serverconfig.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;

/**
 * @author: wuchenxi
 * @date: 2020/6/23 17:59:59
 */
public interface ServerConfigService extends IService<ServerConfig> {

    /**
     * 根据serverNo查询服务器信息
     *
     * @param serverNo
     * @return
     */
    ServerConfig getByServerNo(String serverNo);
}
