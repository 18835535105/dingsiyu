package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.pojo.PhoneticSymbol;
import com.baomidou.mybatisplus.service.IService;

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
}
