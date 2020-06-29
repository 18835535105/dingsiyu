package com.zhidejioayu.center.business.wechat.feignclient.qy;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.qy.auth.dto.LoginDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: wuchenxi
 * @date: 2020/6/29 12:30:30
 */
public interface BaseQyFeignClient {
    /**
     * 绑定企业微信
     *
     * @param loginDTO
     * @return
     */
    @PostMapping("/qy/auth/login")
    ServerResponse<Object> login(@RequestBody LoginDTO loginDTO);
}
