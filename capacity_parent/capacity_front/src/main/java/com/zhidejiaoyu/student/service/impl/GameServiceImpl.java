package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.Vo.game.GameOneVo;
import com.zhidejiaoyu.common.Vo.game.GameTwoVo;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.GameScore;
import com.zhidejiaoyu.common.pojo.GameStore;
import com.zhidejiaoyu.common.pojo.RunLog;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author wuchenxi
 * @date 2018/10/29
 */
@Service
public class GameServiceImpl extends BaseServiceImpl<GameStoreMapper, GameStore> implements GameService {

    @Autowired
    private GameStoreMapper gameStoreMapper;

    @Autowired
    private CapacityReviewMapper capacityReviewMapper;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private GameScoreMapper gameScoreMapper;

    @Autowired
    private RunLogMapper runLogMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private BaiduSpeak baiduSpeak;

    @Override
    public ServerResponse<GameOneVo> getGameOne(HttpSession session) {
        Student student = getStudent(session);

        // 从当前正在学习的课程已学习的单词中随机查找15个单词
        List<Map<String, String>> wordMap = gameStoreMapper.selectGameOneSubjects(student.getId());

        List<Map<String, String>> subjects = new ArrayList<>(30);

        wordMap.forEach(map -> {
            Map<String, String> subjectMap1 = new HashMap<>(16);
            subjectMap1.put("title", map.get("wordChinese"));
            subjectMap1.put("value", map.get("word"));

            Map<String, String> subjectMap2 = new HashMap<>(16);
            subjectMap2.put("title", map.get("word"));
            subjectMap2.put("value", map.get("wordChinese"));

            subjects.add(subjectMap1);
            subjects.add(subjectMap2);
        });

        Collections.shuffle(subjects);

        GameOneVo gameOneVo = new GameOneVo();
        gameOneVo.setMatchKeyValue(subjects);

        return ServerResponse.createBySuccess(gameOneVo);
    }

    @Override
    public ServerResponse<List<GameTwoVo>> getGameTwo(HttpSession session) {
        Student student = getStudent(session);

        // 从学生当前正在学习的课程中随机获取10个需要复习的生词
        List<Map<String, Object>> needReviewWords = this.getNeedReviewWord(student);

        if (needReviewWords.size() == 10) {
            List<Long> wordIds = new ArrayList<>(10);
            needReviewWords.forEach(map -> wordIds.add(Long.valueOf(map.get("id").toString())));

            // 从学生当前正在学习的课程中随机获取110个单词
            List<String> wordList = learnMapper.selectWordInCurrentCourse(student.getId(), wordIds);

            List<GameTwoVo> gameTwoVos = new ArrayList<>();
            List<String> subjects;
            GameTwoVo gameTwoVo;
            int i = 0;
            for (Map<String, Object> needReviewWord : needReviewWords) {
                int bigBossIndex = new Random().nextInt(12);
                int minBossIndex = new Random().nextInt(12);

                gameTwoVo = new GameTwoVo();
                gameTwoVo.setBigBossIndex(bigBossIndex);
                gameTwoVo.setMinBossIndex(minBossIndex);
                gameTwoVo.setReadUrl(baiduSpeak.getLanguagePath(needReviewWord.get("word").toString()));

                // 封装纸牌的试题集合并打乱顺序；
                subjects = new ArrayList<>(12);
                subjects.add(needReviewWord.get("word").toString());
                subjects.addAll(wordList.subList(i * 11, (i + 1) * 11));
                Collections.shuffle(subjects);

                gameTwoVo.setSubjects(subjects);
                // 封装正确答案的索引
                for (int i1 = 0; i1 < subjects.size(); i1++) {
                    if (Objects.equals(subjects.get(i1), needReviewWord.get("word").toString())) {
                        gameTwoVo.setRightIndex(i1);
                    }
                }
                i++;
                gameTwoVos.add(gameTwoVo);
            }
            return ServerResponse.createBySuccess(gameTwoVos);
        }

        return ServerResponse.createBySuccess();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<String> saveGameTwo(HttpSession session, GameScore gameScore) {
        Student student = getStudent(session);
        GameStore gameStore = gameStoreMapper.selectById(gameScore.getGameId());
        saveGameScore(session, gameScore, student, gameStore);

        RunLog runLog = new RunLog(student.getId(), 4, "学生[" + student.getStudentName() + "]在游戏《"
                + gameStore.getGameName() + "》中奖励#" + gameScore.getAwardGold() + "#枚金币", new Date());
        runLogMapper.insert(runLog);

        if (gameScore.getAwardGold() > 0) {
            student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), gameScore.getAwardGold()));
            studentMapper.updateById(student);
            session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        }

        return ServerResponse.createBySuccess();
    }

    /**
     * 保存游戏记录
     *
     * @param session
     * @param gameScore
     * @param student
     * @param gameStore
     */
    private void saveGameScore(HttpSession session, GameScore gameScore, Student student, GameStore gameStore) {
        gameScore.setStudentId(student.getId());
        gameScore.setGameId(gameStore.getId());
        gameScore.setGameName(gameStore.getGameName());
        gameScore.setGameStartTime((Date) session.getAttribute(TimeConstant.GAME_BEGIN_START_TIME));
        gameScore.setGameEndTime(new Date());
        if (gameScore.getScore() < 60) {
            gameScore.setPassFlag(0);
        } else {
            gameScore.setPassFlag(1);
        }
        gameScoreMapper.insert(gameScore);
    }

    /**
     * 从学生当前正在学习的课程中随机获取10个需要复习的生词
     *
     * @param student
     * @return
     */
    private List<Map<String, Object>> getNeedReviewWord(Student student) {

        // 获取当前所学课程下单词图鉴需要复习的单词
        List<Map<String, Object>> pictureMapList = capacityReviewMapper.selectPictureNeedReviewInCurrentCourse(student.getId());
        if (pictureMapList.size() == 10) {
            return pictureMapList;
        }

        // 存储需要排除的单词id
        List<Long> wordIds = new ArrayList<>();
        if (pictureMapList.size() > 0) {
            pictureMapList.forEach(pictureMap -> wordIds.add(Long.valueOf(pictureMap.get("id").toString())));
        }

        // 获取当前所学课程下慧记忆需要复习的单词
        List<Map<String, Object>> memoryMapList = capacityReviewMapper.selectMemoryNeedReviewInCurrentCourse(student.getId(), wordIds);
        pictureMapList.addAll(memoryMapList);

        if (pictureMapList.size() == 10) {
            return pictureMapList;
        }

        if (pictureMapList.size() > 0) {
            pictureMapList.forEach(pictureMap -> wordIds.add(Long.valueOf(pictureMap.get("id").toString())));
        }

        // 获取当前所学课程下慧听力需要复习的单词
        List<Map<String, Object>> listenMapList = capacityReviewMapper.selectListenNeedReviewInCurrentCourse(student.getId(), wordIds);
        pictureMapList.addAll(listenMapList);

        if (pictureMapList.size() == 10) {
            return pictureMapList;
        }

        if (pictureMapList.size() > 0) {
            pictureMapList.forEach(pictureMap -> wordIds.add(Long.valueOf(pictureMap.get("id").toString())));
        }

        // 获取当前所学课程下慧默写需要复习的单词
        List<Map<String, Object>> writeMapList = capacityReviewMapper.selectWriteNeedReviewInCurrentCourse(student.getId(), wordIds);
        pictureMapList.addAll(writeMapList);

        if (pictureMapList.size() == 10) {
            return pictureMapList;
        }
        return new ArrayList<>();
    }
}
