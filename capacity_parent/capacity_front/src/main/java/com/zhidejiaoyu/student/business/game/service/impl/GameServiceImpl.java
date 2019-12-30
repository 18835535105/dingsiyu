package com.zhidejiaoyu.student.business.game.service.impl;

import com.github.pagehelper.PageHelper;
import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.game.GameOneVo;
import com.zhidejiaoyu.common.vo.game.GameTwoVo;
import com.zhidejiaoyu.student.business.game.service.GameService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wuchenxi
 * @date 2018/10/29
 */
@Slf4j
@Service
public class GameServiceImpl extends BaseServiceImpl<GameStoreMapper, GameStore> implements GameService {

    @Resource
    private GameStoreMapper gameStoreMapper;

    @Resource
    private LearnMapper learnMapper;

    @Resource
    private GameScoreMapper gameScoreMapper;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private VocabularyMapper vocabularyMapper;

    @Resource
    private CourseNewMapper courseNewMapper;

    @Resource
    private UnitNewMapper unitNewMapper;

    @Resource
    private LearnExtendMapper learnExtendMapper;

    @Resource
    private BaiduSpeak baiduSpeak;

    @Override
    public ServerResponse<GameOneVo> getGameOne(HttpSession session, Integer pageNum, List<String> wordList, Long unitId) {
        session.setAttribute(TimeConstant.GAME_BEGIN_START_TIME, new Date());

        // 从当前正在学习的课程已学习的单词中随机查找10个单词
        List<Map<String, Object>> wordMap = this.getGameOneSubject(pageNum, wordList, unitId);

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

    private List<Map<String, Object>> getGameOneSubject(Integer pageNum, List<String> wordList, Long unitId) {
        int startRow = (pageNum - 1) * 10;
        // 从当前单元单词中随机获取10题
        List<Map<String, Object>> unitLearns = learnExtendMapper.selectLearnedByUnitId(unitId, startRow, 10);
        UnitNew unitNew = unitNewMapper.selectById(unitId);
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
            packageGameSubjectMap(unitNew.getCourseId(), unitLearns, ignoreList);
        }
        Collections.shuffle(unitLearns);

        return unitLearns;
    }

    @Override
    public ServerResponse<List<GameTwoVo>> getGameTwo(HttpSession session, Long courseId, Long unitId) {
        Student student = super.getStudent(session);

        List<Map<String, Object>> needReviewWords = this.getGameTwoSubject(courseId, unitId);
        List<Long> wordIds = new ArrayList<>(10);
        List<Vocabulary> vocabularies = needReviewWords.stream().map(map -> {
            wordIds.add(Long.valueOf(map.get("id").toString()));
            return Vocabulary.builder()
                    .id(Long.valueOf(map.get("id").toString()))
                    .word(map.get("word").toString())
                    .wordChinese(map.get("wordChinese").toString())
                    .build();
        }).collect(Collectors.toList());

        // 从学生当前正在学习的课程中随机获取110个单词
        List<Map<String, String>> wordMapList = learnMapper.selectWordInCurrentCourse(courseId, wordIds);
        if (wordMapList.size() < 110) {
            // 如果当前课程下单词总数补足110个，从其他智能版课程随机再取剩余数量的单词
            List<Map<String, String>> otherWordList = learnMapper.selectWordRandomInCourse(student.getId(), 110 - wordMapList.size(), wordIds);
            wordMapList.addAll(otherWordList);
        }
        if (wordMapList.size() < 110) {
            // 如果学生所有课程中单词总数补足110个，从《外研社版（一年级起）(一年级-上册)》取剩余单词
            List<Map<String, String>> otherWordList = vocabularyMapper.selectWordByCourseId(2863L, 0, 110 - wordIds.size(), wordIds);
            wordMapList.addAll(otherWordList);
        }
        List<Vocabulary> wordList = wordMapList.stream().map(map -> Vocabulary.builder()
                .word(map.get("word"))
                .wordChinese(map.get("wordChinese"))
                .build())
                .collect(Collectors.toList());

        return ServerResponse.createBySuccess(packageGameTwoVos(vocabularies, wordList, baiduSpeak));
    }

