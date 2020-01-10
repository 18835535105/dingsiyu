package com.zhidejiaoyu.student.business.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.annotation.TestChangeAnnotation;
import com.zhidejiaoyu.common.constant.*;
import com.zhidejiaoyu.common.constant.study.PointConstant;
import com.zhidejiaoyu.common.constant.study.StudyModelConstant;
import com.zhidejiaoyu.common.constant.study.TestGenreConstant;
import com.zhidejiaoyu.common.dto.WordUnitTestDTO;
import com.zhidejiaoyu.common.dto.phonetic.UnitTestDto;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.study.TestPointUtil;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.CcieUtil;
import com.zhidejiaoyu.common.utils.goldUtil.TestGoldUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.math.MathUtil;
import com.zhidejiaoyu.common.utils.pet.PetSayUtil;
import com.zhidejiaoyu.common.utils.pet.PetUrlUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.server.TestResponseCode;
import com.zhidejiaoyu.common.utils.testUtil.TestResultUtil;
import com.zhidejiaoyu.common.utils.testUtil.TestSentenceUtil;
import com.zhidejiaoyu.common.vo.TestResultVo;
import com.zhidejiaoyu.common.vo.game.StrengthGameVo;
import com.zhidejiaoyu.common.vo.student.SentenceTranslateVo;
import com.zhidejiaoyu.common.vo.testVo.TestDetailVo;
import com.zhidejiaoyu.common.vo.testVo.TestRecordVo;
import com.zhidejiaoyu.common.vo.testVo.TestResultVO;
import com.zhidejiaoyu.student.business.learn.common.SaveData;
import com.zhidejiaoyu.student.business.learn.common.SaveTeksData;
import com.zhidejiaoyu.student.business.service.TestService;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.util.*;

@Slf4j
@Service
public class TestServiceImpl extends BaseServiceImpl<TestRecordMapper, TestRecord> implements TestService {

    @Autowired
    private SentenceMapper sentenceMapper;

    @Resource
    private SaveData saveData;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TestResultUtil testResultUtil;

    @Autowired
    private TestRecordMapper testRecordMapper;

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private CommonMethod commonMethod;

    @Autowired
    private PetSayUtil petSayUtil;

    @Autowired
    private CcieUtil ccieUtil;

    @Resource
    private SaveTeksData saveTeksData;

    @Autowired
    private TestRecordInfoMapper testRecordInfoMapper;

    @Autowired
    private StudentFlowMapper studentFlowMapper;

    @Autowired
    private RedisOpt redisOpt;

    @Autowired
    private TestSentenceUtil testSentenceUtil;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private StudyFlowMapper studyFlowMapper;

    @Autowired
    private TestGoldUtil testGoldUtil;

    @Autowired
    private SentenceUnitMapper sentenceUnitMapper;

    @Autowired
    private LetterMapper letterMapper;

    @Autowired
    private BaiduSpeak baiduSpeak;

    @Resource
    private UnitNewMapper unitNewMapper;
    @Resource
    private LearnNewMapper learnNewMapper;


