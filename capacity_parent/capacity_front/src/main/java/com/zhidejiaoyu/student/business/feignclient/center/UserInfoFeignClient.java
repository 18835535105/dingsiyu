package com.zhidejiaoyu.student.business.feignclient.center;

import com.zhidejiaoyu.common.pojo.center.BusinessUserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 修改用户信息相关接口请求（教师、学生）
 *
 * @author: wuchenxi
 * @date: 2020/6/28 16:28:28
 */
@FeignClient(name = "center", path = "/center/userInfo")
public interface UserInfoFeignClient {

    /**
     * 根据用户uuid查询用户信息
     *
     * @param uuid
     * @return
     */
    @GetMapping("/getUserInfoByUserUuid")
    BusinessUserInfo getUserInfoByUserUuid(@RequestParam String uuid);

    /**
     * 修改用户信息
     *
     * @param businessUserInfo
     * @return
     */
    @PostMapping("/updateUserInfo")
    Boolean updateUserInfo(@RequestBody BusinessUserInfo businessUserInfo);

    /**
     * 保存用户信息
     *
     * @param businessUserInfo
     * @return
     */
    @PostMapping("/saveUserInfo")
    Boolean saveUserInfo(@RequestBody BusinessUserInfo businessUserInfo);

    /**
     * 判断用户信息是否存在
     *
     * @param uuid
     * @return <ul>
     * <li>true:已在服务器中存在</li>
     * <li>false：在服务器中不存在</li>
     * </ul>
     */
    @GetMapping("/isExist")
    Boolean isExist(@RequestParam String uuid);
}
