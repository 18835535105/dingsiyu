package com.zhidejiaoyu.common.Vo.simple;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 实力初显游戏
 *
 * @author wuchenxi
 * @date 2019-01-29
 */
@Data
public class StrengthGameVo implements Serializable {

    /**
     * 单词列表
     */
    private List<String> wordList;

    /**
     * 单词翻译列表
     */
    private List<String> chineseList;

    /**
     * 正确单词索引
     */
    private Integer rightIndex;

    /**
     * 类型：英译汉；汉译英
     */
    private String type;

    /**
     * 题目
     */
    private String title;
}
