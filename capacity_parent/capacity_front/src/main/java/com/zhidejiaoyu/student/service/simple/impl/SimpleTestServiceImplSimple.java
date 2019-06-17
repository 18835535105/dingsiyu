package com.zhidejiaoyu.student.service.simple.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.Vo.simple.testVo.TestDetailVo;
import com.zhidejiaoyu.common.Vo.simple.testVo.TestRecordVo;
import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.award.DailyAwardAsync;
import com.zhidejiaoyu.common.award.GoldAwardAsync;
import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.study.MemoryDifficultyUtil;
import com.zhidejiaoyu.common.study.TestPointUtil;
import com.zhidejiaoyu.common.study.simple.SimpleCommonMethod;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.simple.goldUtil.SimpleTestGoldUtil;
import com.zhidejiaoyu.common.utils.simple.language.SimpleBaiduSpeak;
import com.zhidejiaoyu.common.utils.simple.server.SimpleTestResponseCode;
import com.zhidejiaoyu.common.utils.simple.testUtil.SimpleSentenceTestResult;
import com.zhidejiaoyu.common.utils.simple.testUtil.SimpleTestResult;
import com.zhidejiaoyu.common.utils.simple.testUtil.SimpleTestResultUtil;
import com.zhidejiaoyu.student.common.PerceiveEngine;
import com.zhidejiaoyu.student.common.SaveLearnAndCapacity;
import com.zhidejiaoyu.student.constant.PetImageConstant;
import com.zhidejiaoyu.student.constant.PetMP3Constant;
import com.zhidejiaoyu.student.constant.TestAwardGoldConstant;
import com.zhidejiaoyu.student.dto.WordUnitTestDTO;
import com.zhidejiaoyu.student.service.simple.SimpleTestServiceSimple;
import com.zhidejiaoyu.student.utils.PetSayUtil;
import com.zhidejiaoyu.student.utils.PetUrlUtil;
import com.zhidejiaoyu.student.utils.simple.SimpleCcieUtil;
import com.zhidejiaoyu.student.vo.TestResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
public class SimpleTestServiceImplSimple extends SimpleBaseServiceImpl<SimpleTestRecordMapper, TestRecord> implements SimpleTestServiceSimple {

    private static Logger LOGGER = LoggerFactory.getLogger(SimpleTestServiceImplSimple.class);

    @Value("${ftp.prefix}")
    private String prefix;

    /**
     * 50分
     */
    private static final int FIVE = 50;
    /**
     * 60分
     */
    private static final int SIX = 60;
    /**
     * 80
     */
    private static final int PASS = 80;
    /**
     * 90
     */
    private static final int NINETY_POINT = 90;
    /**
     * 100
     */
    private static final int FULL_MARK = 100;


    @Autowired
    private SimpleSentenceMapper simpleSentenceMapper;

    @Autowired
    private SimpleVocabularyMapper vocabularyMapper;

    @Autowired
    private SimpleStudentMapper simpleStudentMapper;

    @Autowired
    private SimpleTestResultUtil simpleTestResultUtil;

    @Autowired
    private SimpleTestRecordMapper simpleTestRecordMapper;

    @Autowired
    private SimpleRunLogMapper runLogMapper;

    @Autowired
    private SimpleCourseMapper simpleCourseMapper;

    @Autowired
    private SimpleUnitMapper unitMapper;

    @Autowired
    private SimpleStudentUnitMapper simpleStudentUnitMapper;

    @Autowired
    private SimpleCommonMethod simpleCommonMethod;

    @Autowired
    private SaveLearnAndCapacity saveLearnAndCapacity;

    @Autowired
    private PetSayUtil petSayUtil;

    @Autowired
    private SimpleSimpleCapacityMapper simpleSimpleCapacityMapper;

    @Autowired
    private SimpleBaiduSpeak simpleBaiduSpeak;

    @Autowired
    private SimpleLearnMapper learnMapper;

    @Autowired
    private SimpleSimpleStudentUnitMapper simpleSimpleStudentUnitMapper;

    @Autowired
    private SimpleTestRecordInfoMapper simpleTestRecordInfoMapper;

    @Autowired
    private SimpleOpenUnitLogMapper simpleOpenUnitLogMapper;

    @Autowired
    private MemoryDifficultyUtil memoryDifficultyUtil;

    @Autowired
    private SimpleCcieUtil simpleCcieUtil;

    @Autowired
    private SimpleTestGoldUtil simpleTestGoldUtil;

    @Autowired
    private DailyAwardAsync dailyAwardAsync;

    @Autowired
    private GoldAwardAsync goldAwardAsync;

    @Autowired
    private MedalAwardAsync medalAwardAsync;

    @Autowired
    private ExecutorService executorService;

