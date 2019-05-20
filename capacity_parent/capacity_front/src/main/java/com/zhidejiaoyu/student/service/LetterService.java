package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.pojo.Letter;
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
public interface LetterService extends IService<Letter> {

    Object getLetterUnit(HttpSession session);
}
