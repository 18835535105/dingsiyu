package com.zhidejioayu.center.business.wechat.feignclient.smallapp;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.smallapp.dto.BindAccountDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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

    /**
     * 通过扫码获取学生总学习信息
     *
     * @param studentUuid
     * @param num         二维码序号
     * @return
     */
    @GetMapping("/smallApp/fly/v1/getStudyInfo")
    ServerResponse<Object> getTotalStudyInfo(@RequestParam String studentUuid, @RequestParam Integer num);

    /**
     * 获取学生上传你的图片信息
     *
     * @param studentUuid
     * @param num         二维码序号
     * @return
     */
    @GetMapping("/smallApp/fly/v1/getStudentInfo")
    ServerResponse<Object> getStudentInfo(@RequestParam String studentUuid, @RequestParam Integer num);
}