    /**
     * 学前测试/学后测试,从课程取50道题
     *  保存的时候只保存测试记录
     *
     * @param typeModel 1=学前测试 2=学后测试 3=能力值测试
     * @param modelType 1=辨音模块 2=慧记忆模块 3=慧默写模块
     * @param studyParagraph 1=小学, 2=初中, 3=高中 - 只能力值测试有该字段
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> getPreSchoolTest(HttpSession session, Integer typeModel, Long courseId, Integer modelType, String studyParagraph, boolean example) {

        // 1.题类型
        String[] type;
        if((modelType != null && modelType == 2) || typeModel == 3) {
            type = new String[]{"英译汉", "汉译英", "听力理解"};
        }else {
            type = new String[]{"听力理解"};
        }

        // 例句模块测试
        if(example) {
            type = new String[]{"英译汉", "汉译英"};
        }

        // 随机取50道题 / 100道题
        List<Vocabulary> vocabularies = null;
        // 学前测试/学后测试
        if (typeModel != 3) {
            vocabularies = vocabularyMapper.getRandomCourseThirty(courseId);
        } else {
            // 能力值测试
            vocabularies = vocabularyMapper.getStudyParagraphTest(studyParagraph, "单词");
        }

        // 处理结果
        List<SimpleTestResult> testResults = null;
        if (typeModel != 3) {
            testResults = simpleTestResultUtil.getWordTestesForCourse(type, vocabularies.size(), vocabularies, courseId);
        } else {
            // 能力值测试
            testResults = simpleTestResultUtil.getWordTestesForCourse5DTest(type, vocabularies.size(), vocabularies, studyParagraph, "单词");
        }

        // 当测试模块是“慧默写”时，将题目修改为中文
        if (modelType != null && modelType == 3) {
            testResults.parallelStream().forEach(result -> result.getSubject().forEach((key, value) -> {
                if (value) {
                    result.setChinese(key.toString());
                }
            }));
        }

        // 设置测试开始时间
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        return ServerResponse.createBySuccess(testResults);
    }

    /**
     * 游戏测试题目获取，获取20个单词供测试
     *
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Map<String, Object>> getGameSubject(HttpSession session) {
        // 获取当前学生信息
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        // 查询当前学生游戏测试的次数，如果已经测试两次不再允许游戏测试
        TestRecordExample example = new TestRecordExample();
        example.createCriteria().andStudentIdEqualTo(student.getId()).andGenreEqualTo("学前游戏测试");
        List<TestRecord> records = simpleTestRecordMapper.selectByExample(example);
        Integer point = null;
        if (records.size() > 0) {
            TestRecord testRecord = records.get(0);
            String explain = testRecord.getExplain();
            point = testRecord.getPoint();
            // 测试次数
            String time = explain.split("#")[1];
            if ("2".equals(time)) {
                return ServerResponse.createByErrorCodeMessage(SimpleTestResponseCode.GAME_TESTED_SECOND.getCode(),
                        SimpleTestResponseCode.GAME_TESTED_SECOND.getMsg());
            }
        }

        // 设置游戏测试开始时间
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        List<Vocabulary> vocabularies;
        // 根据当前学生所学学段去对应的单词预科库中查找对应的单词和释义
        String grade = student.getGrade();
        String phase = CommonMethod.getPhase(grade);

        // todo: 以下取题以后改为从预科库中获取
        if ("初中".equals(phase)) {
            // 前往初中预科库查询简单单词
            vocabularies = vocabularyMapper.selectByStudentPhase(student, 1);
        } else {
            // 前往高中预科库查询简单单词
            vocabularies = vocabularyMapper.selectByStudentPhase(student, 2);
            if (vocabularies.size() == 0) {
                vocabularies = vocabularyMapper.selectByStudentPhase(student, 3);
            }
        }

        String[] type = {"汉译英"};
        // 游戏测试从取当前学段的预科库中的单词25个,其中有5个未预备单词，即可以直接跳过
        List<SimpleTestResult> testResults = simpleTestResultUtil.getWordTestes(type, 26, vocabularies, student.getVersion(), phase);
        Map<String, Object> map = new HashMap<>(16);
        map.put("testResults", testResults);
        if (point != null) {
            map.put("point", point);
        }
        // 宠物url,用于跳过游戏时显示
        map.put("petUrl", student.getPartUrl());

        return ServerResponse.createBySuccess(map);
    }

    @Override
    @GoldChangeAnnotation
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Map<String, Object>> saveGameTestRecord(HttpSession session, TestRecord testRecord) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);

        // 存放响应的 提示语，推送课程名称，奖励金币数
        Map<String, Object> map = new HashMap<>(16);

        // 游戏测试开始时间
        Date gameStartTime = (Date) session.getAttribute(TimeConstant.BEGIN_START_TIME);
        session.removeAttribute(TimeConstant.BEGIN_START_TIME);

        testRecord.setStudentId(student.getId());
        testRecord.setTestStartTime(gameStartTime);
        testRecord.setTestEndTime(new Date());
        testRecord.setGenre("学前游戏测试");
        testRecord.setQuantity(20);

        // 查看当前学生是否已经有游戏测试记录
        TestRecordExample example = new TestRecordExample();
        example.createCriteria().andStudentIdEqualTo(student.getId()).andGenreEqualTo("学前游戏测试");
        List<TestRecord> records = simpleTestRecordMapper.selectByExample(example);

        // 已经有游戏测试记录进行记录的更新
        if (records.size() > 0) {
            updateGameTestRecord(testRecord, student, map, records);
        } else {
            // 无游戏测试记录，新增记录
            createGameTestRecord(testRecord, student, map);
        }
        session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        getLevel(session);
        return ServerResponse.createBySuccess(map);
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
        if (testRecord.getPoint() >= PASS) {
            msg = "恭喜你旗开得胜，祝未来勇闯天涯！";
            map.put("tip", msg);
            testRecord.setExplain(testRecord.getExplain() + msg);
        } else {
            msg = "失败乃成功他母亲，速速利用第二次机会迎回春天！";
            map.put("tip", msg);
            testRecord.setExplain(testRecord.getExplain() + msg);
            map.put("tip", "游戏还不错吧？下面我们来开始“特色版”的学习吧。");
        }

        // 保存历史最高分和历史最低分
        testRecord.setHistoryBestPoint(testRecord.getPoint());
        testRecord.setHistoryBadPoint(testRecord.getPoint());

        // 奖励
        int goldCount = this.award(student, testRecord);
        map.put("gold", goldCount);
        testRecord.setAwardGold(goldCount);
        int count = simpleTestRecordMapper.insert(testRecord);
        if (count == 0) {
            String errMsg = "id为 " + student.getId() + " 的学生 " + student.getStudentName() + " 游戏测试记录保存失败！";
            LOGGER.error(errMsg);
            RunLog runLog = new RunLog(2, errMsg, new Date());
            runLogMapper.insertSelective(runLog);
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
        if (testRecord.getPoint() >= PASS) {
            msg = "恭喜你旗开得胜，祝未来勇闯天涯！";
            map.put("tip", msg);
            testRecord.setExplain(testRecord.getExplain() + msg);
        } else {
            msg = "虽败犹荣，我们从头来过。";
            map.put("tip", msg);
            testRecord.setExplain(testRecord.getExplain() + msg);

            map.put("tip", "游戏还不错吧？下面我们来开始“特色版”的学习吧。");

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
        int count = simpleTestRecordMapper.updateByPrimaryKeySelective(testRecord);
        if (count == 0) {
            String errMsg = "id为 " + student.getId() + " 的学生 " + student.getStudentName() + " 更新游戏测试记录失败！";
            LOGGER.error(errMsg);
            RunLog runLog = new RunLog(2, errMsg, new Date());
            runLogMapper.insert(runLog);
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
        if (point >= PASS && point < NINETY_POINT) {
            goldCount = 10;
            this.saveLog(student, goldCount, null, "游戏测试");
        } else if (point >= NINETY_POINT && point < FULL_MARK) {
            goldCount = 20;
            this.saveLog(student, goldCount, null, "游戏测试");
        } else if (point == FULL_MARK) {
            goldCount = 30;
            this.saveLog(student, goldCount, null, "游戏测试");
        }
        simpleStudentMapper.updateByPrimaryKeySelective(student);

        return goldCount;
    }

    /**
     * 分单元闯关式学习：当前单元下所有模块都完成单元闯关测试后（不论测试通过与否），开启下个单元
     *
     * @param courseId 课程id
     * @param unitId   单元id（当前单元闯关测试的单元id）
     * @param type     测试类型 0=单词图鉴 1=慧记忆 2=慧听写 3=慧默写 4=例句听力 5=例句翻译
     * @return 解锁单元提示语
     */
    private String unlockNextUnit(Student student, Long courseId, Long unitId, Integer type) {
        // 查看当前单元的所有模块是否都已完成单元闯关测试
        int count = simpleTestRecordMapper.selectByUnitId(student.getId(), unitId, type);

        // 开启下一单元
        return unlockUnit(student, courseId, unitId, type);
    }

