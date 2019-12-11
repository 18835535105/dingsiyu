package com.zhidejiaoyu.student.timingtask.service;

import com.zhidejiaoyu.student.utils.ServiceInfoUtil;

/**
 * @author: wuchenxi
 * @Date: 2019/11/25 17:05
 */
public interface BaseQuartzService {

    /**
     * 校验当前端口是否可执行定时任务
     *
     * @param port
     * @return true：不可以执行；false：可执行
     */
    default boolean checkPort(int port) {
        int localPort = ServiceInfoUtil.getPort();
        return localPort != 0 && port != localPort;
    }
}
