package com.zhidejiaoyu.student.service.impl;

import com.github.pagehelper.PageHelper;
import com.zhidejiaoyu.common.Vo.game.GameOneVo;
import com.zhidejiaoyu.common.Vo.game.GameTwoVo;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author wuchenxi
 * @date 2018/10/29
 */
@Slf4j
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
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private CapacityStudentUnitMapper capacityStudentUnitMapper;

    @Autowired
    private BaiduSpeak baiduSpeak;

    @Override
    public ServerResponse<GameOneVo> getGameOne(HttpSession session, Integer pageNum, List<String> wordList) {
        Student student = getStudent(session);
        session.setAttribute(TimeConstant.GAME_BEGIN_START_TIME, new Date());

        // 从当前正在学习的课程已学习的单词中随机查找10个单词
        List<Map<String, Object>> wordMap = this.getGameOneSubject(student, pageNum, wordList);

        List<Map<String, Object>> subjects = new ArrayList<>(30);

        wordMap.forEach(map -> {
            Map<String, Object> subjectMap1 = new HashMap<>(16);
            subjectMap1.put("title", map.get("wordChinese"));
            subjectMap1.put("value", map.get("word"));

            Map<String, Object> subjectMap2 = new HashMap<>(16);
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

    private List<Map<String, Object>> getGameOneSubject(Student student, Integer pageNum, List<String> wordList) {
        CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selectCurrentUnitIdByStudentIdAndType(student.getId(), 1);
        int startRow = (pageNum - 1) * 10;
        // 从当前单元单词中随机获取10题
        List<Map<String, Object>> unitLearns = learnMapper.selectLearnedByUnitId(student.getId(), capacityStudentUnit.getUnitId(), startRow, 10);
        if (unitLearns.size() < 10) {
            List<Map<String, Object>> ignoreList = new ArrayList<>(unitLearns);
            if (wordList != null && wordList.size() > 0) {
                Map<String, Object> map;
                for (String s : wordList) {
                    map = new HashMap<>(16);
                    map.put("word", s);
                    ignoreList.add(map);
                }
            }
            PageHelper.startPage(pageNum, 10 - unitLearns.size());
            packageGameSubjectMap(capacityStudentUnit.getCourseId(), unitLearns, ignoreList);
        }
        Collections.shuffle(unitLearns);

        return unitLearns;
    }

    @Override
    public ServerResponse<List<GameTwoVo>> getGameTwo(HttpSession session) {
        Student student = getStudent(session);

        // 从当前课程随机取10个已学的单词
        List<Map<String, Object>> needReviewWords = this.getGameTwoSubject(student);

        List<Long> wordIds = new ArrayList<>(10);
        needReviewWords.forEach(map -> wordIds.add(Long.valueOf(map.get("id").toString())));

        // 从学生当前正在学习的课程中随机获取110个单词
        List<String> wordList = learnMapper.selectWordInCurrentCourse(student.getId(), wordIds);
        if (wordList.size() < 110) {
            // 如果当前课程下单词总数补足110个，从其他智能版课程随机再取剩余数量的单词
            List<String> otherWordList = learnMapper.selectWordRandomInCourse(student.getId(), 110 - wordList.size(), wordIds);
            wordList.addAll(otherWordList);
        }
        if (wordList.size() < 110) {
            // 如果学生所有课程中单词总数补足110个，从《外研社版（一年级起）(一年级-上册)》取剩余单词
            List<String> otherWordList = vocabularyMapper.selectWordByCourseId(2863L, 0, 110 - wordIds.size(), wordIds);
            wordList.addAll(otherWordList);
        }
        Collections.shuffle(wordList);

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
            gameTwoVo.setChinese(needReviewWord.get("wordChinese").toString());

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

    /**
     * 获取游戏题目
     *
     * @param student
     * @return
     */
    private List<Map<String, Object>> getGameTwoSubject(Student student) {
        CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selectCurrentUnitIdByStudentIdAndType(student.getId(), 1);
        Long courseId = capacityStudentUnit.getCourseId();
        // 从当前单元单词中随机获取10题
        List<Map<String, Object>> unitLearns = learnMapper.selectLearnedByUnitId(student.getId(), capacityStudentUnit.getUnitId(), 0, 10);
        if (unitLearns.size() < 10) {
            List<Map<String, Object>> ignoreList = new ArrayList<>(unitLearns);
            PageHelper.startPage(1, 10 - unitLearns.size());
            packageGameSubjectMap(courseId, unitLearns, ignoreList);
        }
        Collections.shuffle(unitLearns);

        return unitLearns;
    }

    private void packageGameSubjectMap(Long courseId, List<Map<String, Object>> unitLearns, List<Map<String, Object>> ignoreList) {
        List<Vocabulary> vocabularies = vocabularyMapper.selectByCourseIdNotInWord(courseId, ignoreList);
        int size = unitLearns.size() + vocabularies.size();
        if (size < 10) {
            PageHelper.startPage(1, 10 - size);
            vocabularies.addAll(vocabularyMapper.selectByCourseIdNotInWord(courseId + 1, ignoreList));
        }
        vocabularies.forEach(vocabulary -> {
            Map<String, Object> map = new HashMap<>(16);
            map.put("word", vocabulary.getWord());
            map.put("wordChinese", vocabulary.getWordChinese());
            map.put("id", vocabulary.getId());
            unitLearns.add(map);
        });
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

    @Override
    public ServerResponse<String> getGameName(HttpSession session) {
        Student student = getStudent(session);
        Long gameId = gameScoreMapper.selectGameNameList(student);
        String gameName;
        if (gameId == null) {
            // 获取第一个游戏游戏名
            gameName = gameStoreMapper.selectFirstGameName();
            return ServerResponse.createBySuccess(gameName);
        } else {
            long nextGameId = gameId + 1;
            GameStore gameStore = gameStoreMapper.selectById(nextGameId);
            if (gameStore == null) {
                // 当前游戏时最后一个游戏，获取第一个游戏名
                gameName = gameStoreMapper.selectFirstGameName();
                return ServerResponse.createBySuccess(gameName);
            } else {
                // 获取下一个游戏的游戏名称
                return ServerResponse.createBySuccess(gameStore.getGameName());
            }
        }
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
}
