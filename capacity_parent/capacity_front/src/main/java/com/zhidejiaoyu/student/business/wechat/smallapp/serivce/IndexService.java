package com.zhidejiaoyu.student.business.wechat.smallapp.serivce;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.BaseService;
import com.zhidejiaoyu.student.business.wechat.smallapp.dto.PrizeDTO;

/**
 * 首页数据
 *
 * @author: wuchenxi
 * @date: 2020/2/14 15:01:01
 */
public interface IndexService extends BaseService<Student> {

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

    /**
     * 飞行记录总览
     *
     * @param openId
     * @param num
     * @return
     */
    ServerResponse<Object> recordOverview(String openId, Integer num);

    /**
     * 飞行记录总览
     *
     * @param uuid 学生uuid
     * @param date 指定日期
     * @return
     */
    ServerResponse<Object> recordOverviewByUuid(String uuid, String date);
}
