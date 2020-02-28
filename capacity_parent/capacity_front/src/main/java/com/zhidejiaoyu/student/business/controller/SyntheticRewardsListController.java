package com.zhidejiaoyu.student.business.controller;


import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.SyntheticRewardsListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * 合成奖励表 前端控制器
 * </p>
 *
 * @author zdjy
 * @since 2018-11-19
 */
@Controller
@RequestMapping("/syntheticRewardsList")
public class SyntheticRewardsListController {

    @Autowired
    private SyntheticRewardsListService syntheticRewardsListService;

    /**
     * 查找手套,花朵,和手套花朵碎片
     *
     * @param session
     * @return
     */
    @PostMapping("/getGloveOrFlower")
    @ResponseBody
    public ServerResponse<Object> getGloveOrFlower(HttpSession session) {
        return syntheticRewardsListService.getGloveOrFlower(session);
    }

    /**
     * 查看所有手套及印记
     *
     * @param session
     * @return
     */
    @PostMapping("/selSyntheticList")
    @ResponseBody
    public ServerResponse<Object> selSyntheticList(HttpSession session) {
        return syntheticRewardsListService.selSyntheticList(session);
    }

    /**
     * 使用印记或手套
     *
     * @param session
     * @param nameInteger
     * @return
     */
    @PostMapping("/useSyntheticList")
    @ResponseBody
    public ServerResponse<Object> useSyntheticList(HttpSession session, Integer nameInteger) {
        return syntheticRewardsListService.updSyntheticList(session, nameInteger);
    }

    /**
     * 查看手套,印记,皮肤信息
     *
     * @param session
     * @param nameInteger
     * @param type
     * @return
     */
    @PostMapping("/getMessage")
    @ResponseBody
    public ServerResponse<Object> getMessage(HttpSession session, Integer nameInteger, Integer type) {
        return syntheticRewardsListService.getMessage(session, nameInteger, type);
    }

    /**
     * 根据学生id查询奖品
     */
    @GetMapping("/getLucky")
    @ResponseBody
    public ServerResponse<Object> getLucky(Long studentId, HttpSession session) {
        return syntheticRewardsListService.getLucky(studentId, session);
    }


}

