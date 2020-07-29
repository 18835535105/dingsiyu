package com.dfdz.teacher.feignclient;

import com.zhidejiaoyu.common.pojo.center.BusinessUserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: wuchenxi
 * @date: 2020/6/29 12:30:30
 */
@FeignClient(name = "center", path = "/center")
public interface CenterUserFeignClient {

    @GetMapping("/userInfo/user")
    void getUser(@RequestParam BusinessUserInfo businessUserInfo,@RequestParam String no);
}
