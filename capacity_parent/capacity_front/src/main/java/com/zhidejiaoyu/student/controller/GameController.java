package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.Vo.game.GameOneVo;
import com.zhidejiaoyu.common.Vo.game.GameTwoVo;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.pojo.GameScore;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

/**
 * 游戏相关
 *
 * @author wuchenxi
 * @date 2018/10/29
 */
@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameService gameService;

    /**
     * 获取游戏《冰火两重界》试题
     *
     * @param session
     * @return
     */
    @GetMapping("/getGameOne")
    public ServerResponse<GameOneVo> getGameOne(HttpSession session) {
        return gameService.getGameOne(session);
    }

    /**
     * 获取游戏《桌牌捕音》试题
     *
     * @param session
     * @return
     */
    @GetMapping("/getGameTwo")
    public ServerResponse<List<GameTwoVo>> getGameTwo(HttpSession session) {
        session.setAttribute(TimeConstant.GAME_BEGIN_START_TIME, new Date());
        return gameService.getGameTwo(session);
    }

    /**
     * 保存《桌牌捕音》游戏数据
     *
     * @param session
     * @param gameScore
     * @return
     */
    @PostMapping("/saveGameTwo")
    public ServerResponse<String> saveGameTwo(HttpSession session, GameScore gameScore) {
        if (gameScore.getScore() == null) {
            ServerResponse.createByErrorMessage("成绩不能为空");
        }
        if (gameScore.getAwardGold() == null) {
            ServerResponse.createByErrorMessage("奖励金币数不能为空");
        }
        return gameService.saveGameTwo(session, gameScore);
    }
}