    /**
     * 游戏测试题目获取，获取20个单词供测试
     *
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Map<String, Object>> getGameSubject(HttpSession session, Long unitId) {
        // 获取当前学生信息
        Student student = getStudent(session);
        // 查询当前学生游戏测试的次数，如果已经测试两次不再允许游戏测试
        TestRecordExample example = new TestRecordExample();
        example.createCriteria().andStudentIdEqualTo(student.getId()).andGenreEqualTo("学前游戏测试");
        List<TestRecord> records = testRecordMapper.selectByExample(example);
        Integer point = null;
        if (records.size() > 0) {
            TestRecord testRecord = records.get(0);
            String explain = testRecord.getExplain();
            point = testRecord.getPoint();
            // 测试次数
            String time = explain.split("#")[1];
            if ("2".equals(time)) {
                return ServerResponse.createByErrorCodeMessage(TestResponseCode.GAME_TESTED_SECOND.getCode(),
                        TestResponseCode.GAME_TESTED_SECOND.getMsg());
            }
        }

        // 设置游戏测试开始时间
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        List<Vocabulary> vocabularies;
        Long courseId;
        PageHelper.startPage(1, 100);
        if (unitId != null) {
            UnitNew unitNew = unitNewMapper.selectById(unitId);
            courseId = unitNew.getCourseId();
            vocabularies = vocabularyMapper.selectByCourseId(courseId);
        } else {
            courseId = 2751L;
            vocabularies = vocabularyMapper.selectByCourseId(2751L);
        }
        Collections.shuffle(vocabularies);

        String[] type = {"汉译英"};
        // 游戏测试从取当前学段的预科库中的单词25个,其中有5个未预备单词，即可以直接跳过
        List<TestResultVO> testResults = testResultUtil.getWordTestesForCourse(type, 26, vocabularies, courseId);
        Map<String, Object> map = new HashMap<>(16);
        map.put("testResults", testResults);
        if (point != null) {
            map.put("point", point);
        }
        // 宠物url,用于跳过游戏时显示
        map.put("petUrl", AliyunInfoConst.host + student.getPartUrl());

        return ServerResponse.createBySuccess(map);
    }

    @Override
    @GoldChangeAnnotation
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Map<String, Object>> saveGameTestRecord(HttpSession session, TestRecord testRecord) {
        Student student = getStudent(session);

        // 存放响应的 提示语，推送课程名称，奖励金币数
        Map<String, Object> map = new HashMap<>(16);

        // 游戏测试开始时间
        Date date = new Date();
        saveTestRecordTime(testRecord, session, date);
        session.removeAttribute(TimeConstant.BEGIN_START_TIME);

        testRecord.setStudentId(student.getId());
        testRecord.setGenre("学前游戏测试");
        testRecord.setStudyModel("学前游戏测试");
        testRecord.setQuantity(20);

        // 查看当前学生是否已经有游戏测试记录
        TestRecordExample example = new TestRecordExample();
        example.createCriteria().andStudentIdEqualTo(student.getId()).andGenreEqualTo("学前游戏测试");
        List<TestRecord> records = testRecordMapper.selectByExample(example);

        // 已经有游戏测试记录进行记录的更新
        if (records.size() > 0) {
            updateGameTestRecord(testRecord, student, map, records);
        } else {
            // 无游戏测试记录，新增记录
            createGameTestRecord(testRecord, student, map);
            // 根据游戏分数初始化不同流程
            StudentFlow studentFlow = studentFlowMapper.selectByStudentId(student.getId(), 0, 1);
            this.initStudentFlow(student, testRecord.getPoint(), studentFlow);
        }
        getLevel(session);
        // 流程名称
        StudyFlow studyFlow = studyFlowMapper.selectById(3);
        if (studyFlow != null) {
            int grade = studyFlow.getType();
            if (testRecord.getPoint() >= grade) {
                map.put("flow", "流程二");
            } else {
                map.put("flow", "流程一");
            }
        } else {
            map.put("flow", "流程一");
        }

        session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        return ServerResponse.createBySuccess(map);
    }

    @Override
    public ServerResponse<Map<String, Object>> getStrengthGame(HttpSession session, Long unitId) {

        Student student = super.getStudent(session);

        // 判断学生是否已经进行过游戏测试，如果进行过不允许再次进行；否则返回游戏题目
        int count = testRecordMapper.countGameCount(student);
        if (count > 0) {
            return ServerResponse.createBySuccess(ResponseCode.FORBIDDEN.getCode(), ResponseCode.FORBIDDEN.getMsg());
        }
        UnitNew unitNew = unitNewMapper.selectById(unitId);
        if (unitNew == null) {
            log.warn("学生[{} - {} - {}]没有分配智慧单词学习计划！", student.getId(), student.getAccount(), student.getStudentName());
            return ServerResponse.createByError(301, "学生未分配智能版单词学习计划！");
        }

        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        // 随机选出20个正确单词信息
        PageHelper.startPage(1, 20);
        List<Vocabulary> rightVocabularies = vocabularyMapper.selectByUnitId(unitId);
        Map<String, String> map = this.getWordMap(rightVocabularies);

        int size = rightVocabularies.size();
        if (rightVocabularies.size() > 20) {
            rightVocabularies = rightVocabularies.subList(0, 20);
        }
        int errorSize = size * 3;
        long currentCourseId = unitNew.getCourseId();
        PageHelper.startPage(1, errorSize);
        List<Vocabulary> errorVocabularies = vocabularyMapper.selectByCourseIdWithoutWordIds(currentCourseId, rightVocabularies);
        map.putAll(this.getWordMap(errorVocabularies));

        List<Vocabulary> ignore = new ArrayList<>(errorVocabularies);
        ignore.addAll(rightVocabularies);

        // 如果错误单词取值不够用，从当前课程的上一个课程和下一个课程取值
        if (errorVocabularies.size() < errorSize) {
            PageHelper.startPage(1, errorSize - errorVocabularies.size());
            List<Vocabulary> otherErrorVocabularies = vocabularyMapper.selectByCourseIdWithoutWordIds(currentCourseId + 1, ignore);
            if (otherErrorVocabularies.size() > 0) {
                errorVocabularies.addAll(otherErrorVocabularies);
                ignore.addAll(otherErrorVocabularies);
                map.putAll(this.getWordMap(otherErrorVocabularies));
            }
            if (otherErrorVocabularies.size() < errorSize) {
                PageHelper.startPage(1, errorSize - errorVocabularies.size());
                otherErrorVocabularies = vocabularyMapper.selectByCourseIdWithoutWordIds(currentCourseId - 1, ignore);
                if (otherErrorVocabularies.size() > 0) {
                    errorVocabularies.addAll(otherErrorVocabularies);
                    map.putAll(this.getWordMap(otherErrorVocabularies));
                }
            }
        }

        // 封装各组信息
        Map<String, Object> resultMap = packageStrengthVo(student, rightVocabularies, map, size, errorSize, errorVocabularies);
        if (resultMap != null) {
            resultMap.put("courseId", currentCourseId);
            resultMap.put("unitId", unitId);
        }
        return ServerResponse.createBySuccess(resultMap);
    }

    @Override
    public Object getLetterUnitEntry(HttpSession session, Long unitId) {
        // 设置游戏测试开始时间
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        List<Letter> letters = letterMapper.getByUnitId(unitId);
        Collections.shuffle(letters);
        List<Map<String, Object>> returnList = new ArrayList<>();
        int i = 0;
        for (Letter letter : letters) {
            Map<String, Object> map = new HashMap<>();
            if (i < 2) {
                //字母匹配
                getLetterPair(map, letter);
            } else if (i < 4) {
                //字母辨音
                getLetterDiscrimination(map, letter);
            } else {
                //字母听写
                getLetterWrite(map, letter);
            }
            returnList.add(map);
            i++;
        }
        return ServerResponse.createBySuccess(returnList);
    }

    @Override
    @GoldChangeAnnotation
    @TestChangeAnnotation
    public Object saveLetterUnitEntry(HttpSession session, TestRecord testRecord) {
        Student student = getStudent(session);
        Integer point = testRecord.getPoint();
        testRecord.setStudentId(student.getId());
        testRecord.setGenre("单元闯关测试");
        testRecord.setStudyModel("字母单元闯关");
        testRecord.setQuantity(testRecord.getErrorCount() + testRecord.getRightCount());
        Date date = new Date();
        saveTestRecordTime(testRecord, session, date);
        testRecord.setExplain(getUnitTestMsg(testRecord.getPoint()));
        Integer integer = testRecordMapper.selectUnitTestMaxPointByStudyModel(student.getId(), testRecord.getUnitId(), 10);
        if (integer == null || integer <= 0) {
            integer = 0;
        }
        //获取历史最高分
        int goldCount = 0;
        if (point > integer) {
            goldCount = this.getGold(point);
            this.saveLog(student, goldCount, null, "字母单元闯关测试");
            if (student.getBonusExpires() != null) {
                if (student.getBonusExpires().getTime() > System.currentTimeMillis()) {
                    Double doubleGoldCount = goldCount * 0.2;
                    student.setSystemGold(student.getSystemGold() + doubleGoldCount);
                    testRecord.setAwardGold(goldCount + doubleGoldCount.intValue());
                }
            }
        }

        testRecord.setAwardGold(goldCount);

        TestResultVo vo = new TestResultVo();
        vo.setGold(goldCount);
        vo.setPetUrl(GetOssFile.getPublicObjectUrl(student.getPartUrl()));
        int number = testRecordMapper.selCount(student.getId(), testRecord.getCourseId(), testRecord.getUnitId(),
                testRecord.getStudyModel(), testRecord.getGenre());
        int energy = getEnergy(student, testRecord.getPoint(), number);
        vo.setEnergy(energy);
        testRecordMapper.insert(testRecord);
        getMessage(student, vo, testRecord, point, 100);
        studentMapper.updateById(student);
        return ServerResponse.createBySuccess(vo);
    }

    private int getGold(Integer point) {
        int goldCount;
        if (point < PointConstant.SIXTY) {
            goldCount = 0;
        } else if (point < PointConstant.SEVENTY) {
            goldCount = TestAwardGoldConstant.UNIT_TEST_SIXTY_TO_SEVENTY;
        } else if (point < PointConstant.EIGHTY) {
            goldCount = TestAwardGoldConstant.UNIT_TEST_SEVENTY_TO_EIGHTY;
        } else if (point < PointConstant.NINETY) {
            goldCount = TestAwardGoldConstant.UNIT_TEST_EIGHTY_TO_NINETY;
        } else if (point < PointConstant.HUNDRED) {
            goldCount = TestAwardGoldConstant.UNIT_TEST_NINETY_TO_FULL;
        } else {
            goldCount = TestAwardGoldConstant.UNIT_TEST_FULL;
        }
        return goldCount;
    }

    @Override
    public Object getLetterAfterLearning(HttpSession session, Long unitId) {
        // 设置游戏测试开始时间
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        //获取单元字母
        List<Letter> letters = letterMapper.getAllLetter();
        //打乱顺序
        Collections.shuffle(letters);
        //获取字母配对试题
        List<Letter> letterPairList = letters.subList(0, 10);
        List<Map<String, Object>> pairList = new ArrayList<>();
        for (Letter letter : letterPairList) {
            Map<String, Object> letterPairMap = new HashMap<>();
            getLetterPair(letterPairMap, letter);
            pairList.add(letterPairMap);
        }
        List<Map<String, Object>> returnList = new ArrayList<>(pairList);
        //获取字母辨音试题
        List<Letter> letterDiscriminationList = letters.subList(10, 20);
        List<Map<String, Object>> discriminationList = new ArrayList<>();
        for (Letter letter : letterDiscriminationList) {
            Map<String, Object> discriminationMap = new HashMap<>();
            getLetterDiscrimination(discriminationMap, letter);
            discriminationList.add(discriminationMap);
        }
        returnList.addAll(discriminationList);
        //获取字母听写试题
        List<Letter> letterWriteList = letters.subList(20, letters.size() - 1);
        List<Map<String, Object>> writeList = new ArrayList<>();
        for (Letter letter : letterWriteList) {
            Map<String, Object> letterWriteMap = new HashMap<>();
            getLetterWrite(letterWriteMap, letter);
            writeList.add(letterWriteMap);
        }
        returnList.addAll(writeList);
        return ServerResponse.createBySuccess(returnList);
    }

    @Override
    @GoldChangeAnnotation
    @TestChangeAnnotation(isUnitTest = false)
    public Object saveLetterAfterLearning(HttpSession session, TestRecord testRecord) {
        Student student = getStudent(session);
        Integer point = testRecord.getPoint();
        testRecord.setStudentId(student.getId());
        testRecord.setGenre("学后测试");
        testRecord.setStudyModel("字母学后测试");
        testRecord.setQuantity(testRecord.getErrorCount() + testRecord.getRightCount());
        Date date = new Date();
        saveTestRecordTime(testRecord, session, date);
        testRecord.setExplain(getUnitTestMsg(testRecord.getPoint()));
        Integer integer = testRecordMapper.selectUnitTestMaxPointByStudyModel(student.getId(), testRecord.getUnitId(), 12);
        int goldCount = 0;
        if (integer == null || integer <= 0) {
            integer = 0;
        }
        if (integer < point) {
            goldCount = this.getGold(point);
        }
        this.saveLog(student, goldCount, null, "字母学后测试");
        if (student.getBonusExpires() != null) {
            if (student.getBonusExpires().getTime() > System.currentTimeMillis()) {
                Double doubleGoldCount = goldCount * 0.2;
                student.setSystemGold(student.getSystemGold() + doubleGoldCount);
                testRecord.setAwardGold(goldCount + doubleGoldCount.intValue());
            }
        }
        TestResultVo vo = new TestResultVo();
        vo.setGold(goldCount);
        //获取测试有效次数
        int number = testRecordMapper.selCount(student.getId(), testRecord.getCourseId(), testRecord.getUnitId(),
                testRecord.getStudyModel(), testRecord.getGenre());
        int energy = getEnergy(student, testRecord.getPoint(), number);
        vo.setEnergy(energy);
        vo.setPetUrl(AliyunInfoConst.host + student.getPartUrl());
        getMessage(student, vo, testRecord, point, 100);

        testRecord.setAwardGold(goldCount);
        testRecordMapper.insert(testRecord);
        studentMapper.updateById(student);
        return ServerResponse.createBySuccess(vo);
    }

    @Override
    @GoldChangeAnnotation
    @TestChangeAnnotation(isUnitTest = false)
    public Object saveReadTest(HttpSession session, TestRecord testRecord) {
        Student student = super.getStudent(session);
        Integer point = testRecord.getPoint();
        testRecord.setStudentId(student.getId());
        testRecord.setGenre("单元闯关测试");
        testRecord.setStudyModel("阅读测试");
        Date date = new Date();
        saveTestRecordTime(testRecord, session, date);
        testRecord.setExplain(getUnitTestMsg(testRecord.getPoint()));
        Integer integer = testRecordMapper.selectUnitTestMaxPointByStudyModel(student.getId(), testRecord.getUnitId(), 13);
        int gold = 0;
        int energy = 0;
        if (integer != null && integer < 60) {
            if (point >= 60) {
                gold = 5;
                energy = 2;
                this.saveLog(student, gold, null, "阅读测试");
                studentMapper.updateById(student);
            }
        }
        TestResultVo vo = new TestResultVo();
        vo.setGold(gold);
        vo.setEnergy(energy);
        vo.setPetUrl(AliyunInfoConst.host + student.getPartUrl());
        vo.setPoint(point);
        testRecordMapper.insert(testRecord);
        if (point < PointConstant.EIGHTY) {
            vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_LESS_EIGHTY));
        } else if (point < PointConstant.NINETY) {
            vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_EIGHTY_TO_HUNDRED));
        } else {
            vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_HUNDRED));
        }
        return ServerResponse.createBySuccess(vo);
    }


    private void getLetterWrite(Map<String, Object> map, Letter studyLetter) {
        map.put("type", 3);
        map.put("mp3url", baiduSpeak.getLetterPath(studyLetter.getBigLetter()));
        map.put("bigLetter", studyLetter.getBigLetter());
        map.put("lowercaseLetter", studyLetter.getLowercaseLetters());
    }

    private void getLetterDiscrimination(Map<String, Object> map, Letter studyLetter) {
        map.put("type", 2);
        List<Letter> threeLetter = letterMapper.getThreeLetter(studyLetter.getId());
        //获取题目
        Random random = new Random();
        int ranId = random.nextInt(10);
        map.put("mp3url", baiduSpeak.getLetterPath(studyLetter.getBigLetter()));
        List<Map<String, Object>> options = new ArrayList<>();
        Map<String, Object> anwars = new HashMap<>();
        if (ranId > 5) {
            //小写字母题目
            anwars.put("letter", studyLetter.getLowercaseLetters());
            anwars.put("isTrue", true);
            options.add(anwars);
            for (Letter letter : threeLetter) {
                Map<String, Object> option = new HashMap<>();
                option.put("letter", letter.getLowercaseLetters());
                option.put("isTrue", false);
                options.add(option);
            }
        } else {
            anwars.put("letter", studyLetter.getBigLetter());
            anwars.put("isTrue", true);
            options.add(anwars);
            //大写字母题目
            for (Letter letter : threeLetter) {
                Map<String, Object> option = new HashMap<>();
                option.put("letter", letter.getBigLetter());
                option.put("isTrue", false);
                options.add(option);
            }
        }
        Collections.shuffle(options);
        map.put("options", options);
    }

    private void getLetterPair(Map<String, Object> map, Letter studyLetter) {
        map.put("type", 1);
        List<Letter> threeLetter = letterMapper.getThreeLetter(studyLetter.getId());
        //获取题目
        Random random = new Random();
        int ranId = random.nextInt(10);
        List<Map<String, Object>> options = new ArrayList<>();
        Map<String, Object> anwars = new HashMap<>();
        if (ranId > 5) {
            //大写字母题目
            map.put("title", studyLetter.getBigLetter());
            anwars.put("letter", studyLetter.getLowercaseLetters());
            anwars.put("isTrue", true);
            options.add(anwars);
            for (Letter letter : threeLetter) {
                Map<String, Object> users = new HashMap<>();
                users.put("letter", letter.getLowercaseLetters());
                users.put("isTrue", false);
                options.add(users);
            }
        } else {
            //小写字母题目
            map.put("title", studyLetter.getLowercaseLetters());
            anwars.put("letter", studyLetter.getBigLetter());
            anwars.put("isTrue", true);
            options.add(anwars);
            for (Letter letter : threeLetter) {
                Map<String, Object> users = new HashMap<>();
                users.put("letter", letter.getBigLetter());
                users.put("isTrue", false);
                options.add(users);
            }
        }
        Collections.shuffle(options);
        map.put("options", options);
    }

    /**
     * 将单词集合封装成 map key 为单词，value 为单词翻译
     *
     * @param rightVocabularies
     * @return
     */
    private Map<String, String> getWordMap(List<Vocabulary> rightVocabularies) {
        Map<String, String> map = new HashMap<>(16);
        if (rightVocabularies.size() > 0) {
            rightVocabularies.forEach(vocabulary -> map.put(vocabulary.getWord(), vocabulary.getWordChinese()));
        }
        return map;
    }

