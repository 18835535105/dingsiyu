package com.zhidejioayu.center.business.wechat.feignclient.qy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

/**
 * @author: wuchenxi
 * @date: 2020/6/29 11:02:02
 */
@Service("server1QyAuthFeignClient")
@FeignClient(name = "server1", path = "/ec")
public interface Server1QyAuthFeignClient extends BaseQyFeignClient {

}