    /**
     * 开启下一单元
     *
     * @param student
     * @param courseId
     * @param unitId
     * @param type
     * @return
     */
    String unlockUnit(Student student, Long courseId, Long unitId, Integer type) {
        // 查看当前课程的最大单元和当前单元的index
        Integer maxUnitIndex = unitMapper.selectMaxUnitIndexByCourseId(courseId);
        Integer currentUnitIndex = unitMapper.selectCurrentUnitIndexByUnitId(unitId);
        if (currentUnitIndex < maxUnitIndex) {
            // 查询 当前单元index+1 的单元id
            Integer nextUnitIndex = currentUnitIndex + 1;
            Long wordNextUnitId = unitMapper.selectNextUnitIndexByCourseId(courseId, nextUnitIndex);

            // Unit unit = unitMapper.selectByPrimaryKey(wordNextUnitId);
            // 根据学生id，课程id和下一个单元id开启下个单元
            simpleStudentUnitMapper.updateStatus(student.getId(), courseId, wordNextUnitId, type);
            // 保存到学生当前学习哪个课程,单元
            simpleSimpleStudentUnitMapper.updateCourseIdAndUnitId(courseId, wordNextUnitId, student.getId());

            OpenUnitLog openUnitLog = new OpenUnitLog();
            openUnitLog.setStudentId(student.getId());
            openUnitLog.setCurrentUnitId(unitId);
            openUnitLog.setNextUnitId(wordNextUnitId);
            openUnitLog.setCreateTime(new Date());
            simpleOpenUnitLogMapper.insert(openUnitLog);
            return "1" ;
        }else {
            return "2";
        }
    }

    @Override
    public ServerResponse<List<SimpleTestResult>> getWordUnitTest(HttpSession session, Long unitId,
                                                            Boolean isTrue, int typeModel, boolean example, Integer model) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        student = simpleStudentMapper.selectById(student.getId());
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        String studyModel = null;
        // 根据测试模块选择题型
        if (typeModel == 1) {
            // 单词辨音，词组辩音
            studyModel = "慧听写";
        } else if (typeModel == 2) {
            studyModel = "慧记忆";
        } else if (typeModel == 3) {
            // 单词默写，词组默写
            studyModel = "慧默写";
        }

        // 获取当前单元下的所有单词
        List<Vocabulary> vocabularies = vocabularyMapper.selectByUnitId(unitId);
        Integer subjectNum = vocabularies.size();
        String[] type;
        if ("慧记忆".equals(studyModel)) {
            type = new String[]{"英译汉","汉译英","听力理解"};
        } else {
            type = new String[]{"听力理解"};
        }

        // 例句模块, 快速单词, 快速词组
        if(example || model == 3 || model == 4) {
            type = new String[]{"英译汉", "汉译英"};
        }

