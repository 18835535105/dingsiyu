package com.zhidejiaoyu.common.Vo.syntax.game;

import com.zhidejiaoyu.common.Vo.syntax.game.GameSelect;
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
     * 选项
     */
    private List<GameSelect> select;

    public GameVO(String topic, List<GameSelect> list) {
        this.topic = topic;
        this.select = list;
    }
}
