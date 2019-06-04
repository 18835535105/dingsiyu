package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.pojo.PhoneticSymbol;
import com.baomidou.mybatisplus.service.IService;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zdjy
 * @since 2019-05-17
 */
public interface PhoneticSymbolService extends IService<PhoneticSymbol> {

    /**
     * 获取学生单元
     * @param session
     * @return
     */
    Object getSymbolUnit(HttpSession session);

    Object getSymbol(Integer unitId,HttpSession session);

    /**
     * 获取音标辨音题目
     *
     * @param unitId 单元 id
     * @param session
     * @param restudy   restudy 是否重新学习
     * @return
     */
    ServerResponse<Object> getSymbolListen(Long unitId, HttpSession session, Boolean restudy);

    /**
     * 保存音节辨音记录
     *
     * @param session
     * @param unitId
     * @param symbolId  音节 id
     * @return
     */
    ServerResponse saveSymbolListen(HttpSession session, Long unitId, Integer symbolId);

    /**
     * 获取单元闯关试题
     *
     * @param session
     * @param unitId
     * @return
     */
    ServerResponse getUnitTest(HttpSession session, Long unitId);

    ServerResponse getAllSymbolListen();
}