        List<SimpleTestResult> results = simpleTestResultUtil.getWordTestesForUnit(type, subjectNum, vocabularies, unitId);
        // 改为听力理解,方便前台使用
        if(!"慧记忆".equals(studyModel)) {
            for (SimpleTestResult testResult : results) {
                testResult.setType("听力理解");
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

    @Override
    @GoldChangeAnnotation
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<TestResultVo> saveWordUnitTest(HttpSession session, WordUnitTestDTO wordUnitTestDTO, String testDetail) {

        Student student = super.getStudent(session);
        if (StringUtils.isEmpty(student.getPetName())) {
            student.setPetName("大明白");
            student.setPartUrl(PetImageConstant.DEFAULT_IMG);
        }

        // 是否是第一次进行当前模块下的单元闯关测试标识
        boolean isFirst = false;

        String[] correctWord = wordUnitTestDTO.getCorrectWord();
        Long[] correctWordId = wordUnitTestDTO.getCorrectWordId();
        String[] errorWord = wordUnitTestDTO.getErrorWord();
        Long[] errorWordId = wordUnitTestDTO.getErrorWordId();
        Long[] unitId = wordUnitTestDTO.getUnitId();
        Long courseId = unitMapper.selectCourseIdByUnitId(unitId[0]);
        Integer classify = wordUnitTestDTO.getClassify();
        String type = simpleCommonMethod.getTestType(wordUnitTestDTO.getClassify());

        Integer point = wordUnitTestDTO.getPoint();

        // 保存测试记录
        // 查看是否已经有该单元当前模块的单元闯关测试记录
        TestRecord testRecord = simpleTestRecordMapper.selectByStudentIdAndUnitId(student.getId(),
                wordUnitTestDTO.getUnitId()[0], "单元闯关测试", type);

        // 只有第一次进行当前单元模块下的单元闯关测试才尝试开启下个单元，避免重复计算当前课程下所有未解锁单元的个数,排除例句默写
        // 只有第一次进行当前单元模块下单元闯关测试才有各项奖励
        String lockMsg = null;
        Long testId;
        int goldCount = 0;
        int addEnergy = 0;
        if (testRecord == null) {

            isFirst = true;

            saveLearnAndCapacity.saveTestAndCapacity(correctWord, errorWord, correctWordId, errorWordId, session, unitId, classify);

            lockMsg = this.unlockNextUnit(student, courseId, unitId[0], classify);

            //判断得分成绩大于80给与2个能量 小于80 给与1个能量
            addEnergy = getEnergy(student, point);

            if (point >= 60) {
                simpleCcieUtil.saveCcieTest(student, 1, 1, 10 + classify, point);
            }

            // 根据不同分数奖励学生金币
            goldCount = this.saveGold(true, wordUnitTestDTO, student, null);
            /*// 保存日志表, 修改学生金币
            goldCount = this.saveLog(student, goldCount, wordUnitTestDTO, "单元闯关测试");*/

            testRecord = this.saveTestRecord(courseId, student, session, wordUnitTestDTO, goldCount);
        }

        TestResultVo vo = new TestResultVo();
        // 获取测试结果页提示语
        String msg = getMsg(student, vo, classify, testRecord, point);
        if (testRecord.getId() == null) {
            simpleTestRecordMapper.insert(testRecord);
        }
        testId = testRecord.getId();

        student.setUnitId(unitId[0]);
        // 保存测试记录详情
        if (testDetail != null && isFirst) {
            this.saveTestDetail(testDetail, testId, classify, student);
        }

        vo.setMsg(msg);
        vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, point, "单元闯关测试"));
        vo.setLockMsg(lockMsg);
        vo.setGold(goldCount);
        vo.setTestId(testId);
        vo.setEnergy(addEnergy);
        simpleStudentMapper.updateByPrimaryKeySelective(student);
        getLevel(session);
        session.removeAttribute("token");

        executorService.execute(() -> saveGoldAward(student, point));

        return ServerResponse.createBySuccess(vo);
    }

    private void saveGoldAward(Student student, Integer point) {
        // 验证学生今日是否完成一个单元
        dailyAwardAsync.todayLearnOneUnit(student);
        // 验证学生今日是否完成10个单元闯关测试
        dailyAwardAsync.todayCompleteTenUnitTest(student);
        // 验证学生单元闯关成功个数
        goldAwardAsync.completeUnitTest(student);
        if (point != null && point == 100) {
            // 学霸崛起勋章计算
            medalAwardAsync.superStudent(student);
        }
        // 最有潜力勋章
        medalAwardAsync.potentialMan(student);
    }

