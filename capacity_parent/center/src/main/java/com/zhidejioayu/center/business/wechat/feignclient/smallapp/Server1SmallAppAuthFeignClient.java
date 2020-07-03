package com.zhidejioayu.center.business.wechat.feignclient.smallapp;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

/**
 * @author: wuchenxi
 * @date: 2020/6/29 11:02:02
 */
@Service("Server1SmallAppAuthFeignClient")
@FeignClient(name = "server1", path = "/ec")
public interface Server1SmallAppAuthFeignClient extends BaseSmallAppFeignClient {

}
