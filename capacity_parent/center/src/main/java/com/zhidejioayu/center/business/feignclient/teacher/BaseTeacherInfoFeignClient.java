package com.zhidejioayu.center.business.feignclient.teacher;

import com.zhidejiaoyu.common.pojo.SysUser;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 教师服务
 *
 * @author wuchenxi
 * @date 2020-07-27 11:50:12
 */
public interface BaseTeacherInfoFeignClient {

    @GetMapping("/getByUuid")
    ServerResponse<SysUser> getByUuid(@RequestParam String uuid);
}
