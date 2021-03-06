package com.zhidejiaoyu.student.business.service;

import com.zhidejiaoyu.common.vo.student.SentenceTranslateVo;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.SentenceWordInfoVo;

import javax.servlet.http.HttpSession;

/**
 * @author wuchenxi
 * @date 2018/5/21 15:13
 */
public interface SentenceService extends BaseService<Sentence> {
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
                                                 Integer total, String classify,Integer unitId);

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

    /**
     * 进入句型学习页获取学生所有课程及单元，并标记当前学习、未学习、已学习状态
     *
     * @param session
     * @return
     */
    ServerResponse<Object> getLearnCourseAndUnit(HttpSession session);

    /**
     * 判断是否可以学习
     * @param session
     * @param unitId
     * @return
     */
    ServerResponse<Object> getIsInto(HttpSession session, Long unitId);

    ServerResponse<Object> getSentenceLaterLearnTime(HttpSession session);

    ServerResponse<Object> getModuleRelearning(HttpSession session, String studyModel, Integer unitId);

    ServerResponse<SentenceTranslateVo> returnGoldWord(SentenceTranslate sentenceTranslate, Long plan, boolean firstStudy,
                                                       Long sentenceCount, SentenceListen sentenceListen, SentenceWrite sentenceWrite, Integer type);

    ServerResponse<Object> getLearnUnitByCourse(HttpSession session, Long courseId);
}
