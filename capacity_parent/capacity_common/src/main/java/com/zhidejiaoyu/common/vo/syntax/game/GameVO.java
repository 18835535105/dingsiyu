package com.zhidejiaoyu.common.vo.syntax.game;

import com.zhidejiaoyu.common.vo.syntax.game.GameSelect;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 语法小游戏相应数据
 *
 * @author: wuchenxi
 * @Date: 2019/10/29 17:29
 */
@Data
public class GameVO implements Serializable {

    /**
     * 题目
     */
    private String topic;

    /**
     * 读音
     */
    private String readUrl;

    /**
     * 选项
     */
    private List<GameSelect> select;

    public GameVO(String topic, List<GameSelect> list,String readUrl) {
        this.topic = topic;
        this.readUrl = readUrl;
        this.select = list;
    }
}
