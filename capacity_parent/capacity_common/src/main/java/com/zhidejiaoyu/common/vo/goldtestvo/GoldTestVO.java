package com.zhidejiaoyu.common.vo.goldtestvo;

import lombok.Data;

/**
 * 金币试卷试题内容
 *
 * @author: wuchenxi
 * @date: 2020/4/17 09:42:42
 */
@Data
public class GoldTestVO {

    /**
     * 题目
     */
    private String title;

    /**
     * 正文
     */
    private String content;

    /**
     * 题型
     */
    private String type;

    /**
     * 选项
     */
    private String select;

    /**
     * 答案
     */
    private String answer;

    /**
     * 解析
     */
    private String analysis;
}