    private Map<String, Object> packageStrengthVo(Student student, List<Vocabulary> rightVocabularies,
                                                  Map<String, String> map, int size, int errorSize,
                                                  List<Vocabulary> errorVocabularies) {
        List<StrengthGameVo> strengthGameVos = new ArrayList<>();
        if (size > 0) {
            List<String> wordList;
            List<String> chineseList;
            StrengthGameVo strengthGameVo;
            int k = 0;
            for (Vocabulary rightVocabulary : rightVocabularies) {
                wordList = new ArrayList<>(8);
                chineseList = new ArrayList<>(8);
                strengthGameVo = new StrengthGameVo();

                // 小人图片顺序
                int pictureIndex = 0;
                wordList.add(String.valueOf(pictureIndex++));
                wordList.add(rightVocabulary.getWord());
                for (int j = 0; j < 3; j++) {
                    wordList.add(String.valueOf(pictureIndex++));
                    if (k < errorSize) {
                        wordList.add(errorVocabularies.get(k).getWord());
                    }
                    k++;
                }

                Collections.shuffle(wordList);

                int num = new Random().nextInt(2);
                for (int n = 0; n < 8; n++) {
                    if (Objects.equals(wordList.get(n), rightVocabulary.getWord())) {
                        if (num % 2 == 0) {
                            strengthGameVo.setType("英译汉");
                            strengthGameVo.setTitle(rightVocabulary.getWord());
                        } else {
                            strengthGameVo.setType("汉译英");
                            strengthGameVo.setTitle(rightVocabulary.getWordChinese());
                        }
                        strengthGameVo.setRightIndex(n);
                        break;
                    }
                }
                pictureIndex = 0;
                for (String word : wordList) {
                    if (map.get(word) == null) {
                        chineseList.add(String.valueOf(pictureIndex++));
                    } else {
                        chineseList.add(map.get(word));
                    }

                }

                strengthGameVo.setChineseList(chineseList);
                strengthGameVo.setWordList(wordList);

                strengthGameVos.add(strengthGameVo);
            }

            Map<String, Object> resultMap = new HashMap<>(16);
            resultMap.put("result", strengthGameVos);
            resultMap.put("sex", student.getSex());
            return resultMap;
        }
        return null;
    }

    /**
     * 游戏结束初始化学习流程
     *
     * @param student
     * @param point
     * @param studentFlow
     */
    private void initStudentFlow(Student student, Integer point, StudentFlow studentFlow) {
        if (studentFlow == null) {
            studentFlow = new StudentFlow();
            studentFlow.setStudentId(student.getId());
            studentFlow.setTimeMachine(0);
            studentFlow.setPresentFlow(1);
            if (point >= PointConstant.EIGHTY) {
                // 流程2
                studentFlow.setCurrentFlowId(24L);
            } else {
                // 流程1
                studentFlow.setCurrentFlowId(11L);
            }
            studentFlowMapper.insert(studentFlow);
        } else {
            if (point >= PointConstant.EIGHTY) {
                // 流程2
                studentFlow.setCurrentFlowId(24L);
            } else {
                // 流程1
                studentFlow.setCurrentFlowId(11L);
            }
            studentFlowMapper.updateById(studentFlow);
        }
    }

    /**
     * 无游戏测试记录，新增游戏测试记录
     *
     * @param testRecord
     * @param student
     * @param map
     */
    private void createGameTestRecord(TestRecord testRecord, Student student, Map<String, Object> map) {
        testRecord.setExplain("游戏测试次数#1#");
        String msg;
        if (testRecord.getPoint() >= PointConstant.EIGHTY) {
            msg = "恭喜你旗开得胜，祝未来勇闯天涯！";
            map.put("tip", msg);
            testRecord.setExplain(testRecord.getExplain() + msg);
        } else {
            msg = "失败乃成功他母亲，速速利用第二次机会迎回春天！";
            map.put("tip", msg);
            testRecord.setExplain(testRecord.getExplain() + msg);
            map.put("tip", "游戏还不错吧？下面我们来开始学习吧。");
        }

        // 保存历史最高分和历史最低分
        testRecord.setHistoryBestPoint(testRecord.getPoint());
        testRecord.setHistoryBadPoint(testRecord.getPoint());

        // 奖励
        int goldCount = this.award(student, testRecord);
        map.put("gold", goldCount);
        testRecord.setAwardGold(goldCount);
        int count = testRecordMapper.insertSelective(testRecord);
        if (count == 0) {
            String errMsg = "id为 " + student.getId() + " 的学生 " + student.getStudentName() + " 游戏测试记录保存失败！";
            super.saveRunLog(student, 2, errMsg);
            log.error(errMsg);
        }
    }

