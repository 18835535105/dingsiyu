package com.zhidejiaoyu.student.business.game.service;

import com.zhidejiaoyu.common.vo.game.GameOneVo;
import com.zhidejiaoyu.common.vo.game.GameTwoVo;
import com.zhidejiaoyu.common.pojo.GameScore;
import com.zhidejiaoyu.common.pojo.GameStore;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.BaseService;

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
     * @param pageNum
     * @param unitId  单元id
     * @return
     */
    ServerResponse<GameOneVo> getGameOne(HttpSession session, Integer pageNum, List<String> wordList, Long unitId);

    /**
     * 获取游戏《桌牌捕音》试题
     *
     * @param session
     * @param courseId
     * @param unitId
     * @return
     */
    ServerResponse<List<GameTwoVo>> getGameTwo(HttpSession session, Long courseId, Long unitId);

    /**
     * 保存《桌牌捕音》游戏数据
     *
     * @param session
     * @param gameScore
     * @return
     */
    ServerResponse<String> saveGameTwo(HttpSession session, GameScore gameScore);

    /**
     * 获取需要进行的游戏名称，游戏流程开始之旅是调用
     *
     * @param session
     * @return
     */
    ServerResponse<String> getGameName(HttpSession session);

    /**
     * 获取学前游戏《飞船建设》题目
     *
     * @param unitId 单元id
     * @param type   1:一键排课；2：自由学习
     * @return
     */
    ServerResponse<Object> getBeforeLearnGame(Long unitId, Integer type);
}

