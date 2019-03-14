package com.zhidejiaoyu.student.service;

import com.baomidou.mybatisplus.service.IService;
import com.zhidejiaoyu.common.pojo.SyntheticRewardsList;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * 合成奖励表 服务类
 * </p>
 *
 * @author zdjy
 * @since 2018-11-19
 */
public interface SyntheticRewardsListService extends IService<SyntheticRewardsList> {


    /**
     * 添加合成奖励
     * @param name
     * @param imgUrl
     * @param type
     * @param StudentId
     * @return
     */
    int addSynthetic(String name, String imgUrl, Integer type, Integer StudentId, Integer model);

    /**
     * 查询手套或花朵
     * @param session
     * @return
     */
    ServerResponse<Object> getGloveOrFlower(HttpSession session);


    /**
     * 查找手套或印记
     * @param session
     * @return
     */
    ServerResponse<Object> selSyntheticList(HttpSession session);


    /**
     * 使用手套或印记
     * @param session
     * @param naemInteger
     * @return
     */
    ServerResponse<Object> updSyntheticList(HttpSession session, Integer naemInteger);


    /**
     * 获取手套、印记或地图信息
     * @param session
     * @param nameInteger
     * @param type
     * @return
     */
    ServerResponse<Object> getMessage(HttpSession session, Integer nameInteger, Integer type);

    ServerResponse<Object> getLucky(Integer studentId, HttpSession session);
}
