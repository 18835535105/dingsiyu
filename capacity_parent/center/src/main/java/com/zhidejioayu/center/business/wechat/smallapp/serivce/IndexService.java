package com.zhidejioayu.center.business.wechat.smallapp.serivce;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.smallapp.dto.PrizeDTO;

/**
 * 首页数据
 *
 * @author: wuchenxi
 * @date: 2020/2/14 15:01:01
 */
public interface IndexService extends IService<Student> {

    /**
     * 首页数据
     *
     * @param openId
     * @return
     */
    ServerResponse<Object> index(String openId);

    /**
     * 补签
     *
     * @param date   补签日期
     * @param openId
     * @return
     */
    ServerResponse<Object> replenish(String date, String openId);

    /**
     * 飞行记录（学习记录）
     *
     * @param openId
     * @return
     */
    ServerResponse<Object> record(String openId);

    /**
     * 藏宝阁
     *
     * @param dto
     * @return
     */
    ServerResponse<Object> prize(PrizeDTO dto);

    /**
     * 打卡日历信息
     *
     * @param openId
     * @return
     */
    ServerResponse<Object> cardInfo(String openId);

}
