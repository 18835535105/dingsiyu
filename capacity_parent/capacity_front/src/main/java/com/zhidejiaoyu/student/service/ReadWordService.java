package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.pojo.ReadWord;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;

/**
 * @author wuchenxi
 * @date 2019-07-23
 */
public interface ReadWordService extends BaseService<ReadWord> {

    /**
     * 获取选中的单词信息
     *
     *
     *
     * @param session
     * @param courseId
     * @param word
     * @return
     */
    ServerResponse<Object> getWordInfo(HttpSession session, Long courseId, String word);

    /**
     * 添加单词到生词本
     *
     *
     *
     * @param session
     * @param courseId
     * @param wordId
     * @return
     */
    ServerResponse<Object> addNewWordsBook(HttpSession session, Long courseId, Long wordId);

    /**
     * 获取生词本列表数据
     *
     * @param session
     * @param courseId
     * @return
     */
    ServerResponse getNewWordsBook(HttpSession session, Long courseId);
}
