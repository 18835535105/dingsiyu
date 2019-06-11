package com.zhidejiaoyu.student.service.simple;

import com.zhidejiaoyu.common.pojo.DrawRecord;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * 抽奖记录表 服务类
 * </p>
 *
 * @author zdjy
 * @since 2018-11-19
 */
public interface SimpleDrawRecordServiceSimple extends SimpleBaseService<DrawRecord> {


    /**
     * 添加抽奖记录
     * @param session
     * @param type
     * @param explain
     * @param imgUrl
     * @return
     */
    int[] addAward(HttpSession session, Integer type, String explain, String imgUrl);


    /**
     * 查询所有的抽奖记录
     * @param session
     * @return
     */
    ServerResponse<Object> selDrawRecordByStudentId(HttpSession session, Integer page, Integer rows);

    /**
     * 查询所有抽奖及消费信息
     * @param session
     * @return
     */
    ServerResponse<Object> selAllRecord(HttpSession session);

    ServerResponse<Object> selAwardNow(HttpSession session);
}