    /**
     * 已有游戏测试记录，更新游戏测试记录
     *
     * @param testRecord
     * @param student
     * @param map
     * @param records
     */
    private void updateGameTestRecord(TestRecord testRecord, Student student, Map<String, Object> map, List<TestRecord> records) {
        TestRecord record = records.get(0);
        testRecord.setId(record.getId());

        testRecord.setExplain("游戏测试次数#2#");
        String msg;
        if (testRecord.getPoint() >= PointConstant.EIGHTY) {
            msg = "恭喜你旗开得胜，祝未来勇闯天涯！";
            map.put("tip", msg);
            testRecord.setExplain(testRecord.getExplain() + msg);
        } else {
            msg = "虽败犹荣，我们从头来过。";
            map.put("tip", msg);
            testRecord.setExplain(testRecord.getExplain() + msg);

            map.put("tip", "游戏还不错吧？下面我们来开始学习吧。");

        }

        // 第二次测试只有成绩比第一次高才可以获得金币
        int goldCount = 0;
        if (testRecord.getPoint() > record.getPoint()) {
            goldCount = this.award(student, testRecord);
            testRecord.setAwardGold(goldCount);
        }
        map.put("gold", goldCount);

        // 比较出最高分
        if (testRecord.getPoint() < record.getHistoryBadPoint()) {
            testRecord.setHistoryBadPoint(record.getPoint());
        } else if (testRecord.getPoint() > record.getHistoryBestPoint()) {
            testRecord.setHistoryBestPoint(testRecord.getPoint());
        }

        // 更新游戏测试记录
        int count = testRecordMapper.updateByPrimaryKeySelective(testRecord);
        if (count == 0) {
            String errMsg = "id为 " + student.getId() + " 的学生 " + student.getStudentName() + " 更新游戏测试记录失败！";
            super.saveRunLog(student, 2, errMsg);
            log.error(errMsg);
        }
    }

    /**
     * 根据成绩计算金币奖励
     *
     * @param student    当前学生
     * @param testRecord 测试成绩
     * @return 学生奖励金币数
     */
    private int award(Student student, TestRecord testRecord) {
        int point = testRecord.getPoint();
        int goldCount = 0;
        if (point >= PointConstant.EIGHTY && point < PointConstant.NINETY) {
            goldCount = 3;
            this.saveLog(student, goldCount, null, "游戏测试");
        } else if (point >= PointConstant.NINETY && point < PointConstant.HUNDRED) {
            goldCount = 5;
            this.saveLog(student, goldCount, null, "游戏测试");
        } else if (point == PointConstant.HUNDRED) {
            goldCount = 10;
            this.saveLog(student, goldCount, null, "游戏测试");
        }
        studentMapper.updateById(student);
        return goldCount;
    }

    @Override
    public ServerResponse<Map<String, Object>> getLevelTest(HttpSession session) {
        Student student = getStudent(session);

        // 判断学生是否已经进行过摸底测试
        Integer levelTestCount = testRecordMapper.countLevelTestCountByStudentId(student);
        if (levelTestCount > 0) {
            // 已经进行过等级测试，不允许再次测试
            return ServerResponse.createBySuccess(TestResponseCode.LEVEL_TESTED.getCode(), TestResponseCode.LEVEL_TESTED.getMsg());
        }

        // 更新学生的游戏测试记录为2
        testRecordMapper.updateGameRecord(student);

        // 设置等级测试开始时间
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        List<Vocabulary> vocabularies;
        // 根据当前学生所学学段去对应的单词预科库中查找对应的单词和释义
        String grade = student.getGrade();
        String phase = CommonMethod.getPhase(grade);
        String courseName;

        if ("初中".equals(phase)) {
            // 前往初中预科库查询简单单词
            vocabularies = vocabularyMapper.selectByStudentPhase(student, 1);
            courseName = student.getVersion() + " (七年级)";
        } else {
            // 前往高中预科库查询简单单词
            vocabularies = vocabularyMapper.selectByStudentPhase(student, 2);
            if (vocabularies.size() > 0) {
                courseName = student.getVersion() + " (高一)";
            } else {
                vocabularies = vocabularyMapper.selectByStudentPhase(student, 3);
                courseName = student.getVersion() + " (必修一)";
            }
        }

        Collections.shuffle(vocabularies);
        // 取前10个单词作为英译汉题型
        List<Vocabulary> englishToChinese;
        // 取中间10个单词作为汉译英题型
        List<Vocabulary> chineseToEnglish;
        // 取最后10个单词作为听力理解题型
        List<Vocabulary> listen;
        List<TestResultVO> results = new ArrayList<>();
        List<TestResultVO> results1 = new ArrayList<>();
        List<TestResultVO> results2 = new ArrayList<>();

        String[] type = {"英译汉"};
        String[] type1 = {"汉译英"};
        String[] type2 = {"听力理解"};

        try {
            englishToChinese = vocabularies.subList(0, 10);
            results = testResultUtil.getWordTestes(type, englishToChinese.size(), englishToChinese, student.getVersion(), phase);
        } catch (Exception e) {
            log.error("摸底测试单词不足10个！", e);
        }
        try {
            chineseToEnglish = vocabularies.subList(10, 20);
            results1 = testResultUtil.getWordTestes(type1, chineseToEnglish.size(), chineseToEnglish, student.getVersion(), phase);
        } catch (Exception e) {
            log.error("摸底测试单词不足20个！", e);
        }
        try {
            listen = vocabularies.subList(20, 30);
            results2 = testResultUtil.getWordTestes(type2, listen.size(), listen, student.getVersion(), phase);
        } catch (Exception e) {
            log.error("摸底测试单词不足30个！", e);
        }

        results.forEach(testResult -> testResult.setType(type[0]));
        results1.forEach(testResult -> testResult.setType(type1[0]));
        results2.forEach(testResult -> testResult.setType(type2[0]));

        results.addAll(results1);
        results.addAll(results2);

        Map<String, Object> map = new HashMap<>(16);
        map.put("courseName", courseName);
        map.put("subject", results);

        return ServerResponse.createBySuccess(map);
    }

    @Override
    public ServerResponse<List<TestResultVO>> getWordUnitTest(HttpSession session, Long unitId, String studyModel,
                                                              Boolean isTrue) {
        Student student = getStudent(session);
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        // 获取当前单元下的所有单词
        int easyOrHard;
        String[] type;
        if ("慧记忆".equals(studyModel)) {
            type = new String[]{"英译汉", "汉译英"};
            easyOrHard = 1;
        } else {
            type = new String[]{"听力理解"};
            easyOrHard = 2;
        }
        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHardAndModelType(student.getId(), unitId, easyOrHard, 1);
        List<Vocabulary> vocabularies = redisOpt.getWordInfoInUnitAndGroup(unitId, learnNew.getGroup());
        // 需要封装的测试题个数
        int subjectNum = Math.min(vocabularies.size(), 20);

        List<TestResultVO> results = testResultUtil.getWordTestesForUnit(type, subjectNum, vocabularies, unitId);
        if (!"慧记忆".equals(studyModel)) {
            for (TestResultVO testResult : results) {
                testResult.setType("单元闯关测试");
            }
        }

        // 当测试模块是“慧默写”时，将题目修改为中文
        if ("慧默写".equals(studyModel)) {
            results.parallelStream().forEach(result -> result.getSubject().forEach((key, value) -> {
                if (value) {
                    result.setChinese(key.toString());
                }
            }));
        }
        session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        return ServerResponse.createBySuccess(results);
    }

    /**
     * 获取例句测试
     *
     * @param unitId
     * @return
     */
    @Override
    public ServerResponse<Object> gitUnitSentenceTest(HttpSession session, Long unitId) {
        Student student = getStudent(session);
        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHardAndModelType(student.getId(), unitId, 1, 2);
        //获取单元句子
        List<Sentence> sentences = sentenceMapper.selectByUnitIdAndGroup(unitId, learnNew.getGroup());
        List<Sentence> sentenceList = null;
        //获取干扰项句子 在当前课程下选择
        if (sentences.size() < 4) {
            //获取测试单元所在的课程
            Long courseId = sentenceUnitMapper.getCourseIdByunitId(unitId.intValue());
            sentenceList = sentenceMapper.selectRoundSentence(courseId);
        }
        List<Object> list = testSentenceUtil.resultTestSentence(sentences, sentenceList);
        return ServerResponse.createBySuccess(list);
    }