    private String getMsg(Student student, TestResultVo vo, Integer classify, TestRecord testRecord, Integer point) {
        String msg;
        if (classify == 8 || classify == 9) {
            if (point < FIVE) {
                msg = "很遗憾，闯关失败。但是，绊脚石乃是进身之阶。";
                vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.AFTER_UNIT_FIRST_LEVEL));
                vo.setBackMsg(new String[] {"别气馁，已经超越了", TestPointUtil.getPercentage(point), "的同学，继续努力吧！"} );
                testRecord.setPass(2);
            } else if (point < FULL_MARK) {
                msg = "闯关成功。彪悍的人生不需要解释！";
                vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.AFTER_UNIT_SECOND_LEVEL));
                vo.setBackMsg(new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(point), "的同学，再接再励！"});
                testRecord.setPass(1);
            } else {
                msg = "恭喜你刷新了记录。果然是经天纬地之才！";
                vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.AFTER_UNIT_THIRD_LEVEL));
                vo.setBackMsg(new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(point), "的同学，再接再励！"});
                testRecord.setPass(1);
            }
            // 单词辩音, 词组辩音
        } else if (classify == 1 || classify == 2) {
            msg = packageMsg(student, vo, point, SIX, testRecord);
        } else {
            msg = packageMsg(student, vo, point, PASS, testRecord);
        }
        return msg;
    }

    /**
     * 封装话语
     *
     * @param student
     * @param vo
     * @param point
     * @param pass  及格线
     * @param testRecord
     * @return
     */
    private String packageMsg(Student student, TestResultVo vo, Integer point, int pass, TestRecord testRecord) {
        String msg;
        if (point < pass) {
            msg = "很遗憾，闯关失败。但是，绊脚石乃是进身之阶。";
            vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.AFTER_UNIT_FIRST_LEVEL));
            vo.setBackMsg(new String[] {"别气馁，已经超越了", TestPointUtil.getPercentage(point), "的同学，继续努力吧！"} );
            testRecord.setPass(2);
        } else if (point < FULL_MARK) {
            msg = "闯关成功。彪悍的人生不需要解释。";
            vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.AFTER_UNIT_SECOND_LEVEL));
            vo.setBackMsg(new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(point), "的同学，再接再励！"});
            testRecord.setPass(1);
        } else {
            msg = "恭喜你刷新了记录。果然是经天纬地之才！";
            vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.AFTER_UNIT_THIRD_LEVEL));
            vo.setBackMsg(new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(point), "的同学，再接再励！"});
            testRecord.setPass(1);
        }
        return msg;
    }

    private int getEnergy(Student student, Integer point) {
        Integer energy = student.getEnergy();
        int addEnergy = 0;
        if (student.getEnergy() == null) {
            if (point >= 80) {
                student.setEnergy(2);
                addEnergy = 2;
            } else if (point > 20) {
                student.setEnergy(1);
                addEnergy = 1;
            }
        } else {
            if (point >= 80) {
                student.setEnergy(energy + 2);
                addEnergy = 2;
            } else if (point > 20) {
                student.setEnergy(energy + 1);
                addEnergy = 1;
            }
        }
        return addEnergy;
    }

    /**
     * 保存金币变化日志信息
     *
     * @param student
     * @param goldCount       奖励金币数
     * @param wordUnitTestDTO
     * @param model           测试模块
     */
    private int saveLog(Student student, int goldCount, WordUnitTestDTO wordUnitTestDTO, String model) {

        double gold = goldCount;
        if (student.getBonusExpires() != null) {
            if (student.getBonusExpires().getTime() > System.currentTimeMillis()) {
                gold = gold + gold * 0.2;
            }
        }
        student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), gold));
        String msg;
        if (model == null) {
            msg = "id为：" + student.getId() + "的学生在" + simpleCommonMethod.getTestType(wordUnitTestDTO.getClassify())
                    + " 模块下的单元闯关测试中首次闯关成功，获得#" + gold + "#枚金币";
        } else {
            msg = "id为：" + student.getId() + "的学生在" + model + " 模块下，获得#" + gold + "#枚金币";
        }
        RunLog runLog = new RunLog(student.getId(), 4, msg, new Date());
        runLogMapper.insert(runLog);
        LOGGER.info(msg);
        return (int) Math.floor(gold);
    }

    /**
     * 根据测试成绩计算奖励金币数
     *
     * @param isFirst         是否是第一次进行该模块下的单元闯关测试
     * @param wordUnitTestDTO
     * @param student
     * @param testRecord
     * @return 学生应奖励金币数
     */
    private Integer saveGold(boolean isFirst, WordUnitTestDTO wordUnitTestDTO, Student student, TestRecord testRecord) {
        int point = wordUnitTestDTO.getPoint();
        int goldCount = 0;
        if (isFirst) {
            goldCount = getGoldCount(wordUnitTestDTO, student, point, goldCount);
        } else {
            // 查询当前单元测试历史最高分数
            int betterPoint = simpleTestRecordMapper.selectUnitTestMaxPointByStudyModel(student.getId(), wordUnitTestDTO.getUnitId()[0], simpleCommonMethod.getTestType(wordUnitTestDTO.getClassify()));

            // 非首次测试成绩大于或等于80分并且本次测试成绩大于历史最高分，超过历史最高分次数 +1并且金币奖励翻倍
            if (point >= PASS && betterPoint < wordUnitTestDTO.getPoint()) {
                int betterCount = testRecord.getBetterCount() + 1;
                testRecord.setBetterCount(betterCount);
                if (point < FULL_MARK) {
                    goldCount = betterCount * TestAwardGoldConstant.UNIT_TEST_EIGHTY_TO_FULL;
                    this.saveLog(student, goldCount, wordUnitTestDTO, null);
                } else if (point == FULL_MARK) {
                    goldCount = betterCount * TestAwardGoldConstant.UNIT_TEST_FULL;
                    this.saveLog(student, goldCount, wordUnitTestDTO, null);
                }
            } else {
                goldCount = getGoldCount(wordUnitTestDTO, student, point, goldCount);
            }
        }
        return simpleTestGoldUtil.addGold(student, goldCount);
    }

    /**
     * 单元测试通过但没有达到金币翻倍条件，计算金币并保存日志
     *
     * @param wordUnitTestDTO
     * @param student
     * @param point
     * @param goldCount
     * @return
     */
    private int getGoldCount(WordUnitTestDTO wordUnitTestDTO, Student student, int point, int goldCount) {
        if(point >= PASS){
            if (point < FULL_MARK) {
                goldCount = TestAwardGoldConstant.UNIT_TEST_EIGHTY_TO_FULL;
                this.saveLog(student, goldCount, wordUnitTestDTO, null);
            } else if (point == FULL_MARK) {
                goldCount = TestAwardGoldConstant.UNIT_TEST_FULL;
                this.saveLog(student, goldCount, wordUnitTestDTO, null);
            }
        }
        return goldCount;
    }

    /**
     * 保存测试记录,只保留所有测试中成绩最好的一次测试记录
     *
     * @param courseId
     * @param student
     * @param session
     * @param wordUnitTestDTO
     * @param goldCount       奖励的金币数
     * @return  此时 testrecord 没有 id
     */
    private TestRecord saveTestRecord(Long courseId, Student student, HttpSession session, WordUnitTestDTO wordUnitTestDTO, Integer goldCount) {
        // 新生成的测试记录

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
        TestRecord testRecord = new TestRecord();
        // 首次测试大于或等于80分，超过历史最高分次数 +1
        if (point >= PASS) {
            testRecord.setBetterCount(1);
        } else {
            testRecord.setBetterCount(0);
        }
        testRecord.setCourseId(courseId);
        testRecord.setErrorCount(errorWord == null ? 0 : errorWord.length);
        testRecord.setGenre("单元闯关测试");
        testRecord.setPoint(wordUnitTestDTO.getPoint());
        testRecord.setQuantity(quantity);
        testRecord.setRightCount(correctWord == null ? 0 : correctWord.length);
        testRecord.setStudentId(student.getId());
        testRecord.setStudyModel(simpleCommonMethod.getTestType(wordUnitTestDTO.getClassify()));
        testRecord.setTestEndTime(new Date());
        Object startTime = session.getAttribute(TimeConstant.BEGIN_START_TIME);
        testRecord.setTestStartTime(startTime == null ? new Date() : (Date) startTime);
        testRecord.setUnitId(wordUnitTestDTO.getUnitId()[0]);
        testRecord.setAwardGold(goldCount);
        testRecord.setType(1);

        getUnitTestMsg(testRecord, point);
        return testRecord;
    }

    private void getUnitTestMsg(TestRecord testRecord, int point) {
        if (point >= 0 && point < PASS) {
            testRecord.setExplain("很遗憾，闯关失败，再接再厉。");
        } else if (point >= PASS && point < FULL_MARK) {
            testRecord.setExplain("闯关成功，独孤求败！");
        } else if (point == FULL_MARK) {
            testRecord.setExplain("恭喜你刷新了纪录！");
        }
    }

    @Override
    public ServerResponse<Map<String, Object>> getSentenceUnitTest(HttpSession session, Long unitId, String studyModel,
                                                                   Boolean isSure) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        student = simpleStudentMapper.selectById(student.getId());
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        // 获取当前单元下的所有例句
        List<Sentence> sentences = simpleSentenceMapper.selectByUnitId(unitId);
        List<SimpleSentenceTestResult> results = simpleTestResultUtil.getSentenceTestResults(sentences);
        Map<String, Object> result = new HashMap<>(16);
        result.put("subject", results);
        result.put("total", sentences.size());
        result.put("studyModel", studyModel);
        session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        return ServerResponse.createBySuccess(result);
    }

    @Override
    public ServerResponse<Object> showRecord(String course_id, HttpSession session, Integer page, Integer rows) {
        Student student = super.getStudent(session);
        Long studentId = student.getId();

        if(page == null){
            page = 1;
        }
        if(rows != null){
            PageHelper.startPage(page, rows);
        }
        List<TestRecordVo> records = simpleTestRecordMapper.showRecord(studentId);
        PageInfo<TestRecordVo> testRecordPageInfo = new PageInfo<>(records);

        // 每个测试记录下含有测试详情个数，如果没有测试详情，不显示详情按钮
        Map<Long, Map<Long, Long>> testDetailCountMap = null;
        if (records.size() > 0) {
            testDetailCountMap = simpleTestRecordInfoMapper.countByRecordIds(records);
        }

        // 封装后返回
        List<Map<String, Object>> result = new ArrayList<>();

        Long recordId;
        for(TestRecordVo record: records){
            recordId = record.getId();
            Map<String, Object> map = new HashMap<>(16);
            map.put("id", recordId);
            if("能力值测试".equals(record.getStudyModel())) {
                map.put("genre", record.getGenre());
            }else {
                map.put("genre", StringUtils.isEmpty(record.getStudyModel()) ? record.getGenre() : record.getStudyModel() + "-" + record.getGenre());
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
            if (record.getCourseName() != null && record.getCourseName().contains("冲刺版")) {
                String[] split = record.getCourseName().split("-");
                map.put("courseName", split[0] + "-" + split[2] + "-全册）");
            } else {
                map.put("courseName", record.getCourseName());
            }
            map.put("unitName", record.getUnitName());
            String explain = record.getExplain();
            if (StringUtils.isNotEmpty(explain) && explain.contains("#")) {
                map.put("explain", explain.substring(explain.lastIndexOf("#")+1));
            }else {
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
     * 保存学后测试, 单元前测, 能力值测试
     *
     * @param session
     * @param type       1:学前测试 2:学后测试 3:单元前测 4:能力值测试
     * @param modelType  1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写; 20:能力值测试
     * @param testDetail
     * @return
     */
    @Override
    @GoldChangeAnnotation
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<TestResultVo> savePreSchoolTest(HttpSession session, TestRecord testRecord, int type,
                                                          int modelType, String testDetail) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        // 学生需要更新的信息
        if (StringUtils.isEmpty(student.getPetName())) {
            student.setPetName("大明白");
            student.setPartUrl(PetImageConstant.DEFAULT_IMG);
        }

        String typeModel = matchTypeModel(testRecord, type, modelType, student);

        //  1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写;
        String studyModel = matchStudyModel(modelType);

        TestResultVo vo = new TestResultVo();
        if (Objects.equals(typeModel, "单元前测")) {
            TestRecord preUnitTest = simpleTestRecordMapper.selectByStudentIdAndUnitId(student.getId(), testRecord.getUnitId(), "单元前测", studyModel);
            if (preUnitTest != null) {
                // 当前单元已有单元前测测试记录，不再保存奖励和测试记录
                int point = testRecord.getPoint();
                getPreSchoolTestGold(testRecord, modelType, student, typeModel, vo, point);

                vo.setGold(0);
                vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, point, typeModel));
                vo.setTestId(preUnitTest.getId());
                vo.setEnergy(0);
                return ServerResponse.createBySuccess(vo);
            }
        }

        // 游戏测试开始时间
        Object startTime = session.getAttribute(TimeConstant.BEGIN_START_TIME);
        session.removeAttribute(TimeConstant.BEGIN_START_TIME);

        if(StringUtils.isNotEmpty(studyModel)) {
            testRecord.setStudyModel(studyModel);
        }
        int point = testRecord.getPoint();
        int addEnergy = getEnergy(student, point);

        int gold = getPreSchoolTestGold(testRecord, modelType, student, typeModel, vo, point);

        // 奖励
        if (gold > 0) {
            gold = this.saveLog(student, gold, null, typeModel);
        }
        getLevel(session);
        if (Objects.equals(typeModel, "学后测试")) {
            // 查询当前课程下的其中一个单元
            Unit unit = unitMapper.selectFirstUnitByCourseId(testRecord.getCourseId());
            if (unit != null) {
                testRecord.setUnitId(unit.getId());
            }
        }

        // 保存测试记录
        testRecord.setAwardGold(gold);
        testRecord.setStudentId(student.getId());
        testRecord.setTestStartTime(startTime == null ? new Date() : (Date) startTime);
        testRecord.setTestEndTime(new Date());
        testRecord.setGenre(typeModel);
        testRecord.setQuantity(testRecord.getErrorCount() + testRecord.getRightCount());
        testRecord.setType(1);
        simpleTestRecordMapper.insert(testRecord);

        // 保存测试记录详情
        if (testDetail != null) {
            this.saveTestDetail(testDetail, testRecord.getId(), modelType, student);
        }

        vo.setGold(gold);
        vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, point, typeModel));
        vo.setTestId(testRecord.getId());
        vo.setEnergy(addEnergy);
        simpleStudentMapper.updateByPrimaryKeySelective(student);
        session.setAttribute(UserConstant.CURRENT_STUDENT, student);

        // 学霸崛起勋章计算
        if (point == 100) {
            medalAwardAsync.superStudent(student);
        }
        // 最有潜力勋章
        medalAwardAsync.potentialMan(student);
        return ServerResponse.createBySuccess(vo);
    }

    private int getPreSchoolTestGold(TestRecord testRecord, int modelType, Student student, String typeModel, TestResultVo vo, int point) {
        int gold = 0;
        if("学后测试".equals(typeModel)) {
            if (point < 80) {
                vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.COURSE_TEST_LESS_EIGHTY));
                testRecord.setExplain("你和优秀的人差的不是智商，是努力。");
                vo.setMsg("你和优秀的人差的不是智商，是努力。");
                vo.setBackMsg(new String[] {"别气馁，已经超越了", TestPointUtil.getPercentage(point), "的同学，继续努力吧！"} );
                testRecord.setPass(2);
            } else {
                gold = 5;
                vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.COURSE_TEST_EIGHTY_TO_HUNDRED));
                testRecord.setExplain("谁都不能阻止你成为优秀的人！");
                vo.setMsg("谁都不能阻止你成为优秀的人！");
                vo.setBackMsg(new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(point), "的同学，再接再励！"});
                testRecord.setPass(1);
            }
        }

        if("单元前测".equals(typeModel)) {
            // 单词默写; 词组默写
            if (modelType == 8 || modelType == 9) {
                if (point < 50) {
                    packageUnPassTestRecordVo(testRecord, student, vo, point);
                } else {
                    gold = getGold(testRecord, student, vo, point);
                    vo.setBackMsg(new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(point), "的同学，再接再励！"});
                    testRecord.setPass(1);
                }
            } else if (modelType == 1 || modelType == 2) {
                // 单词辨音; 词组辨音
                if (point < 60) {
                    packageUnPassTestRecordVo(testRecord, student, vo, point);
                } else {
                    gold = getGold(testRecord, student, vo, point);
                    vo.setBackMsg(new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(point), "的同学，再接再励！"});
                    testRecord.setPass(1);
                }
            } else {
                if (point < 80) {
                    packageUnPassTestRecordVo(testRecord, student, vo, point);
                } else {
                    gold = getGold(testRecord, student, vo, point);
                    vo.setBackMsg(new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(point), "的同学，再接再励！"});
                    testRecord.setPass(1);
                }
            }
        }

        if("能力值测试".equals(typeModel)) {
            if (point < 80) {
                testRecord.setExplain("学海攀崖，尽力而为，曙光必见。");
                vo.setMsg("学海攀崖，尽力而为，曙光必见。");
                vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.WORD_TEST_LESS_EIGHTY));
                vo.setBackMsg(new String[] {"别气馁，已经超越了", TestPointUtil.getPercentage(point), "的同学，继续努力吧！"} );
                testRecord.setPass(2);
            } else if (point < 90) {
                gold = 5;
                packagePassTestRecordVo(testRecord, student, vo, point);
            } else {
                gold = 10;
                packagePassTestRecordVo(testRecord, student, vo, point);
            }
        }
        return simpleTestGoldUtil.addGold(student, gold);
    }

    private void packageUnPassTestRecordVo(TestRecord testRecord, Student student, TestResultVo vo, int point) {
        testRecord.setExplain("测试失败，有潜力的人总是厚积薄发。");
        vo.setMsg("测试失败，有潜力的人总是厚积薄发。");
        vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.BEFORE_UNIT_FIRST_LEVEL));
        vo.setBackMsg(new String[] {"别气馁，已经超越了", TestPointUtil.getPercentage(point), "的同学，继续努力吧！"} );
        testRecord.setPass(2);
    }

    private void packagePassTestRecordVo(TestRecord testRecord, Student student, TestResultVo vo, int point) {
        testRecord.setExplain("这成绩，可真是绝顶到家了！");
        vo.setMsg("这成绩，可真是绝顶到家了！");
        vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.WORD_TEST_EIGHTY_TO_HUNDRED));
        vo.setBackMsg(new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(point), "的同学，再接再励！"});
        testRecord.setPass(1);
    }

    private int getGold(TestRecord testRecord, Student student, TestResultVo vo, int point) {
        int gold;
        if (point <= 90) {
            gold = 3;
            testRecord.setExplain("有品位的人，才能配得上这样的成绩！");
            vo.setMsg("有品位的人，才能配得上这样的成绩！");
            vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.BEFORE_UNIT_SECOND_LEVEL));
        } else {
            gold = 5;
            testRecord.setExplain("祖国的栋梁之才，你可来了！");
            vo.setMsg("祖国的栋梁之才，你可来了！");
            vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.BEFORE_UNIT_THIRD_LEVEL));
        }
        return gold;
    }

    private String matchStudyModel(int modelType) {
        String studyModel = null;
        if (modelType == 1) {
            studyModel = "单词辨音";
        } else if (modelType == 2) {
            studyModel = "词组辨音";
        } else if (modelType == 3) {
            studyModel = "快速单词";
        } else if (modelType == 4) {
            studyModel = "快速词组";
        } else if (modelType == 6) {
            studyModel = "快速句型";
        } else if (modelType == 7) {
            studyModel = "语法辨析";
        }else if(modelType == 8) {
            studyModel = "单词默写";
        } else if (modelType == 9) {
            studyModel = "词组默写";
        } else if (modelType == 20) {
            // 能力值测试
            studyModel = null;
        } else {
            ServerResponse.createByErrorMessage("该模块不存在测试题， 无法保存！");
        }
        return studyModel;
    }

    /**
     * 匹配测试类型 数字和汉字
     *
     * @param testRecord
     * @param type
     * @param modelType
     * @param student
     * @return
     */
    private String matchTypeModel(TestRecord testRecord, int type, int modelType, Student student) {
        String typeModel = null;
        Integer point = testRecord.getPoint();
        if (type == 2) {
            typeModel = "学后测试";
            if (point >= 90) {
                simpleCcieUtil.saveCcieTest(student, 8, 1, 10 + modelType, point);
            }
        } else if (type == 3) {
            typeModel = "单元前测";
            if (point >= 60) {
                simpleCcieUtil.saveCcieTest(student, 9, 1, 10 + modelType, point);
            }
        } else if (type == 4) {
            typeModel = "能力值测试";
            simpleCcieUtil.saveCcieTest(student, 10, 1, -1, point);
            testRecord.setStudyModel(typeModel);
        }
        return typeModel;
    }

    /**
     * @param testDetail
     * @param testRecordId
     * @param modelType    1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写; 20:能力值测试
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
            try {
                for (Object aJsonArray : jsonArray) {
                    testRecordInfo = new TestRecordInfo();

                    object = (JSONObject) aJsonArray;
                    String word = object.getString("title");
                    final String[] selected = {null};
                    if (modelType != 8 && modelType != 9) {
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
                                selected[0] = this.matchSelected(finalObject.getJSONObject("userInput").getInteger("optionIndex"));
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
            } catch (Exception e) {
                log.error("json解析失败，testDetail=[{}], modelType=[{}]", testDetail, modelType);
                return;
            }
            if (testRecordInfos.size() > 0) {
                try {
                    simpleTestRecordInfoMapper.insertList(testRecordInfos);
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
    private String matchSelected(Integer integer) {
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
     * 生成试卷
     *
     * @param courseId 课程id
     * @param typeOne 取题范围: 1=全部 2=追词纪
     * @param typeTwo 取题范围: 1=较少(20) 2=普通(40) 3=较多(100)
     * @param unitId 单元id null=全部
     * @return
     */
    @Override
    public ServerResponse<Object> getTestPaper(long courseId, int typeOne, int typeTwo, String[] unitId) {
        // 根据规则取打乱顺序题,  需要返回类型:id,word, wordChinese
        List<Vocabulary> list;

        if (typeTwo == 1) {
            typeTwo = 20;
        } else if (typeTwo == 2) {
            typeTwo = 40;
        } else {
            typeTwo = 100;
        }

        if (unitId.length < 1) {
            unitId = null;
        }

        if (typeOne == 1) {
            // 单词库下的题
            list = vocabularyMapper.getTestPaperGenerationAll(courseId, typeTwo, unitId);
        } else {
            // 记忆追踪下的题
            list = simpleSimpleCapacityMapper.getTestPaperGenerationAll(courseId, typeTwo, unitId);
        }

        // 调用封装题
        String[] type = {"英译汉"};
        List<SimpleTestResult> wordTestesForCourse = simpleTestResultUtil.getWordTestesForCourse(type, list.size(), list, courseId);

        return ServerResponse.createBySuccess(wordTestesForCourse);
    }

    /**
     * 根据课程查询学生需要复习的数据
     *
     * @param session
     * @param courseId
     * @param type      1-9测试类型
     * @return 一道题
     */
    @Override
    public ServerResponse<Object> getTaskCourseTest(HttpSession session, Long courseId, int type) {
        Student student = getStudent(session);
        Long studentId = student.getId();
        // 获取一道需要复习的单词
        Map<String, Object> vocabulary = simpleSimpleCapacityMapper.getWordLimitOneByStudentIdByCourseId(studentId, courseId, new Date());

        if (vocabulary == null) {
            return ServerResponse.createBySuccess();
        }

        if (vocabulary.get("recordpicurl") != null && vocabulary.get("recordpicurl") != "") {
            vocabulary.put("recordpicurl", prefix + vocabulary.get("recordpicurl"));
        } else {
            vocabulary.put("recordpicurl", "");
        }

        String word = vocabulary.get("word").toString();

        // 读音
        if (type == 1 || type == 2 || type == 3 || type == 4 || type==8 || type == 9) {
            vocabulary.put("readUrl", simpleBaiduSpeak.getLanguagePath(word));
        }

        // 该课程已学单词
        Long plan = learnMapper.learnCourseCountWord(studentId, courseId.toString(), typeToModelStr(type));
        vocabulary.put("plan", plan);
        // 该课程一共多少单词
        Integer count = unitMapper.countWordByCourse(courseId.toString());
        vocabulary.put("wordCount", count);
        vocabulary.put("studyNew", false);

        // 记忆难度
        SimpleCapacity simpleCapacity = new SimpleCapacity();
        simpleCapacity.setStudentId(studentId);
        simpleCapacity.setVocabularyId(Long.valueOf(vocabulary.get("id").toString()));
        simpleCapacity.setUnitId(Long.valueOf(vocabulary.get("unit_id").toString()));
        simpleCapacity.setFaultTime(Integer.parseInt(vocabulary.get("fault_time").toString()));
        simpleCapacity.setMemoryStrength(Double.valueOf(vocabulary.get("memory_strength").toString()));
        int hard = memoryDifficultyUtil.getMemoryDifficulty(simpleCapacity, type);
        vocabulary.put("memoryDifficulty", hard);
        vocabulary.put("engine", PerceiveEngine.getPerceiveEngine(hard, simpleCapacity.getMemoryStrength()));

        return ServerResponse.createBySuccess(vocabulary);
    }


    /**
     * 匹配类型
     *
     * @param type
     * @return
      */
    private String typeToModelStr(int type) {
        if (type == 1) {
            return "单词辨音";
        } else if (type == 2) {
            return "词组辨音";
        } else if (type == 3) {
            return "快速单词";
        } else if (type == 4) {
            return "快速词组";
        } else if (type == 5) {
            return "词汇考点";
        } else if (type == 6) {
            return "快速句型";
        } else if (type == 7) {
            return "语法辨析";
        } else if (type == 8) {
            return "单词默写";
        } else if (type == 9) {
            return "词组默写";
        } else {
            return null;
        }
    }

    /**
     * 任务课程保存学习记录 - 精简版
     */
    @Override
    public ServerResponse<Object> postTaskCourseTest(Learn learn, boolean isKnown, Integer type, HttpSession session) {
        saveLearnAndCapacity.saveLearnCapacity(session, learn, isKnown, type);

        return ServerResponse.createBySuccess("ok");
    }

    @Override
    public ServerResponse<TestDetailVo> getTestDetail(HttpSession session, Long testId) {
        Student student = getStudent(session);
        TestDetailVo testDetailVo = simpleTestRecordMapper.selectTestDetailVo(student.getId(), testId);
        testDetailVo.setUseTime(getUseTime(testDetailVo.getUseTime()));
        if (StringUtils.isNotEmpty(testDetailVo.getIsWrite())) {
            if (testDetailVo.getIsWrite().contains("默写")) {
                testDetailVo.setIsWrite("1");
            } else {
                testDetailVo.setIsWrite("0");
            }
        } else {
            testDetailVo.setIsWrite(null);
        }
        testDetailVo.setInfos(simpleTestRecordMapper.selectTestRecordInfo(testId));
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
            int intUseTime = Integer.valueOf(useTime);
            return "用时：" + (intUseTime / 60) + " 分 " + (intUseTime % 60) + "秒";
        }
        return "用时：0 分 0 秒";
    }
}