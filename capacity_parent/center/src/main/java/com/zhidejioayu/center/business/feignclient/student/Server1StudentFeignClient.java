package com.zhidejioayu.center.business.feignclient.student;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

/**
 * @author: wuchenxi
 * @date: 2020/6/29 11:02:02
 */
@Service("server1StudentAuthFeignClient")
@FeignClient(name = "server1", path = "/ec")
public interface Server1StudentFeignClient extends BaseStudentFeignClient {

}
