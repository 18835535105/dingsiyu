package com.zhidejiaoyu.common.Vo.syntax.game;

import lombok.Data;

import java.io.Serializable;

/**
 * 游戏选项
 *
 * @author: wuchenxi
 * @Date: 2019/10/29 17:38
 */
@Data
public class GameSelect implements Serializable {

    /**
     * 选项名
     */
    private String title;

    /**
     * 是否是正确选项：true 正确；false 错误
     */
    private Boolean right;

    public GameSelect(String title, Boolean right) {
        this.title = title;
        this.right = right;
    }
}