    @Override
    @GoldChangeAnnotation
    @TestChangeAnnotation(isUnitTest = false)
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> saveCapSentenceTest(HttpSession session, WordUnitTestDTO wordUnitTestDTO) {
        Student student = getStudent(session);
        TestRecord testRecord;
        saveTeksData.insertLearnExtend(wordUnitTestDTO.getFlowId(), wordUnitTestDTO.getUnitId()[0], student, "音译测试", 1, 2);
        wordUnitTestDTO.setClassify(8);

        // 判断当前单元是不是首次进行测试
        boolean isFirst = false;
        TestRecord testRecordOld = testRecordMapper.selectByStudentIdAndUnitId(student.getId(),
                wordUnitTestDTO.getUnitId()[0], "音译测试", "音译测试");
        if (testRecordOld == null) {
            isFirst = true;
        }

        int goldCount = this.saveGold(isFirst, wordUnitTestDTO, student, testRecordOld, true);
        testRecord = new TestRecord();
        if (testRecordOld == null) {
            // 首次测试大于或等于80分，超过历史最高分次数 +1
            if (wordUnitTestDTO.getPoint() >= PointConstant.EIGHTY) {
                testRecord.setBetterCount(1);
            } else {
                testRecord.setBetterCount(0);
            }
        } else {
            testRecord.setBetterCount(testRecordOld.getBetterCount());
        }
        testRecord.setCourseId(wordUnitTestDTO.getCourseId());
        testRecord.setUnitId(wordUnitTestDTO.getUnitId()[0]);
        testRecord.setPoint(wordUnitTestDTO.getPoint());
        int rightCount = 0;
        int errorCount = 0;
        if (wordUnitTestDTO.getErrorCount() != null) {
            errorCount = wordUnitTestDTO.getErrorCount();
        }
        if (wordUnitTestDTO.getRightCount() != null) {
            rightCount = wordUnitTestDTO.getRightCount();
        }
        testRecord.setErrorCount(errorCount);
        testRecord.setRightCount(rightCount);
        testRecord.setQuantity(errorCount + rightCount);
        testRecord.setAwardGold(goldCount);
        testRecord.setGenre("音译测试");
        testRecord.setStudentId(student.getId());
        Date date = new Date();
        saveTestRecordTime(testRecord, session, date);
        if (wordUnitTestDTO.getErrorCount() != null && wordUnitTestDTO.getRightCount() != null) {
            testRecord.setQuantity(wordUnitTestDTO.getErrorCount() + wordUnitTestDTO.getRightCount());
        }
        testRecord.setStudyModel("音译测试");
        if (student.getBonusExpires() != null) {
            if (student.getBonusExpires().getTime() > System.currentTimeMillis()) {
                Double doubleGoldCount = goldCount * 0.2;
                student.setSystemGold(student.getSystemGold() + doubleGoldCount);
                testRecord.setAwardGold(goldCount + doubleGoldCount.intValue());
            }
        }
        return getObjectServerResponse(session, wordUnitTestDTO, student, testRecord);
    }


    @Override
    @GoldChangeAnnotation
    @TestChangeAnnotation(isUnitTest = false)
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> saveCapTeksTest(HttpSession session, WordUnitTestDTO wordUnitTestDTO) {

        Student student = getStudent(session);
        TestRecord testRecord;
        saveTeksData.insertLearnExtend(wordUnitTestDTO.getFlowId(), wordUnitTestDTO.getUnitId()[0], student, "闯关测试", 1, 3);
        wordUnitTestDTO.setClassify(9);

        // 判断当前单元是不是首次进行测试
        boolean isFirst = false;
        TestRecord testRecordOld = testRecordMapper.selectByStudentIdAndUnitId(student.getId(),
                wordUnitTestDTO.getUnitId()[0], "课文测试", "课文测试");
        if (testRecordOld == null) {
            isFirst = true;
        }

        int goldCount = this.saveGold(isFirst, wordUnitTestDTO, student, testRecordOld, true);
        testRecord = new TestRecord();
        if (testRecordOld == null) {
            // 首次测试大于或等于80分，超过历史最高分次数 +1
            if (wordUnitTestDTO.getPoint() >= PointConstant.EIGHTY) {
                testRecord.setBetterCount(1);
            } else {
                testRecord.setBetterCount(0);
            }
        } else {
            testRecord.setBetterCount(testRecordOld.getBetterCount());
        }
        testRecord.setCourseId(wordUnitTestDTO.getCourseId());
        testRecord.setUnitId(wordUnitTestDTO.getUnitId()[0]);
        testRecord.setPoint(wordUnitTestDTO.getPoint());
        int rightCount = 0;
        int errorCount = 0;
        if (wordUnitTestDTO.getErrorCount() != null) {
            errorCount = wordUnitTestDTO.getErrorCount();
        }
        if (wordUnitTestDTO.getRightCount() != null) {
            rightCount = wordUnitTestDTO.getRightCount();
        }
        testRecord.setErrorCount(errorCount);
        testRecord.setRightCount(rightCount);
        testRecord.setQuantity(errorCount + rightCount);
        testRecord.setGenre("课文测试");
        testRecord.setStudentId(student.getId());
        Date date = new Date();
        saveTestRecordTime(testRecord, session, date);
        if (wordUnitTestDTO.getErrorCount() != null && wordUnitTestDTO.getRightCount() != null) {
            testRecord.setQuantity(wordUnitTestDTO.getErrorCount() + wordUnitTestDTO.getRightCount());
        }
        testRecord.setAwardGold(goldCount);
        testRecord.setStudyModel("课文测试");
        if (student.getBonusExpires() != null) {
            if (student.getBonusExpires().getTime() > System.currentTimeMillis()) {
                Double doubleGoldCount = goldCount * 0.2;
                student.setSystemGold(student.getSystemGold() + doubleGoldCount);
                testRecord.setAwardGold(goldCount + doubleGoldCount.intValue());
            }
        }
        Learn learn = new Learn();
        learn.setLearnTime(new Date());
        learn.setUpdateTime(new Date());
        learn.setCourseId(wordUnitTestDTO.getCourseId());
        learn.setUnitId(wordUnitTestDTO.getUnitId()[0]);
        learn.setType(1);
        learn.setStudyModel(testRecord.getStudyModel());
        learn.setStudentId(student.getId());
        Long aLong = learnMapper.selTeksLearn(learn);
        if (aLong != null) {
            learn.setId(aLong);
            learnMapper.updTeksLearn(learn);
        } else {
            learnMapper.insert(learn);
        }
        return getObjectServerResponse(session, wordUnitTestDTO, student, testRecord);
    }


