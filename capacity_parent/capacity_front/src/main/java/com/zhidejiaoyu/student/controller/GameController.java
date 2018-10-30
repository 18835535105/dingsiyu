package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.Vo.game.GameOneVo;
import com.zhidejiaoyu.common.Vo.game.GameTwoVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
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
    @GetMapping("getGameTwo")
    public ServerResponse<List<GameTwoVo>> getGameTwo(HttpSession session) {
        return gameService.getGameTwo(session);
    }
}