    /**
     * 封装 《桌牌捕音》响应结果
     *
     * @param vocabularies 已学习单词
     * @param wordList     其余随机单词
     * @return
     */
    public static List<GameTwoVo> packageGameTwoVos(List<Vocabulary> vocabularies, List<Vocabulary> wordList, BaiduSpeak baiduSpeak) {
        Collections.shuffle(wordList);

        List<GameTwoVo> gameTwoVos = new ArrayList<>();
        List<Vocabulary> list;
        // 中文集合
        List<String> chinese;
        // 试题英文集合
        List<String> subjects;
        GameTwoVo gameTwoVo;
        int i = 0;
        for (Vocabulary vocabulary : vocabularies) {
            int bigBossIndex = -1;
            int minBossIndex = -1;
            if (i < 2) {
                int index = new Random().nextInt(2);
                if (index % 2 == 0) {
                    bigBossIndex = new Random().nextInt(12);
                } else {
                    minBossIndex = new Random().nextInt(12);
                }
                i++;
            }

            gameTwoVo = new GameTwoVo();
            gameTwoVo.setBigBossIndex(bigBossIndex);
            gameTwoVo.setMinBossIndex(minBossIndex);
            gameTwoVo.setReadUrl(baiduSpeak.getLanguagePath(vocabulary.getWord()));

            // 封装纸牌的试题集合并打乱顺序；
            list = new ArrayList<>(12);
            list.add(vocabulary);
            list.addAll(wordList.subList(i * 11, (i + 1) * 11));
            Collections.shuffle(list);

            subjects = new ArrayList<>(list.size());
            chinese = new ArrayList<>(list.size());
            for (Vocabulary vocabulary1 : list) {
                subjects.add(vocabulary1.getWord());
                chinese.add(vocabulary1.getWordChinese());
            }

            gameTwoVo.setChinese(chinese);
            gameTwoVo.setSubjects(subjects);
            // 封装正确答案的索引
            for (int i1 = 0; i1 < subjects.size(); i1++) {
                if (Objects.equals(subjects.get(i1), vocabulary.getWord())) {
                    gameTwoVo.setRightIndex(i1);
                }
            }
            gameTwoVos.add(gameTwoVo);
        }
        return gameTwoVos;
    }

    /**
     * 获取游戏题目
     *
     * @param courseId
     * @param unitId
     * @return
     */
    private List<Map<String, Object>> getGameTwoSubject(Long courseId, Long unitId) {
        // 从当前单元单词中随机获取10题
        List<Map<String, Object>> unitLearns = learnMapper.selectLearnedByUnitId(unitId, 0, 10);
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
            // 如果当前课程没有进行游戏的单词补足 10 个，从下一课程中取数据
            PageHelper.startPage(1, 10 - size);
            vocabularies.addAll(vocabularyMapper.selectByCourseIdNotInWord(courseId + 1, ignoreList));
            size = unitLearns.size() + vocabularies.size();
            if (size < 10) {
                // 如果还补足 10 个，从同一学段中随机取余下单词
                CourseNew courseNew = courseNewMapper.selectById(courseId);
                PageHelper.startPage(1, 10 - size);
                vocabularies.addAll(vocabularyMapper.selectByPhaseNotInWord(courseNew.getStudyParagraph(), ignoreList));
            }
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
    @GoldChangeAnnotation
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<String> saveGameTwo(HttpSession session, GameScore gameScore) {
        Student student = getStudent(session);
        GameStore gameStore = gameStoreMapper.selectById(gameScore.getGameId());
        saveGameScore(session, gameScore, student, gameStore);

        if (gameScore.getAwardGold() > 0) {
            student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), gameScore.getAwardGold()));
            studentMapper.updateById(student);
            session.setAttribute(UserConstant.CURRENT_STUDENT, student);
            try {
                super.saveRunLog(student, 4, "学生[" + student.getStudentName() + "]在游戏《" + gameStore.getGameName()
                        + "》中奖励#" + gameScore.getAwardGold() + "#枚金币");
            } catch (Exception e) {
                log.error("保存学生[{} - {} - {}]游戏[{}]结果出错！需要奖励[{}]枚金币！", student.getId(), student.getAccount(),
                        student.getStudentName(), gameStore.getGameName(), gameScore.getAwardGold(), e);
            }
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
