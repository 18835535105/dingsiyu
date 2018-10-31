package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.Vo.game.GameOneVo;
import com.zhidejiaoyu.common.Vo.game.GameTwoVo;
import com.zhidejiaoyu.common.pojo.GameScore;
import com.zhidejiaoyu.common.pojo.GameStore;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author wuchenxi
 * @date 2018/10/29
 */
public interface GameService extends BaseService<GameStore> {

    /**
     * 获取游戏《冰火两重界》试题
     *
     * @param session
     * @return
     */
    ServerResponse<GameOneVo> getGameOne(HttpSession session);

    /**
     * 获取游戏《桌牌捕音》试题
     *
     * @param session
     * @return
     */
    ServerResponse<List<GameTwoVo>> getGameTwo(HttpSession session);

    /**
     * 保存《桌牌捕音》游戏数据
     *
     * @param session
     * @param gameScore
     * @return
     */
    ServerResponse<String> saveGameTwo(HttpSession session, GameScore gameScore);
}
