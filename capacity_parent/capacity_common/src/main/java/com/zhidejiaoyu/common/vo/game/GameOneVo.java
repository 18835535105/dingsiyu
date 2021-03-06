package com.zhidejiaoyu.common.vo.game;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 冰火两重界游戏vo
 *
 * @author wuchenxi
 * @date 2018/10/29
 */
@Data
public class GameOneVo implements Serializable {
    /**
     * 试题集合：key 英文；value 中文翻译；
     */
    private List<Map<String, Object>> matchKeyValue;
}
