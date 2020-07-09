package com.zhidejioayu.center.business.feignclient.wxrobot;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

/**
 * @author: wuchenxi
 * @date: 2020/6/29 11:02:02
 */
@Service("server1WxRobotFeignClient")
@FeignClient(name = "server1", path = "/ec")
public interface Server1WxRobotFeignClient extends BaseWxRobotFeignClient {

}
