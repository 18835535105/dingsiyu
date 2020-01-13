package com.zhidejiaoyu.student.business.flow;

/**
 * 流程中常用的常量
 *
 * @author: wuchenxi
 * @date: 2019/12/26 18:07:07
 */
public interface FlowConstant {

    /**
     * 一键学习
     */
    /**
     * 简单模块开始节点
     */
    long EASY_START = 70;

    /**
     * 困难流程开始节点
     */
    long HARD_START = 118;

    /**
     * 一键学习课文试听
     */
    long TEKS_LISTEN = 89;

    /**
     * 一键学习语法游戏
     */
    long SYNTAX_GAME = 120;

    /**
     * 一键学习写语法节点
     */
    long SYNTAX_WRITE = 123;


    /**
     * 自由学习
     */
    /**
     * 单词播放机
     */
    long FREE_PLAYER = 11;

    /**
     * 字母填写
     */
    long FREE_LETTER_WRITE = 118;

    /**
     * 慧听写
     */
    long FREE_WORD_LISTEN = 24;

    /**
     * 句型翻译
     */
    long FREE_SENTENCE_TRANSLATE = 110;
    /**
     * 句型默写
     */
    long FREE_SENTENCE_WRITE = 113;

    /**
     * 课文试听（简单）
     */
    long FREE_TEKS_LISTEN = 115;

    /**
     * 课文训练（难）
     */
    long FREE_TEKS_TRAIN = 116;

    /**
     * 语法游戏（简单）
     */
    long FREE_SYNTAX_GAME = 125;

    /**
     * 写语法（难）
     */
    long FREE_SYNTAX_WRITE = 128;

    /**
     * 单词模块
     */
    String FLOW_ONE = "流程1";
    String FLOW_TWO = "流程2";

    /**
     * 句型模块
     */
    String FLOW_THREE = "流程3";
    String FLOW_FOUR = "流程4";

    /**
     * 课文模块
     */
    String FLOW_FIVE = "流程5";

    /**
     * 语法模块
     */
    String FLOW_SIX = "流程6";
}
