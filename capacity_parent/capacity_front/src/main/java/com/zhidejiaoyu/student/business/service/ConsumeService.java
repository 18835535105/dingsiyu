package com.zhidejiaoyu.student.business.service;

import com.zhidejiaoyu.common.pojo.Consume;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zdjy
 * @since 2018-11-21
 */
public interface ConsumeService extends BaseService<Consume> {


    /**
     * 减少金币或钻石
     *
     * @param type
     * @param number
     * @param session
     * @return
     */
    int reduceConsume(int type, int number, HttpSession session);
}
