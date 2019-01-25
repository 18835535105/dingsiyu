package com.zhidejiaoyu.common.Vo.game;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 游戏《桌牌捕音》试题vo
 *
 * @author wuchenxi
 * @date 2018/10/29
 */
@Data
public class GameTwoVo implements Serializable {

    /**
     * 单词读音地址
     */
    private String readUrl;

    /**
     * 单词翻译
     */
    private List<String> chinese;

    /**
     * 大王牌索引
     */
    private Integer bigBossIndex;

    /**
     * 小王牌索引
     */
    private Integer minBossIndex;

    /**
     * 正确答案索引
     */
    private Integer rightIndex;

    /**
     * 答案集合
     */
    private List<String> subjects;
}
