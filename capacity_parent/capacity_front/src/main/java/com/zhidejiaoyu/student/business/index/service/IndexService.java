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
    ServerResponse<Object> wordIndex(HttpSession session);

    /**
     * 句型首页数据
     *
     * @param session
     * @return
     */
    ServerResponse<Object> sentenceIndex(HttpSession session);

    /**
     * 首页点击头像
     *
     * @param session
     * @param type    类型：1.单词；2.句型；3.课文；4.字母、音标
     */
    ServerResponse<Object> clickPortrait(HttpSession session, Integer type);

    /**
     * 首页右侧小人显示需要复习的单词和句型数
     *
     * @param session
     * @return
     */
    Object getNeedReviewCount(HttpSession session);
}
