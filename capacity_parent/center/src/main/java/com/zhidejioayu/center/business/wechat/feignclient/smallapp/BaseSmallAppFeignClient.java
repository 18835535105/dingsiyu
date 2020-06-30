package com.zhidejioayu.center.business.wechat.feignclient.smallapp;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.smallapp.dto.BindAccountDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: wuchenxi
 * @date: 2020/6/29 12:30:30
 */
public interface BaseSmallAppFeignClient {

    /**
     * 绑定用户信息
     *
     * @param dto
     * @return
     */
    @PostMapping("/smallApp/authorization/bind")
    ServerResponse<Object> bind(@RequestBody BindAccountDTO dto);
}
