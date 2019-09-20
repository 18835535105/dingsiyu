package com.zhidejiaoyu.common.Vo.testbeforstudy;

import lombok.Data;

import java.util.Map;

/**
 * 问题，答案及解析
 *
 * @author: wuchenxi
 * @Date: 2019-09-16 16:15
 */
@Data
public class QuestionAndAnswer {
    /**
     * 题目
     */
    private String question;

    /**
     * 选择题答案
     */
    private Map<String, Boolean> answerMap;

    /**
     * 连词成句答案
     */
    private String answerStr;

    /**
     * 解析
     */
    private String analysis;

    /**
     * 参考知识点
     */
    private String knowledge;

    /**
     * 参考答案
     */
    private String suggestedAnswer;
}
