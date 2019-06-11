package com.zhidejiaoyu.student.service.simple;

import com.zhidejiaoyu.common.pojo.StudentExchangePrize;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author stylefeng
 * @since 2019-02-25
 */
public interface SimpleIStudentExchangePrizeServiceSimple extends SimpleBaseService<StudentExchangePrize> {

    ServerResponse<Object> getList(int page, int number, HttpSession session, int type);

    ServerResponse<Object> getExchangePrize(int page, int number, HttpSession session);

    ServerResponse<Object> addExchangePrize(HttpSession session, Long prizeId);

    ServerResponse<Object> getAllList(HttpSession session);
}
