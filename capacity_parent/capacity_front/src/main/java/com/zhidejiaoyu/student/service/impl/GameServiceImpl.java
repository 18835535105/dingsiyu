package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.Vo.game.GameOneVo;
import com.zhidejiaoyu.common.mapper.GameScoreMapper;
import com.zhidejiaoyu.common.mapper.GameStoreMapper;
import com.zhidejiaoyu.common.pojo.GameStore;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author wuchenxi
 * @date 2018/10/29
 */
@Service
public class GameServiceImpl extends BaseServiceImpl<GameStoreMapper, GameStore> implements GameService {

    @Autowired
    private GameStoreMapper gameStoreMapper;

    @Autowired
    private GameScoreMapper gameScoreMapper;

    @Override
    public ServerResponse<GameOneVo> getGameOne(HttpSession session) {
        Student student = getStudent(session);

        // 从当前正在学习的课程已学习的单词中随机查找15个单词
        List<Map<String, String>> wordMap = gameStoreMapper.selectGameOneSubjects(student.getId());

        List<String> subjects = new ArrayList<>(30);

        wordMap.forEach(map -> map.forEach((key, value) -> {
            subjects.add(value);
        }));

        Collections.shuffle(subjects);

        GameOneVo gameOneVo = new GameOneVo();
        gameOneVo.setMatchKeyValue(wordMap);
        gameOneVo.setSubjects(subjects);

        return ServerResponse.createBySuccess(gameOneVo);
    }
}
