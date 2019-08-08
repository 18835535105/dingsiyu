package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.dto.read.SaveStrengthenDto;
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
     * @param readWord
     * @return
     */
    ServerResponse<Object> addNewWordsBook(HttpSession session, ReadWord readWord);

    /**
     * 获取生词本列表数据
     *
     * @param session
     * @param courseId
     * @return
     */
    ServerResponse getNewWordsBook(HttpSession session, Long courseId);

    /**
     * 单词标红
     *
     * @param session
     * @param courseId
     * @param readTypeId 阅读类型 id 阅读 A/B
     * @return
     */
    ServerResponse markWordRed(HttpSession session, Long courseId, Long readTypeId);

    /**
     * 开始强化
     *
     * @param session
     * @param courseId  课程 id
     * @param type  强化类型：1.慧记忆;2.单词图鉴3.慧听写4.慧默写
     * @return
     */
    ServerResponse startStrengthen(HttpSession session, Long courseId, Integer type);

    /**
     * 保存强化信息
     *
     * @param session
     * @param dto
     * @return
     */
    ServerResponse saveStrengthen(HttpSession session, SaveStrengthenDto dto);
}
