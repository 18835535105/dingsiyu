package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.Vo.student.SentenceTranslateVo;
import com.zhidejiaoyu.student.vo.SentenceWordInfoVo;

import javax.servlet.http.HttpSession;

/**
 * @author wuchenxi
 * @date 2018/5/21 15:13
 */
public interface SentenceService {
    /**
     * 获取例句翻译学习题目
     *
     * @param session
     * @param unitId
     * @param type    学习模式：（默认）1：普通模式；2：暴走模式
     * @return
     */
    ServerResponse<SentenceTranslateVo> getSentenceTranslate(HttpSession session, Long unitId, int classify, Integer type);

    /**
     * 保存例句翻译学习数据，包括相关记忆追踪内容
     *
     * @param session
     * @param learn
     * @param isKnown  是否答对    true:答对；false：答错
     * @param plan     进度
     * @param total    单元例句总数
     * @param classify 例句听力, 例句翻译, 例句默写
     * @return
     */
    ServerResponse<String> saveSentenceTranslate(HttpSession session, Learn learn, Boolean isKnown, Integer plan,
                                                 Integer total, String classify);

    /**
     * 例句听力和例句翻译时获取制定单词的信息
     *
     * @param session
     * @param unitId
     * @param courseId
     * @param word     要查询的英语单词
     * @return
     */
    ServerResponse<SentenceWordInfoVo> getSentenceWordInfo(HttpSession session, Long unitId, Long courseId, String word);

    /**
     * 例句听力和例句翻译时将指定单词保存到生词本中
     *
     * @param session  session信息
     * @param unitId   需要保存的单词所在的单元id
     * @param courseId 需要保存的单词所在的课程id
     * @param wordId   需要保存的单词id
     * @return ServerResponse
     */
    ServerResponse<String> saveUnknownWord(HttpSession session, Long unitId, Long courseId, Long wordId);
}
