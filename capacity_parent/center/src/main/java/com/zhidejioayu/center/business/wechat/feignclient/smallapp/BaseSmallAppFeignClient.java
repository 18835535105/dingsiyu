package com.zhidejioayu.center.business.wechat.feignclient.smallapp;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.smallapp.dto.BindAccountDTO;
import com.zhidejioayu.center.business.wechat.smallapp.dto.PrizeDTO;
import org.springframework.cloud.openfeign.SpringQueryMap;
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

    /**
     * 打卡信息
     *
     * @param openId
     * @return
     */
    @GetMapping("/smallApp/index/cardInfo")
    ServerResponse<Object> cardInfo(@RequestParam String openId);

    /**
     * 小程序首页数据
     *
     * @param openId
     * @return
     */
    @GetMapping("/smallApp/index/index")
    ServerResponse<Object> index(@RequestParam String openId);

    /**
     * 获取藏宝阁数据
     *
     * @param dto
     * @return
     */
    @GetMapping("/smallApp/index/prize")
    ServerResponse<Object> prize(@SpringQueryMap PrizeDTO dto);

    /**
     * 飞行记录
     *
     * @param openId
     * @return
     */
    @GetMapping("/smallApp/index/record")
    ServerResponse<Object> record(@RequestParam String openId);

    /**
     * 补签
     *
     * @param date
     * @param openId
     * @return
     */
    @PostMapping("/smallApp/index/replenish")
    ServerResponse<Object> replenish(@RequestParam String date, @RequestParam String openId);

    /**
     * 飞行状态
     *
     * @param openId
     * @return
     */
    @GetMapping("/smallApp/index/myState")
    ServerResponse<Object> myState(@RequestParam String openId);

    /**
     * 获取飞行测试题目
     *
     * @param openId
     * @return
     */
    @GetMapping("/smallApp/test/getTest")
    Object getTest(@RequestParam String openId);

    /**
     * 保存飞行测试记录
     *
     * @param point
     * @param openId
     * @return
     */
    @PostMapping("/smallApp/test/saveTest")
    Object saveTest(@RequestParam Integer point, @RequestParam String openId);
}