    private ServerResponse<Object> getObjectServerResponse(HttpSession session, WordUnitTestDTO wordUnitTestDTO, Student student, TestRecord testRecord) {
        session.removeAttribute(TimeConstant.BEGIN_START_TIME);
        Integer point = wordUnitTestDTO.getPoint();

        Map<String, Object> resultMap = new HashMap<>(16);
        //获取测试有效次数
        int number = testRecordMapper.selCount(student.getId(), testRecord.getCourseId(), testRecord.getUnitId(),
                testRecord.getStudyModel(), testRecord.getGenre());
        resultMap.put("energy", getEnergy(student, wordUnitTestDTO.getPoint(), number));
        resultMap.put("gold", testRecord.getAwardGold());
        if (point < PointConstant.EIGHTY) {
            resultMap.put("petName", petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_LESS_EIGHTY));
            resultMap.put("text", "很遗憾，闯关失败，再接再厉。");
            resultMap.put("backMsg", new String[]{"别气馁，已经超越了", TestPointUtil.getPercentage(point), "的同学，继续努力吧！"});
            testRecord.setPass(2);
        } else if (point < PointConstant.NINETY) {
            resultMap.put("petName", petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_EIGHTY_TO_HUNDRED));
            resultMap.put("text", "闯关成功，独孤求败！");
            resultMap.put("backMsg", new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(point), "的同学，再接再励！"});
            testRecord.setPass(1);
        } else {
            resultMap.put("petName", petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_HUNDRED));
            resultMap.put("text", "恭喜你刷新了纪录！");
            resultMap.put("backMsg", new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(point), "的同学，再接再励！"});
            testRecord.setPass(1);
        }
        resultMap.put("point", point);
        resultMap.put("imgUrl", AliyunInfoConst.host + student.getPartUrl());

        testRecordMapper.insert(testRecord);
        studentMapper.updateById(student);
        return ServerResponse.createBySuccess(resultMap);
    }

    @Override
    public ServerResponse<TestDetailVo> getTestDetail(HttpSession session, Long testId, Integer type) {
        Student student = getStudent(session);
        TestRecord testRecord = testRecordMapper.selectById(testId);
        TestDetailVo testDetailVo = testRecordMapper.selectTestDetailVo(student.getId(), testId, type);
        if (testDetailVo.getTitle() != null) {
            testDetailVo.setTitle(testDetailVo.getTitle().replaceAll("例句", "句型"));
        }
        testDetailVo.setUseTime(getUseTime(testDetailVo.getUseTime()));
        if (testDetailVo.getIsWrite().contains("写")) {
            testDetailVo.setIsWrite("1");
        } else {
            testDetailVo.setIsWrite("0");
        }
        List<TestRecordInfo> testRecordInfos = testRecordMapper.selectTestRecordInfo(testId);
        if (testRecord != null && Objects.equals("慧听写", testRecord.getStudyModel())) {
            // 慧听写题目就是正确答案
            testRecordInfos.forEach(testRecordInfo -> testRecordInfo.setWord(testRecordInfo.getAnswer()));
        }
        testDetailVo.setInfos(testRecordInfos);
        return ServerResponse.createBySuccess(testDetailVo);
    }

    /**
     * 计算测试用时
     *
     * @param useTime
     * @return
     */
    private String getUseTime(String useTime) {
        if (!StringUtils.isEmpty(useTime)) {
            int intUseTime = Integer.parseInt(useTime);
            return "用时：" + (intUseTime / 60) + " 分 " + (intUseTime % 60) + "秒";
        }
        return "用时：0 分 0 秒";
    }


    /**
     * 保存单元闯关测试
     *
     * @param session
     * @param wordUnitTestDTO 需要保存的数据的参数
     * @param testDetail
     * @return
     */
    @Override
    @GoldChangeAnnotation
    @TestChangeAnnotation
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<TestResultVo> saveWordUnitTest(HttpSession session, WordUnitTestDTO wordUnitTestDTO, String testDetail) {

        Student student = getStudent(session);
        if (StringUtils.isEmpty(student.getPetName())) {
            student.setPetName("大明白");
            student.setPartUrl(PetImageConstant.DEFAULT_IMG.replace(AliyunInfoConst.host, ""));
        }
        int easyOrHard;
        @NotNull(message = "测试类型不能为空") Integer classify1 = wordUnitTestDTO.getClassify();
        if (classify1.equals(0) || classify1.equals(1) || classify1.equals(4) || classify1.equals(5)) {
            easyOrHard = 1;
        } else {
            easyOrHard = 2;
        }
        TestResultVo vo = new TestResultVo();
        // 是否是第一次进行当前模块下的单元闯关测试标识
        boolean isFirst = false;
        Long[] errorWordId = wordUnitTestDTO.getErrorWordId();
        Long[] unitId = wordUnitTestDTO.getUnitId();
        Long courseId = wordUnitTestDTO.getCourseId();
        Integer classify = wordUnitTestDTO.getClassify();
        String type = commonMethod.getTestType(wordUnitTestDTO.getClassify());

        Integer point = wordUnitTestDTO.getPoint();

        // 保存测试记录
        // 查看是否已经有该单元当前模块的单元闯关测试记录
        TestRecord testRecord = testRecordMapper.selectByStudentIdAndUnitId(student.getId(),
                wordUnitTestDTO.getUnitId()[0], "单元闯关测试", type);
        if (testRecord == null) {
            isFirst = true;
        }

        // 根据不同分数奖励学生金币
        Integer goldCount = 0;
        int addEnergy = 0;
        if (!redisOpt.isRepeatSubmit(student.getId(), (Date) session.getAttribute(TimeConstant.BEGIN_START_TIME))) {
            //saveTestLearnAndCapacity.saveTestAndCapacity(correctWord, errorWord, correctWordId, errorWordId, session, unitId, classify);
            //保存错误信息
            saveError(unitId[0], errorWordId, student, classify, 1);
            goldCount = this.saveGold(isFirst, wordUnitTestDTO, student, testRecord, false);

            testRecord = this.saveTestRecord(courseId, student, session, wordUnitTestDTO, testRecord, goldCount);

            /*//获取测试有效次数
            int number = testRecordMapper.selCount(student.getId(), testRecord.getCourseId(), testRecord.getUnitId(),
                    testRecord.getStudyModel(), testRecord.getGenre());*/
            // 获取需要奖励的能量值
            addEnergy = getEnergy(student, point, 0);
        }

        String msg;
        // 默写
        if (classify == 3 || classify == 6) {
            msg = getMessage(student, vo, testRecord, point, PointConstant.FIFTY);
            if (point >= PointConstant.FIFTY) {
                ccieUtil.saveCcieTest(student, 1, classify, courseId, unitId[0], point);
            }
        } else if (classify == 4 || classify == 2) {
            // 听力
            msg = getMessage(student, vo, testRecord, point, PointConstant.SIXTY);
            if (point >= PointConstant.SIXTY) {
                ccieUtil.saveCcieTest(student, 1, classify, courseId, unitId[0], point);
            }
        } else {
            msg = getMessage(student, vo, testRecord, point, PointConstant.EIGHTY);
            if (point >= PointConstant.EIGHTY) {
                ccieUtil.saveCcieTest(student, 1, classify, courseId, unitId[0], point);
            }
        }

        if (testRecord != null) {
            LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHardAndModelType(student.getId(), unitId[0], easyOrHard, 1);
            testRecord.setGroup(learnNew.getGroup());
            testRecordMapper.insert(testRecord);
            // 保存测试记录详情
            if (testDetail != null) {
                this.saveTestDetail(testDetail, testRecord.getId(), classify, student);
            }
        }

        vo.setMsg(msg);
        vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, point, "单元闯关测试"));
        if (student.getBonusExpires() != null) {
            if (student.getBonusExpires().getTime() > System.currentTimeMillis()) {
                Double v = goldCount * 0.2;
                student.setSystemGold(student.getSystemGold() + v);
                goldCount = goldCount + v.intValue();
            }
        }
        vo.setGold(goldCount);
        vo.setEnergy(addEnergy);
        studentMapper.updateById(student);
        getLevel(session);
        session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        session.removeAttribute(TimeConstant.BEGIN_START_TIME);
        return ServerResponse.createBySuccess(vo);
    }

    private void saveError(Long unitId, Long[] errorIds, Student student, Integer classify, Integer modelType) {
        if (errorIds != null) {
            int easyOrHard = 1;
            if (classify.equals(2) || classify.equals(3) || classify.equals(6)) {
                easyOrHard = 2;
            }
            LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHardAndModelType(student.getId(), unitId, easyOrHard, modelType);

            for (Long errorId : errorIds) {
                saveData.saveErrorLearnLog(unitId, getModelInteger(classify), easyOrHard, getModel(classify), learnNew, errorId);
            }
        }
    }

    private String getModel(Integer classIfy) {
        //0=单词图鉴 1=慧记忆 2=慧听写 3=慧默写 4=例句听力 5=例句翻译 6=例句默写
        if (classIfy.equals(0)) {
            return "单词图鉴";
        }
        if (classIfy.equals(1)) {
            return "慧记忆";
        }
        if (classIfy.equals(2)) {
            return "慧听写";
        }
        if (classIfy.equals(3)) {
            return "慧默写";
        }
        if (classIfy.equals(4)) {
            return "例句听力";
        }
        if (classIfy.equals(5)) {
            return "例句翻译";
        }
        if (classIfy.equals(6)) {
            return "例句默写";
        }
        return null;

    }

    private Integer getModelInteger(Integer classIfy) {
        if (classIfy.equals(0)) {
            return 1;
        }
        if (classIfy.equals(1)) {
            return 3;
        }
        if (classIfy.equals(2)) {
            return 4;
        }
        if (classIfy.equals(3)) {
            return 5;
        }
        if (classIfy.equals(4)) {
            return 7;
        }
        if (classIfy.equals(5)) {
            return 8;
        }
        if (classIfy.equals(6)) {
            return 10;
        }
        return 0;
    }

    /**
     * 保存句子测试
     *
     * @param session
     * @param wordUnitTestDTO 需要保存的数据的参数
     * @param testDetail
     * @return
     */
    @Override
    @GoldChangeAnnotation
    @TestChangeAnnotation
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<TestResultVo> saveSentenceUnitTest(HttpSession session, WordUnitTestDTO wordUnitTestDTO, String testDetail) {

        Student student = super.getStudent(session);
        if (StringUtils.isEmpty(student.getPetName())) {
            student.setPetName("大明白");
            student.setPartUrl(PetImageConstant.DEFAULT_IMG.replace(AliyunInfoConst.host, ""));
        }

        TestResultVo vo = new TestResultVo();
        // 是否是第一次进行当前模块下的单元闯关测试标识
        boolean isFirst = false;
        Long[] errorWordId = wordUnitTestDTO.getErrorWordId();
        Long[] unitId = wordUnitTestDTO.getUnitId();
        Long courseId = unitMapper.selectCourseIdByUnitId(unitId[0]);
        Integer classify = wordUnitTestDTO.getClassify();
        String type = commonMethod.getTestType(wordUnitTestDTO.getClassify());

        Integer point = wordUnitTestDTO.getPoint();

        //获取测试有效次数
        int number = testRecordMapper.selCount(student.getId(), courseId, unitId[0],
                type, "句子测试");
        // 获取需要奖励的能量值
        int addEnergy = getEnergy(student, point, number);

        // 保存测试记录
        // 查看是否已经有该单元当前模块的单元闯关测试记录
        TestRecord testRecord = testRecordMapper.selectByStudentIdAndUnitId(student.getId(),
                wordUnitTestDTO.getUnitId()[0], "句子测试", type);
        if (testRecord == null) {
            isFirst = true;
        }
        saveError(unitId[0], errorWordId, student, classify, 2);

        // 根据不同分数奖励学生金币
        int goldCount = this.saveGold(isFirst, wordUnitTestDTO, student, testRecord, true);

        testRecord = this.saveTestRecord(courseId, student, session, wordUnitTestDTO, testRecord, goldCount);

        String msg;
        // 默写
        if (classify == 3 || classify == 6) {
            msg = getMessage(student, vo, testRecord, point, PointConstant.FIFTY);
        } else if (classify == 4 || classify == 2) {
            // 听力
            msg = getMessage(student, vo, testRecord, point, PointConstant.SIXTY);
        } else {
            msg = getMessage(student, vo, testRecord, point, PointConstant.EIGHTY);
        }

        testRecordMapper.insert(testRecord);
        // 保存测试记录详情
        if (testDetail != null) {
            this.saveTestDetail(testDetail, testRecord.getId(), classify, student);
        }

        vo.setMsg(msg);
        vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, point, "句子测试"));
        vo.setGold(goldCount);
        vo.setEnergy(addEnergy);
        ccieUtil.saveCcieTest(student, 1, classify, courseId, unitId[0], point);
        studentMapper.updateById(student);
        session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        getLevel(session);
        return ServerResponse.createBySuccess(vo);
    }

    /**
     * 根据学生分数获取响应的说明语
     *
     * @param student
     * @param vo
     * @param testRecord
     * @param point
     * @param pass       及格分数
     * @return
     */
    private String getMessage(Student student, TestResultVo vo, TestRecord testRecord, Integer point, int pass) {
        if (testRecord == null) {
            testRecord = new TestRecord();
        }
        testRecord.setPoint(point);
        return getTestMessage(student, vo, testRecord, pass, petSayUtil);
    }

    public static String getTestMessage(Student student, TestResultVo vo, TestRecord testRecord, int pass, PetSayUtil petSayUtil) {
        String msg;
        if (testRecord.getPoint() < pass) {
            msg = "很遗憾，闯关失败，再接再厉。";
            vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_LESS_EIGHTY));
            vo.setBackMsg(new String[]{"别气馁，已经超越了", TestPointUtil.getPercentage(testRecord.getPoint()), "的同学，继续努力吧！"});
            testRecord.setPass(2);
        } else if (testRecord.getPoint() < PointConstant.HUNDRED) {
            msg = "闯关成功，独孤求败！";
            vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_EIGHTY_TO_HUNDRED));
            vo.setBackMsg(new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(testRecord.getPoint()), "的同学，再接再励！"});
            testRecord.setPass(1);
        } else {
            msg = "恭喜你刷新了纪录！";
            vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_HUNDRED));
            vo.setBackMsg(new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(testRecord.getPoint()), "的同学，再接再励！"});
            testRecord.setPass(1);
        }
        return msg;
    }

    /**
     * @param testDetail
     * @param testRecordId
     * @param modelType    1=慧记忆 2=慧听写 3=慧默写 4=例句听力 5=例句翻译 6=例句默写
     */
    private void saveTestDetail(String testDetail, Long testRecordId, int modelType, Student student) {
        if (StringUtils.isEmpty(testDetail)) {
            return;
        }
        JSONArray jsonArray = JSONObject.parseArray(testDetail);
        if (jsonArray != null && jsonArray.size() > 0) {
            JSONObject object;
            List<TestRecordInfo> testRecordInfos = new ArrayList<>(20);
            TestRecordInfo testRecordInfo;
            for (Object aJsonArray : jsonArray) {
                testRecordInfo = new TestRecordInfo();
                object = (JSONObject) aJsonArray;
                String word = object.getString("title");
                final String[] selected = {null};
                if (modelType == 1 || modelType == 0) {
                    // 试卷题目
                    JSONObject subject = object.getJSONObject("subject");
                    final int[] j = {0};
                    TestRecordInfo finalTestRecordInfo = testRecordInfo;
                    JSONObject finalObject = object;
                    subject.forEach((key, val) -> {
                        if (Objects.equals(subject.get(key), true)) {
                            finalTestRecordInfo.setAnswer(matchSelected(j[0]));
                        }
                        this.setOptions(finalTestRecordInfo, j[0], key);
                        if (finalObject.getJSONObject("userInput") != null) {
                            selected[0] = matchSelected(finalObject.getJSONObject("userInput").getInteger("optionIndex"));
                        }
                        finalTestRecordInfo.setWord(word);
                        j[0]++;
                    });
                } else {
                    selected[0] = object.getString("userInput");
                    String chinese = object.getString("chinese");
                    testRecordInfo.setWord(chinese);
                    testRecordInfo.setAnswer(word);
                }

                testRecordInfo.setTestId(testRecordId);
                testRecordInfo.setSelected(selected[0]);
                testRecordInfos.add(testRecordInfo);
            }
            if (testRecordInfos.size() > 0) {
                try {
                    testRecordInfoMapper.insertList(testRecordInfos);
                } catch (Exception e) {
                    log.error("学生测试记录详情保存失败：studentId=[{}], testId=[{}], modelType=[{}], error=[{}]",
                            student.getId(), testRecordId, modelType, e.getMessage());
                }
            }
        }
    }


    private void setOptions(TestRecordInfo testRecordInfo, int j, String option) {
        switch (j) {
            case 0:
                testRecordInfo.setOptionA(option);
                break;
            case 1:
                testRecordInfo.setOptionB(option);
                break;
            case 2:
                testRecordInfo.setOptionC(option);
                break;
            case 3:
                testRecordInfo.setOptionD(option);
                break;
            default:
        }
    }

    /**
     * 匹配选项
     *
     * @param integer
     * @return
     */
    static String matchSelected(Integer integer) {
        switch (integer) {
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
            default:
                return null;
        }
    }

    /**
     * 保存金币变化日志信息
     *
     * @param student
     * @param goldCount       奖励金币数
     * @param wordUnitTestDTO
     * @param model           测试模块
     */
    private void saveLog(Student student, int goldCount, WordUnitTestDTO wordUnitTestDTO, String model) {
        student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), goldCount));
        String msg;
        if (wordUnitTestDTO != null) {
            msg = "id为：" + student.getId() + "的学生在[" + commonMethod.getTestType(wordUnitTestDTO.getClassify())
                    + "]模块下的单元闯关测试中闯关成功，获得#" + goldCount + "#枚金币";
        } else {
            msg = "id为：" + student.getId() + "的学生在[" + model + "]模块下，获得#" + goldCount + "#枚金币";
        }
        if (goldCount > 0) {
            try {
                Long courseId = wordUnitTestDTO == null ? null : wordUnitTestDTO.getCourseId();
                Long unitId = (wordUnitTestDTO == null || wordUnitTestDTO.getUnitId() == null || wordUnitTestDTO.getUnitId().length == 0) ? null : wordUnitTestDTO.getUnitId()[0];
                super.saveRunLog(student, 4, courseId, unitId, msg);
            } catch (Exception e) {
                log.error("保存学生[{} - {} - {}]日志记录出错！", student.getId(), student.getAccount(), student.getStudentName(), e);
            }
        }
        log.info(msg);
    }

    @Override
    @TestChangeAnnotation
    @GoldChangeAnnotation
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse savePhoneticSymbolUnitTest(HttpSession session, UnitTestDto dto) {
        Student student = super.getStudent(session);
        // 判断是否是首次测试
        boolean isFirst = false;
        TestRecord testRecord = testRecordMapper.selectByStudentIdAndUnitId(student.getId(),
                dto.getUnitId(), TestGenreConstant.UNIT_TEST, PhoneticSymbolServiceImpl.STUDY_MODEL);
        if (testRecord != null) {
            isFirst = true;
        }

        Integer point = dto.getPoint();

        testRecord = new TestRecord();
        testRecord.setStudentId(student.getId());
        testRecord.setUnitId(dto.getUnitId());
        testRecord.setGenre(TestGenreConstant.UNIT_TEST);
        Date date = new Date();
        saveTestRecordTime(testRecord, session, date);
        testRecord.setPoint(point);
        testRecord.setHistoryBadPoint(point);
        testRecord.setHistoryBestPoint(point);
        testRecord.setQuantity(dto.getRightCount() + dto.getErrorCount());
        testRecord.setErrorCount(dto.getErrorCount());
        testRecord.setRightCount(dto.getRightCount());
        testRecord.setStudyModel(StudyModelConstant.PHONETIC_SYMBOL_TEST);

        WordUnitTestDTO wordUnitTestDTO = new WordUnitTestDTO();
        wordUnitTestDTO.setClassify(11);
        wordUnitTestDTO.setUnitId(new Long[]{dto.getUnitId()});
        wordUnitTestDTO.setPoint(point);
        Integer goldCount = this.saveGold(isFirst, wordUnitTestDTO, student, testRecord, true);

        testRecord.setAwardGold(goldCount);
        TestResultVo vo = new TestResultVo();
        String message = getMessage(student, vo, testRecord, point, PointConstant.EIGHTY);
        testRecord.setExplain(message);

        vo.setMsg(message);
        vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, point, TestGenreConstant.UNIT_TEST));
        vo.setGold(getBonusGold(student, goldCount));
        //获取测试有效次数
        int number = testRecordMapper.selCount(student.getId(), testRecord.getCourseId(), testRecord.getUnitId(),
                testRecord.getStudyModel(), testRecord.getGenre());
        vo.setEnergy(super.getEnergy(student, point, number));

        testRecordMapper.insert(testRecord);
        studentMapper.updateById(student);
        getLevel(session);

        // 将学生学习记录置为已学习状态
        learnMapper.updateTypeByStudentIdAndUnitId(student.getId(), dto.getUnitId(), PhoneticSymbolServiceImpl.STUDY_MODEL, 2);

        session.removeAttribute(TimeConstant.BEGIN_START_TIME);
        session.setAttribute(UserConstant.CURRENT_STUDENT, student);

        return ServerResponse.createBySuccess(vo);
    }

    /**
     * 获取加成后的金币
     *
     * @param student
     * @param goldCount
     * @return
     */
    private Integer getBonusGold(Student student, Integer goldCount) {
        if (student.getBonusExpires() != null) {
            if (student.getBonusExpires().getTime() > System.currentTimeMillis()) {
                Double v = goldCount * 0.2;
                student.setSystemGold(student.getSystemGold() + v);
                goldCount = goldCount + v.intValue();
            }
        }
        return goldCount;
    }

    /**
     * 根据测试成绩计算奖励金币数
     *
     * @param isFirst         是否是第一次进行该模块下的单元闯关测试
     * @param wordUnitTestDTO
     * @param student
     * @param testRecord
     * @param type            true 计算最高分  false不计算最高分
     * @return 学生应奖励金币数
     */
    private Integer saveGold(boolean isFirst, WordUnitTestDTO wordUnitTestDTO, Student student, TestRecord testRecord, boolean type) {
        int point = wordUnitTestDTO.getPoint();
        int goldCount = 0;
        // 查询当前单元测试历史最高分数
        Integer betterPoint = null;
        if (type) {
            betterPoint = testRecordMapper.selectUnitTestMaxPointByStudyModel(student.getId(), wordUnitTestDTO.getUnitId()[0],
                    wordUnitTestDTO.getClassify());
            // 当前分数没有超过历史最高分，不获取金币
            if (betterPoint != null && betterPoint >= point) {
                return 0;
            }
        }
        if (isFirst) {
            goldCount = getGoldCount(wordUnitTestDTO, student, point);
        } else {
            if (type) {
                if (betterPoint == null) {
                    betterPoint = 0;
                }
                // 非首次测试成绩本次测试成绩大于历史最高分，超过历史最高分次数 +1并且金币奖励翻倍
                if (betterPoint < point) {
                    int betterCount = (testRecord.getBetterCount() == null ? 0 : testRecord.getBetterCount()) + 1;
                    testRecord.setBetterCount(betterCount);
                    goldCount = getGoldCount(wordUnitTestDTO, student, point);
                }
            } else {
                int betterCount = (testRecord.getBetterCount() == null ? 0 : testRecord.getBetterCount()) + 1;
                testRecord.setBetterCount(betterCount);
                goldCount = getGoldCount(wordUnitTestDTO, student, point);
            }

        }
        return testGoldUtil.addGold(student, goldCount);
    }

    private int getGoldCount(WordUnitTestDTO wordUnitTestDTO, Student student, int point) {
        int goldCount = this.getGold(point);
        this.saveLog(student, goldCount, wordUnitTestDTO, null);
        return goldCount;
    }

    /**
     * 保存测试记录,只保留所有测试中成绩最好的一次测试记录
     *
     * @param courseId
     * @param student
     * @param session
     * @param wordUnitTestDTO
     * @param testRecordOld   上次单元闯关测试的记录
     * @param goldCount       奖励的金币数
     * @return
     */
    private TestRecord saveTestRecord(Long courseId, Student student, HttpSession session, WordUnitTestDTO wordUnitTestDTO,
                                      TestRecord testRecordOld, Integer goldCount) {
        // 新生成的测试记录
        TestRecord testRecord;
        int point = wordUnitTestDTO.getPoint();
        String[] errorWord = wordUnitTestDTO.getErrorWord();
        String[] correctWord = wordUnitTestDTO.getCorrectWord();
        int quantity = 0;
        if (errorWord != null && correctWord != null) {
            quantity = errorWord.length + correctWord.length;
        } else if (errorWord != null) {
            quantity = errorWord.length;
        } else if (correctWord != null) {
            quantity = correctWord.length;
        }
        if (testRecordOld == null) {
            testRecord = new TestRecord();
            // 首次测试大于或等于80分，超过历史最高分次数 +1
            if (point >= PointConstant.EIGHTY) {
                testRecord.setBetterCount(1);
            } else {
                testRecord.setBetterCount(0);
            }
        } else {
            testRecord = new TestRecord();
            testRecord.setBetterCount(testRecordOld.getBetterCount());
        }
        testRecord.setCourseId(courseId);
        testRecord.setErrorCount(errorWord == null ? 0 : errorWord.length);
        testRecord.setGenre("单元闯关测试");
        testRecord.setPoint(point);
        testRecord.setQuantity(quantity);
        testRecord.setRightCount(correctWord == null ? 0 : correctWord.length);
        testRecord.setStudentId(student.getId());
        testRecord.setStudyModel(commonMethod.getTestType(wordUnitTestDTO.getClassify()));
        Date date = new Date();
        saveTestRecordTime(testRecord, session, date);
        testRecord.setUnitId(wordUnitTestDTO.getUnitId()[0]);
        testRecord.setAwardGold(goldCount);

        testRecord.setExplain(getUnitTestMsg(point));
        return testRecord;
    }

    public static String getUnitTestMsg(int point) {
        if (point >= 0 && point < PointConstant.EIGHTY) {
            return "很遗憾，闯关失败，再接再厉。";
        }
        if (point >= PointConstant.EIGHTY && point < PointConstant.HUNDRED) {
            return "闯关成功，独孤求败！";
        }
        if (point == PointConstant.HUNDRED) {
            return "恭喜你刷新了纪录！";
        }
        return "";
    }

    @Override
    public ServerResponse<List<SentenceTranslateVo>> getSentenceUnitTest(HttpSession session, Long unitId, Integer type, Integer pageNum, Integer studyModel) {
        if (session.getAttribute(TimeConstant.BEGIN_START_TIME) == null) {
            session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        }
        Integer easyOrHard = (studyModel == null || studyModel != 6) ? 1 : 2;

        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHardAndModelType(getStudentId(session), unitId, easyOrHard, 2);
        // 获取当前单元下其中一个例句
        List<Sentence> sentences = sentenceMapper.selectByUnitIdAndGroup(unitId, learnNew.getGroup());
        List<SentenceTranslateVo> sentenceTestResults = testResultUtil.getSentenceTestResults(sentences, MathUtil.getRandom(4, 6), type);
        return ServerResponse.createBySuccess(sentenceTestResults);
    }


    @Override
    public ServerResponse<Object> showRecord(String courseId, Integer type, HttpSession session, Integer page, Integer rows) {
        Student student = getStudent(session);
        Long studentId = student.getId();

        if (page == null) {
            page = 1;
        }
        if (rows != null) {
            PageHelper.startPage(page, rows);
        }
        List<TestRecordVo> records = testRecordMapper.showRecord(studentId, type);
        PageInfo<TestRecordVo> testRecordPageInfo = new PageInfo<>(records);

        // 每个测试记录下含有测试详情个数，如果没有测试详情，不显示详情按钮
        Map<Long, Map<Long, Long>> testDetailCountMap = null;
        if (records.size() > 0) {
            testDetailCountMap = testRecordInfoMapper.countByRecordIds(records);
        }

        // 封装后返回
        List<Map<String, Object>> result = new ArrayList<>();

        Long recordId;
        String studyModel;
        for (TestRecordVo record : records) {
            recordId = record.getId();
            Map<String, Object> map = new HashMap<>(16);
            studyModel = record.getStudyModel().replace("例句", "句型");
            map.put("id", recordId);

            if (Objects.equals("单元闯关测试", record.getGenre())) {
                map.put("genre", studyModel);
            } else if (record.getGenre() != null && record.getGenre().contains("五维测试")) {
                map.put("genre", record.getGenre());
            } else {
                map.put("genre", studyModel + "-" + record.getGenre());
            }

            if (!"单词图鉴".equals(record.getStudyModel()) && testDetailCountMap != null && testDetailCountMap.get(recordId) != null
                    && testDetailCountMap.get(recordId).get("count") != null
                    && testDetailCountMap.get(recordId).get("count") > 0) {
                map.put("isShow", true);
            } else {
                map.put("isShow", false);
            }

            // 测试完成时间
            map.put("testStartTime", record.getTestEndTime());
            map.put("point", record.getPoint());
            map.put("awardGold", record.getAwardGold());
            map.put("quantity", record.getQuantity());
            map.put("courseName", record.getCourseName());
            map.put("unitName", record.getUnitName());
            String explain = record.getExplain();
            if (StringUtils.isNotEmpty(explain) && explain.contains("#")) {
                map.put("explain", explain.substring(explain.lastIndexOf("#") + 1));
            } else {
                map.put("explain", explain);
            }
            result.add(map);
        }

        PageInfo<Map<String, Object>> mapPageInfo = new PageInfo<>(result);
        mapPageInfo.setTotal(testRecordPageInfo.getTotal());
        mapPageInfo.setPages(testRecordPageInfo.getPages());
        mapPageInfo.setPageNum(testRecordPageInfo.getPageNum());
        return ServerResponse.createBySuccess(mapPageInfo);
    }

    /**
     * 学前测试,从课程取30道题
     * 保存的时候只保存测试记录
     *
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> getPreSchoolTest(HttpSession session) {
        Student student = getStudent(session);
        Long courseId = student.getCourseId();

        // 1.题类型
        String[] type = {"英译汉", "汉译英", "听力理解"};
        // 随机取三十道题
        List<Vocabulary> vocabularies = vocabularyMapper.getRandomCourseThirty(courseId);

        // 处理结果
        List<TestResultVO> testResults = testResultUtil.getWordTestesForCourse(type, vocabularies.size(), vocabularies, courseId);

        // 设置等级测试开始时间
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        return ServerResponse.createBySuccess(testResults);
    }
}
