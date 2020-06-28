package com.zhidejiaoyu.student.business.feignclient.center;

import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author: wuchenxi
 * @date: 2020/6/28 17:10:10
 */
@FeignClient(name = "center", path = "/center/serverConfig")
public interface ServerConfigFeignClient {

    /**
     * 根据serverNo查询服务器信息
     *
     * @param serverNo
     * @return
     */
    @GetMapping("/getByServerNo/{serverNo}")
    ServerConfig getByServerNo(@PathVariable String serverNo);
}
