package com.zhidejiaoyu.student.business.index.service;

import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.BaseService;

import javax.servlet.http.HttpSession;

/**
 * @author: wuchenxi
 * @date: 2019/12/21 14:26:26
 */
public interface IndexService extends BaseService<Vocabulary> {

    /**
     * 单词首页数据
     *
     * @param session
     * @return
     */
    ServerResponse<Object> index(HttpSession session);

    /**
     * 首页点击头像
     *
     * @param session
     * @return
     */
    ServerResponse<Object> clickPortrait(HttpSession session);

    /**
     * 首页右侧小人显示需要复习的单词和句型数
     *
     * @param session
     * @return
     */
    Object getNeedReviewCount(HttpSession session);
}
