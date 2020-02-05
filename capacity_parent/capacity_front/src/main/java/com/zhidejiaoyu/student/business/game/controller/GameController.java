package com.zhidejiaoyu.student.business.game.controller;

import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.vo.game.GameOneVo;
import com.zhidejiaoyu.common.vo.game.GameTwoVo;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.pojo.GameScore;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.controller.BaseController;
import com.zhidejiaoyu.student.business.game.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

/**
 * 游戏相关
 *
 * @author wuchenxi
 * @date 2018/10/29
 */
@Slf4j
@RestController
@RequestMapping("/game")
public class GameController extends BaseController {

    @Resource
    private GameService gameService;

    /**
     * 获取需要进行的游戏名称，游戏流程开始之旅是调用
     *
     * @param session
     * @return
     */
    @GetMapping("/getGameName")
    public ServerResponse<String> getGameName(HttpSession session) {
        return gameService.getGameName(session);
    }

    /**
     * 获取游戏《冰火两重天》试题
     *
     * @param pageNum  第几次获取游戏题
     * @param session
     * @param wordList 当前游戏题目中的单词集合，避免下次取的游戏题目与上次题目有重复的
     * @return
     */
    @PostMapping("/getGameOne")
    public ServerResponse<GameOneVo> getGameOne(HttpSession session, @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                @RequestParam(required = false, name = "wordList") List<String> wordList, Long unitId) {
        return gameService.getGameOne(session, pageNum, wordList, unitId);
    }

    /**
     * 获取游戏《桌牌捕音》试题
     * 随机取当前课程已学的10个单词，已学单词不足时可从当前课程取剩余的单词
     *
     * @param session
     * @param courseId 课程id
     * @param unitId   单元id
     * @return
     */
    @GetMapping("/getGameTwo")
    public ServerResponse<List<GameTwoVo>> getGameTwo(HttpSession session, Long courseId, Long unitId) {
        session.setAttribute(TimeConstant.GAME_BEGIN_START_TIME, new Date());
        return gameService.getGameTwo(session, courseId, unitId);
    }

    /**
     * 保存游戏数据
     *
     * @param session
     * @param gameScore
     * @return
     */
    @PostMapping("/saveGameTwo")
    public ServerResponse<String> saveGameTwo(HttpSession session, GameScore gameScore) {
        if (gameScore.getScore() == null) {
            return ServerResponse.createByErrorMessage("成绩不能为空");
        }
        if (gameScore.getAwardGold() == null) {
            return ServerResponse.createByErrorMessage("奖励金币数不能为空");
        }
        return gameService.saveGameTwo(session, gameScore);
    }

    /**
     * 获取单词学前游戏测试题
     *
     * @param unitId 单元id
     * @param type 1:一键排课；2：自由学习
     * @return
     */
    @GetMapping("/getBeforeLearnGame")
    public ServerResponse<Object> getBeforeLearnGame(Long unitId, Integer type) {
        if (unitId == null) {
            throw new ServiceException("unitId can't be null!");
        }
        if (type == null) {
            throw new ServiceException("type can't be null!");
        }
        return gameService.getBeforeLearnGame(unitId, type);
    }

}
