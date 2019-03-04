package com.zhidejiaoyu.student.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.Vo.game.StrengthGameVo;
import com.zhidejiaoyu.common.Vo.student.SentenceTranslateVo;
import com.zhidejiaoyu.common.Vo.testVo.TestDetailVo;
import com.zhidejiaoyu.common.Vo.testVo.TestRecordVo;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.math.MathUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.server.TestResponseCode;
import com.zhidejiaoyu.common.utils.testUtil.TestResult;
import com.zhidejiaoyu.common.utils.testUtil.TestResultUtil;
import com.zhidejiaoyu.common.utils.testUtil.TestSentenceUtil;
import com.zhidejiaoyu.student.common.RedisOpt;
import com.zhidejiaoyu.student.common.SaveTestLearnAndCapacity;
import com.zhidejiaoyu.student.constant.PetImageConstant;
import com.zhidejiaoyu.student.constant.PetMP3Constant;
import com.zhidejiaoyu.student.constant.TestAwardGoldConstant;
import com.zhidejiaoyu.student.dto.WordUnitTestDTO;
import com.zhidejiaoyu.student.service.TestService;
import com.zhidejiaoyu.student.utils.CcieUtil;
import com.zhidejiaoyu.student.utils.CountMyGoldUtil;
import com.zhidejiaoyu.student.utils.PetSayUtil;
import com.zhidejiaoyu.student.utils.PetUrlUtil;
import com.zhidejiaoyu.student.vo.TestResultVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class TestServiceImpl extends BaseServiceImpl<TestRecordMapper, TestRecord> implements TestService {

    private static Logger LOGGER = LoggerFactory.getLogger(TestServiceImpl.class);
    /**
     * 50分
     */
    private static final int FIVE = 50;
    /**
     * 60分
     */
    private static final int SIX = 60;
    /**
     * 70分
     */
    private static final int SEVENTY = 70;
    /**
     * 80分
     */
    private static final int PASS = 80;
    /**
     * 90分
     */
    private static final int NINETY_POINT = 90;
    /**
     * 100分
     */
    private static final int FULL_MARK = 100;


    @Autowired
    private SentenceMapper sentenceMapper;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TestResultUtil testResultUtil;

    @Autowired
    private TestRecordMapper testRecordMapper;

    @Autowired
    private RunLogMapper runLogMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private CountMyGoldUtil countMyGoldUtil;

    @Autowired
    private CommonMethod commonMethod;

    @Autowired
    private SaveTestLearnAndCapacity saveTestLearnAndCapacity;

    @Autowired
    private PetSayUtil petSayUtil;

    @Autowired
    private CcieUtil ccieUtil;

    @Autowired
    private CapacityStudentUnitMapper capacityStudentUnitMapper;

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
        CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selectCurrentUnitIdByStudentIdAndType(student.getId(), 1);
        List<Vocabulary> vocabularies;
        Long courseId;
        PageHelper.startPage(1, 100);
        if (capacityStudentUnit != null) {
            courseId = capacityStudentUnit.getCourseId();
            vocabularies = vocabularyMapper.selectByCourseId(courseId);
        } else {
            courseId = 2751L;
            vocabularies = vocabularyMapper.selectByCourseId(2751L);
        }
        Collections.shuffle(vocabularies);

        String[] type = {"汉译英"};
        // 游戏测试从取当前学段的预科库中的单词25个,其中有5个未预备单词，即可以直接跳过
        List<TestResult> testResults = testResultUtil.getWordTestesForCourse(type, 26, vocabularies, courseId);
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
            if (studentFlow == null) {
                countMyGoldUtil.countMyGold(student);
            }
        }

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
    public ServerResponse<Map<String, Object>> getStrengthGame(HttpSession session) {

        Student student = getStudent(session);
        Long studentId = student.getId();

        // 判断学生是否已经进行过游戏测试，如果进行过不允许再次进行；否则返回游戏题目
        int count = testRecordMapper.countGameCount(student);
        if (count > 0) {
            return ServerResponse.createBySuccess(ResponseCode.FORBIDDEN.getCode(), ResponseCode.FORBIDDEN.getMsg());
        }

        CapacityStudentUnit capacityStudentUnit = capacityStudentUnitMapper.selectCurrentUnitIdByStudentIdAndType(studentId, 1);
        // 随机选出20个正确单词信息
        PageHelper.startPage(1, 20);
        List<Vocabulary> rightVocabularies = vocabularyMapper.selectByStartUnitIdAndEndUnitId(capacityStudentUnit.getStartunit(), capacityStudentUnit.getEndunit());
        Map<String, String> map = this.getWordMap(rightVocabularies);

        int size = rightVocabularies.size();
        int errorSize = size * 3;
        long currentCourseId = capacityStudentUnit.getCourseId();
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
        ServerResponse<Map<String, Object>> resultMap = packageStrengthVo(student, rightVocabularies, map, size,
                errorSize, errorVocabularies);
        if (resultMap != null) {
            return resultMap;
        }
        return null;
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

    private ServerResponse<Map<String, Object>> packageStrengthVo(Student student, List<Vocabulary> rightVocabularies, Map<String, String> map, int size, int errorSize, List<Vocabulary> errorVocabularies) {
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
            return ServerResponse.createBySuccess(resultMap);
        }
        return null;
    }

    /**
     * 游戏结束初始化学习流程
     *  @param student
     * @param point
     * @param studentFlow
     */
    private void initStudentFlow(Student student, Integer point, StudentFlow studentFlow) {
        if (studentFlow == null) {
            studentFlow = new StudentFlow();
            studentFlow.setStudentId(student.getId());
            studentFlow.setTimeMachine(0);
            studentFlow.setPresentFlow(1);
            if (point >= PASS) {
                // 流程2
                studentFlow.setCurrentFlowId(24L);
            } else {
                // 流程1
                studentFlow.setCurrentFlowId(11L);
            }
            studentFlowMapper.insert(studentFlow);
        } else {
            if (point >= PASS) {
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
        if (testRecord.getPoint() >= PASS) {
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
        studentMapper.updateByPrimaryKeySelective(student);
        return goldCount;
    }

    /**
     * <p>
     * 游戏测试： 时根据学生最高分来判断学生下一步是进入“预科班课程”学习还是进行“等级测试”
     * 如游戏最高分低于NINETY_POINT分，系统强制推送本学生所在学段‘预科班课程’；如游戏最高分高于NINETY_POINT分，进入‘等级测试’
     * </p>
     * 等级测试：
     * </p>
     * 当测试成绩在80-NINETY_POINT分，系统强制学习‘七年级课程’，可在‘我的课程’中查看七年级课程并学习。成绩反馈结果提示为‘本课程就是为你量身而做，快去学习吧。’。（初中高中各奖励1个金币）
     * <p>
     * 当成绩>=NINETY_POINT分，系统强制学习七年级课程，可在‘我的课程’中查看七年级课程并学习。成绩反馈结果提示为‘哎呦~，不错哦，推荐你做一下’‘五维测试’。（小学初中高中各奖励1个金币）
     * </p>
     *
     * @param point   游戏测试最高分数
     * @param student 学生信息
     * @param genre   测试类型 学前摸底测试，学前游戏测试
     */
    private String pushCourse(Integer point, Student student, String genre) {
        String phase = commonMethod.getPhase(student.getGrade());
        if ("学前游戏测试".equals(genre)) {
            if (point < PASS) {
                // 推送当前学段的 预科班教程
                this.initReadyCourse(student);
                // 初始化学生的课程、单元信息
                commonMethod.initUnit(student);
                return "高中".equals(phase) ? "根据游戏成绩反馈，系统已推送‘高中预科库’可供您学习，快去学习吧"
                        : "根据游戏成绩反馈，系统已推送‘初中预科库’可供您学习，快去学习吧";
            }
        } else if ("学前摸底测试".contentEquals(genre)) {
            if (point < PASS) {
                // 推送当前学段的 预科班教程
                this.initReadyCourse(student);
                // 初始化学生的课程、单元信息
                commonMethod.initUnit(student);
                return "根据你的情况，下面我们来开始学习吧。";
            } else if (point < NINETY_POINT) {
                // 根据学段推送低年级课程，初中推送七年级课程，高中推送高一课程
                this.initLowerCourse(student);
                return "本课程就是为你量身而做，快去学习吧！";
            } else {
                // 根据学段推送低年级课程，初中推送七年级课程，高中推送高一课程
                this.initLowerCourse(student);
                // 初始化学生的课程、单元信息
                commonMethod.initUnit(student);
                return "不要让小事遮住视线，我们还有更大的世界！";
            }
        }
        return null;
    }

    /**
     * 根据学段推送低年级课程，初中推送七年级课程，高中推送高一课程
     *
     * @param student
     */
    private void initLowerCourse(Student student) {
        String phase = commonMethod.getPhase(student.getGrade());
        String version = student.getVersion();
        List<Course> courses;
        Course course;
        CourseExample courseExample = new CourseExample();
        String errMsg = "";
        if ("初中".equals(phase)) {
            courseExample.createCriteria().andVersionEqualTo(version).andGradeEqualTo("七年级").andLabelEqualTo("上册");
            errMsg = "没有找到 " + version + "-七年级-上册 的课程信息！";
        } else if ("高中".equals(phase)) {
            courseExample.createCriteria().andVersionEqualTo(version).andGradeEqualTo("高中").andLabelEqualTo("必修一");
            errMsg = "没有找到 " + version + "-高中-必修一 的课程信息！";
        }
        courses = courseMapper.selectByExample(courseExample);
        if (courses.size() > 0) {
            course = courses.get(0);

            UnitOneExample unitOneExample = new UnitOneExample();
            unitOneExample.setOrderByClause("id asc");
            unitOneExample.createCriteria().andCourseIdEqualTo(course.getId());
            List<Unit> units = unitMapper.selectByExample(unitOneExample);

            student.setCourseId(course.getId());
            student.setCourseName(course.getCourseName());

            if (units.size() > 0) {
                Unit unit = units.get(0);
                student.setUnitId(unit.getId());
                student.setUnitName(unit.getUnitName());

                studentMapper.updateByPrimaryKeySelective(student);

            } else {
                LOGGER.error("id为 {} 的课程下没有找到对应的单元信息！", course.getId());
            }
        } else {
            LOGGER.error(errMsg);
        }
    }

    /**
     * 当游戏测试或者等级测试未通过时，为学生推送当前学段下的预科班课程
     *
     * @param student
     */
    private void initReadyCourse(Student student) {
        // TODO:推送预科班课程
        String phase = commonMethod.getPhase(student.getGrade());
    }

    @Override
    public ServerResponse<Map<String, Object>> getLevelTest(HttpSession session) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);

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
        String phase = commonMethod.getPhase(grade);
        String courseName;

        // todo:以下取题以后改为从预科库中获取
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
        List<TestResult> results = new ArrayList<>();
        List<TestResult> results1 = new ArrayList<>();
        List<TestResult> results2 = new ArrayList<>();

        String[] type = {"英译汉"};
        String[] type1 = {"汉译英"};
        String[] type2 = {"听力理解"};

        try {
            englishToChinese = vocabularies.subList(0, 10);
            results = testResultUtil.getWordTestes(type, englishToChinese.size(), englishToChinese, student.getVersion(), phase);
        } catch (Exception e) {
            LOGGER.error("摸底测试单词不足10个！", e.getMessage());
        }
        try {
            chineseToEnglish = vocabularies.subList(10, 20);
            results1 = testResultUtil.getWordTestes(type1, chineseToEnglish.size(), chineseToEnglish, student.getVersion(), phase);
        } catch (Exception e) {
            LOGGER.error("摸底测试单词不足20个！", e.getMessage());
        }
        try {
            listen = vocabularies.subList(20, 30);
            results2 = testResultUtil.getWordTestes(type2, listen.size(), listen, student.getVersion(), phase);
        } catch (Exception e) {
            LOGGER.error("摸底测试单词不足30个！", e.getMessage());
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
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<TestResultVo> saveLevelTest(HttpSession session, TestRecord testRecord) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        TestResultVo vo = new TestResultVo();
        // 游戏测试开始时间
        Date gameStartTime = (Date) session.getAttribute(TimeConstant.BEGIN_START_TIME);
        session.removeAttribute(TimeConstant.BEGIN_START_TIME);

        testRecord.setStudentId(student.getId());
        testRecord.setTestStartTime(gameStartTime);
        testRecord.setTestEndTime(new Date());
        testRecord.setGenre("学前摸底测试");
        testRecord.setQuantity(testRecord.getErrorCount() + testRecord.getRightCount());

        int gold = 0;
        int point = testRecord.getPoint();
        if (point < PASS) {
            vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, point, "摸底测试"));
            vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.LEVEL_TEST_LESS_EIGHTY));
            testRecord.setExplain("根据你的情况，下、面我们来开始学习吧。");
        } else if (point < NINETY_POINT) {
            gold = 10;
            vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, point, "摸底测试"));
            vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.LEVEL_TEST_EIGHTY_TO_NINETY));
            testRecord.setExplain("本课程就是为你量身而做，快去学习吧！");
        } else {
            gold = 30;
            vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, point, "摸底测试"));
            vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.LEVEL_TEST_GREATER_NINETY));
            testRecord.setExplain("不要让小事遮住视线，我们还有更大的世界！");
        }
        testRecord.setAwardGold(gold);

        // 奖励
        if (gold > 0) {
            this.saveLog(student, gold, null, "摸底测试");
            session.setAttribute(UserConstant.CURRENT_STUDENT, student);
            studentMapper.updateByPrimaryKeySelective(student);
        }

        // 保存摸底测试记录
        int count = testRecordMapper.insertSelective(testRecord);
        if (count > 0) {
            // 根据学生成绩判断学生下一步操作
            String courseName = this.pushCourse(point, student, "学前摸底测试");
            vo.setGold(gold);
            vo.setMsg(courseName);
            studentMapper.updateByPrimaryKeySelective(student);
            return ServerResponse.createBySuccess(vo);
        } else {
            String errMsg = "id为 " + student.getId() + " 的学生 " + student.getStudentName() + " 摸底测试记录保存失败！";
            LOGGER.error(errMsg);
            RunLog runLog = new RunLog(2, errMsg, new Date());
            runLogMapper.insert(runLog);
            return ServerResponse.createByErrorMessage("摸底测试记录保存失败！");
        }

    }

    @Override
    public ServerResponse<List<TestResult>> getWordUnitTest(HttpSession session, Long unitId, String studyModel,
                                                            Boolean isTrue) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        student = studentMapper.selectByPrimaryKey(student.getId());
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        // 判断学生当前单元有无进行单元闯关测试记录，如果已参加过单元闯关测试，提示其需要花费金币购买测试机会，如果还没有测试记录可以免费进行测试
        // 流程中重复进行单元闯关测试不再扣除金币
        /*int flag = this.isFirstTest(student, unitId, studyModel, isTrue);
        if (flag == 1) {
            return ServerResponse.createBySuccess(GoldResponseCode.NEED_REDUCE_GOLD.getCode(), "您已参加过该单元闯关测试，再次参加需扣除1金币。");
        } else if (flag == 2) {
            return ServerResponse.createByError(GoldResponseCode.LESS_GOLD.getCode(), "金币不足");
        }*/
        // 获取当前单元下的所有单词
        List<Vocabulary> vocabularies = redisOpt.getWordInfoInUnit(unitId);
        String[] type;
        if ("慧记忆".equals(studyModel)) {
            type = new String[]{"英译汉","汉译英"};
        } else {
            type = new String[]{"听力理解"};
        }

        // 需要封装的测试题个数
        int subjectNum;
        if (vocabularies.size() < 20) {
            subjectNum = vocabularies.size();
        } else {
            subjectNum = 20;
        }

        List<TestResult> results = testResultUtil.getWordTestesForUnit(type, subjectNum, vocabularies, unitId);
        if (!"慧记忆".equals(studyModel)) {
            for (TestResult testResult : results) {
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
     * 判断学生当前单元有无进行单元闯关测试记录，如果已参加过单元闯关测试，提示其需要花费金币购买测试机会，如果还没有测试记录可以免费进行测试
     *
     * @param student
     * @param unitId
     * @param studyModel
     * @param isTrue
     * @return 1:提示用户需要支付金币购买测试机会；2：金币不足
     */
    private int isFirstTest(Student student, Long unitId, String studyModel, Boolean isTrue) {
        TestRecord testRecord = testRecordMapper.selectByStudentIdAndUnitId(student.getId(), unitId, "单元闯关测试",
                studyModel);
        if (testRecord != null) {
            if (!isTrue) {
                return 1;
            } else {
                // 金币不足
                if (student.getSystemGold() == 0) {
                    return 2;
                }
                student.setSystemGold(BigDecimalUtil.sub(student.getSystemGold(), 1));
                student.setOfflineGold(BigDecimalUtil.add(student.getOfflineGold(), 1));
                studentMapper.updateByPrimaryKeySelective(student);
                String msg = "id为：" + student.getId() + " 的学生花费 1 金币进行" + studyModel + " 模块下的单元闯关测试。";
                RunLog runLog = new RunLog(student.getId(), 5, msg, new Date());
                runLogMapper.insert(runLog);
                LOGGER.info(msg);
            }
        }
        return 0;
    }



    /**
     * 获取例句测试
     * @param unitId
     * @return
     */
    @Override
    public ServerResponse<Object> gitUnitSentenceTest(Long unitId) {
        //获取单元句子
        List<Sentence> sentences = sentenceMapper.selectByUnitId(unitId);
        List<Sentence> sentenceList=null;
        //获取干扰项句子 在当前课程下选择
        if(sentences.size()<4){
            //获取测试单元所在的课程
            Long courseId = unitMapper.getCourseIdByunitId(unitId.intValue());
            sentenceList = sentenceMapper.selectRoundSentence(courseId);
        }
        List<Object> list = testSentenceUtil.resultTestSentence(sentences, sentenceList);
        return ServerResponse.createBySuccess(list);
    }

    @Override
    public ServerResponse<Object> saveCapSentenceTest(HttpSession session, WordUnitTestDTO wordUnitTestDTO) {
        Student student = getStudent(session);
        TestRecord testRecord;

        wordUnitTestDTO.setClassify(8);

        // 判断当前单元是不是首次进行测试
        boolean isFirst = false;
        TestRecord testRecordOld = testRecordMapper.selectByStudentIdAndUnitId(student.getId(),
                wordUnitTestDTO.getUnitId()[0], "音译测试", "音译测试");
        if (testRecordOld == null) {
            isFirst = true;
        }

        int goldCount = this.saveGold(isFirst, wordUnitTestDTO, student, testRecordOld);
        if (testRecordOld == null) {
            testRecord = new TestRecord();
            // 首次测试大于或等于80分，超过历史最高分次数 +1
            if (wordUnitTestDTO.getPoint() >= PASS) {
                testRecord.setBetterCount(1);
            } else {
                testRecord.setBetterCount(0);
            }
        } else {
            testRecord = new TestRecord();
            testRecord.setBetterCount(testRecordOld.getBetterCount());
        }
        testRecord.setCourseId(wordUnitTestDTO.getCourseId());
        testRecord.setUnitId(wordUnitTestDTO.getUnitId()[0]);
        testRecord.setPoint(wordUnitTestDTO.getPoint());
        testRecord.setErrorCount(wordUnitTestDTO.getErrorCount());
        testRecord.setRightCount(wordUnitTestDTO.getRightCount());
        testRecord.setAwardGold(goldCount);
        testRecord.setGenre("音译测试");
        testRecord.setStudentId(student.getId());
        testRecord.setTestEndTime(new Date());
        testRecord.setTestStartTime((Date) session.getAttribute(TimeConstant.BEGIN_START_TIME));
        if(wordUnitTestDTO.getErrorCount()!=null&& wordUnitTestDTO.getRightCount()!=null){
            testRecord.setQuantity(wordUnitTestDTO.getErrorCount() + wordUnitTestDTO.getRightCount());
        }
        testRecord.setAwardGold(goldCount);
        testRecord.setStudyModel("音译测试");
        testRecordMapper.insert(testRecord);
        studentMapper.updateByPrimaryKeySelective(student);
        session.removeAttribute(TimeConstant.BEGIN_START_TIME);
        Map<String,Object> resultMap=new HashMap<>();
        int energy = getEnergy(student, wordUnitTestDTO.getPoint());
        studentMapper.updateByPrimaryKeySelective(student);
        resultMap.put("energy",energy);
        resultMap.put("gold",goldCount);
        Integer point = wordUnitTestDTO.getPoint();
        if (point < PASS) {
            resultMap.put("petName",petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_LESS_EIGHTY));
            resultMap.put("text","很遗憾，闯关失败，再接再厉。");
        } else if (point < NINETY_POINT) {
            resultMap.put("petName",petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_EIGHTY_TO_HUNDRED));
            resultMap.put("text","闯关成功，独孤求败！");
        } else {
            resultMap.put("petName",petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_HUNDRED));
            resultMap.put("text","恭喜你刷新了纪录！");
        }

        resultMap.put("point",point);
        resultMap.put("imgUrl",student.getPartUrl());

        return ServerResponse.createBySuccess(resultMap);
    }

    @Override
    public ServerResponse<Object> saveCapTeksTest(HttpSession session, WordUnitTestDTO wordUnitTestDTO) {
        Student student = getStudent(session);
        TestRecord testRecord;

        wordUnitTestDTO.setClassify(9);

        // 判断当前单元是不是首次进行测试
        boolean isFirst = false;
        TestRecord testRecordOld = testRecordMapper.selectByStudentIdAndUnitId(student.getId(),
                wordUnitTestDTO.getUnitId()[0], "课文测试", "课文测试");
        if (testRecordOld == null) {
            isFirst = true;
        }

        int goldCount = this.saveGold(isFirst, wordUnitTestDTO, student, testRecordOld);
        if (testRecordOld == null) {
            testRecord = new TestRecord();
            // 首次测试大于或等于80分，超过历史最高分次数 +1
            if (wordUnitTestDTO.getPoint() >= PASS) {
                testRecord.setBetterCount(1);
            } else {
                testRecord.setBetterCount(0);
            }
        } else {
            testRecord = new TestRecord();
            testRecord.setBetterCount(testRecordOld.getBetterCount());
        }
        testRecord.setCourseId(wordUnitTestDTO.getCourseId());
        testRecord.setUnitId(wordUnitTestDTO.getUnitId()[0]);
        testRecord.setPoint(wordUnitTestDTO.getPoint());
        testRecord.setErrorCount(wordUnitTestDTO.getErrorCount());
        testRecord.setRightCount(wordUnitTestDTO.getRightCount());
        testRecord.setGenre("课文测试");
        testRecord.setStudentId(student.getId());
        testRecord.setTestEndTime(new Date());
        testRecord.setTestStartTime((Date) session.getAttribute(TimeConstant.BEGIN_START_TIME));
        if(wordUnitTestDTO.getErrorCount()!=null&& wordUnitTestDTO.getRightCount()!=null){
            testRecord.setQuantity(wordUnitTestDTO.getErrorCount() + wordUnitTestDTO.getRightCount());
        }
        testRecord.setAwardGold(goldCount);
        testRecord.setStudyModel("课文测试");
        testRecordMapper.insert(testRecord);
        studentMapper.updateByPrimaryKeySelective(student);
        Learn learn = new Learn();
        learn.setLearnTime(new Date());
        learn.setUpdateTime(new Date());
        learn.setCourseId(wordUnitTestDTO.getCourseId());
        learn.setUnitId(wordUnitTestDTO.getUnitId()[0]);
        learn.setType(1);
        learn.setStudyModel(testRecord.getStudyModel());
        learn.setStudentId(student.getId());
        Long aLong = learnMapper.selTeksLearn(learn);
        if(aLong!=null){
            learn.setId(aLong);
            learnMapper.updTeksLearn(learn);
        }else{
            learnMapper.insert(learn);
        }
        session.removeAttribute(TimeConstant.BEGIN_START_TIME);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("gold",goldCount);
        int energy = getEnergy(student, wordUnitTestDTO.getPoint());
        studentMapper.updateByPrimaryKeySelective(student);
        resultMap.put("energy",energy);
        Integer point = wordUnitTestDTO.getPoint();
        if (point < PASS) {
            resultMap.put("petName",petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_LESS_EIGHTY));
            resultMap.put("text","很遗憾，闯关失败，再接再厉。");
        } else if (point < NINETY_POINT) {
            resultMap.put("petName",petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_EIGHTY_TO_HUNDRED));
            resultMap.put("text","闯关成功，独孤求败！");
        } else {
            resultMap.put("petName",petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_HUNDRED));
            resultMap.put("text","恭喜你刷新了纪录！");
        }

        resultMap.put("point",point);
        resultMap.put("imgUrl",student.getPartUrl());
        return ServerResponse.createBySuccess(resultMap);
    }

    @Override
    public ServerResponse<TestDetailVo> getTestDetail(HttpSession session, Long testId) {
        Student student = getStudent(session);
        TestRecord testRecord = testRecordMapper.selectById(testId);
        TestDetailVo testDetailVo = testRecordMapper.selectTestDetailVo(student.getId(), testId);
        testDetailVo.setTitle(testDetailVo.getTitle().replaceAll("例句","句型"));
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
            int intUseTime = Integer.valueOf(useTime);
            return "用时：" + (intUseTime / 60) + " 分 " + (intUseTime % 60) + "秒";
        }
        return "用时：0 分 0 秒";
    }


    /**
     * 保存单元闯关测试
     * @param session
     * @param wordUnitTestDTO 需要保存的数据的参数
     * @param testDetail
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<TestResultVo> saveWordUnitTest(HttpSession session, WordUnitTestDTO wordUnitTestDTO, String testDetail) {

        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        if (StringUtils.isEmpty(student.getPetName())) {
            student.setPetName("大明白");
            student.setPartUrl(PetImageConstant.DEFAULT_IMAGE);
        }

        TestResultVo vo = new TestResultVo();
        // 是否是第一次进行当前模块下的单元闯关测试标识
        boolean isFirst = false;

        String[] correctWord = wordUnitTestDTO.getCorrectWord();
        Integer[] correctWordId = wordUnitTestDTO.getCorrectWordId();
        String[] errorWord = wordUnitTestDTO.getErrorWord();
        Integer[] errorWordId = wordUnitTestDTO.getErrorWordId();
        Long[] unitId = wordUnitTestDTO.getUnitId();
        Long courseId = unitMapper.selectCourseIdByUnitId(unitId[0]);
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
        int goldCount = 0;
        int addEnergy = 0;
        if (!redisOpt.isRepeatSubmit(student.getId(), (Date) session.getAttribute(TimeConstant.BEGIN_START_TIME))) {
            saveTestLearnAndCapacity.saveTestAndCapacity(correctWord, errorWord, correctWordId, errorWordId, session, unitId, classify);

            goldCount = this.saveGold(isFirst, wordUnitTestDTO, student, testRecord);

            Long testId = this.saveTestRecord(courseId, student, session, wordUnitTestDTO, testRecord, goldCount);

            // 保存测试记录详情
            if (testDetail != null) {
                this.saveTestDetail(testDetail, testId, classify, student);
            }

            // 获取需要奖励的能量值
            addEnergy = getEnergy(student, point);


        }

        String msg;
        // 默写
        if(classify == 3 || classify == 6) {
            msg = getMessage(student, vo, point, FIVE);
            if (point >= FIVE) {
                ccieUtil.saveCcieTest(student, 1, classify, courseId, unitId[0], point);
            }
        }else if(classify == 4 || classify == 2) {
            // 听力
            msg = getMessage(student, vo, point, SIX);
            if (point >= SIX) {
                ccieUtil.saveCcieTest(student, 1, classify, courseId, unitId[0], point);
            }
        }else {
            msg = getMessage(student, vo, point, PASS);
            if (point >= PASS) {
                ccieUtil.saveCcieTest(student, 1, classify, courseId, unitId[0], point);
            }
        }

        vo.setMsg(msg);
        vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, point, "单元闯关测试"));
        vo.setGold(goldCount);
        vo.setEnergy(addEnergy);
        countMyGoldUtil.countMyGold(student);
        studentMapper.updateByPrimaryKeySelective(student);
        session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        return ServerResponse.createBySuccess(vo);
    }

    /**
     * 保存句子测试
     * @param session
     * @param wordUnitTestDTO 需要保存的数据的参数
     * @param testDetail
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<TestResultVo> saveSentenceUnitTest(HttpSession session, WordUnitTestDTO wordUnitTestDTO, String testDetail) {

        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        if (StringUtils.isEmpty(student.getPetName())) {
            student.setPetName("大明白");
            student.setPartUrl(PetImageConstant.DEFAULT_IMAGE);
        }

        TestResultVo vo = new TestResultVo();
        // 是否是第一次进行当前模块下的单元闯关测试标识
        boolean isFirst = false;

        String[] correctWord = wordUnitTestDTO.getCorrectWord();
        Integer[] correctWordId = wordUnitTestDTO.getCorrectWordId();
        String[] errorWord = wordUnitTestDTO.getErrorWord();
        Integer[] errorWordId = wordUnitTestDTO.getErrorWordId();
        Long[] unitId = wordUnitTestDTO.getUnitId();
        Long courseId = unitMapper.selectCourseIdByUnitId(unitId[0]);
        Integer classify = wordUnitTestDTO.getClassify();
        String type = commonMethod.getTestType(wordUnitTestDTO.getClassify());

        Integer point = wordUnitTestDTO.getPoint();

        // 获取需要奖励的能量值
        int addEnergy = getEnergy(student, point);

        // 保存测试记录
        // 查看是否已经有该单元当前模块的单元闯关测试记录
        TestRecord testRecord = testRecordMapper.selectByStudentIdAndUnitId(student.getId(),
                wordUnitTestDTO.getUnitId()[0], "句子测试", type);
        if (testRecord == null) {
            isFirst = true;
        }
        saveTestLearnAndCapacity.saveTestAndCapacity(correctWord, errorWord, correctWordId, errorWordId, session, unitId, classify);

        // 根据不同分数奖励学生金币
        int goldCount = this.saveGold(isFirst, wordUnitTestDTO, student, testRecord);

        Long testId = this.saveTestRecord(courseId, student, session, wordUnitTestDTO, testRecord, goldCount);

        String msg;
        // 默写
        if(classify == 3 || classify == 6) {
            msg = getMessage(student, vo, point, FIVE);
        }else if(classify == 4 || classify == 2) {
            // 听力
            msg = getMessage(student, vo, point, SIX);
        }else {
            msg = getMessage(student, vo, point, PASS);
        }

        // 保存测试记录详情
        if (testDetail != null) {
            this.saveTestDetail(testDetail, testId, classify, student);
        }

        vo.setMsg(msg);
        vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, point, "句子测试"));
        vo.setGold(goldCount);
        vo.setEnergy(addEnergy);
        countMyGoldUtil.countMyGold(student);
        ccieUtil.saveCcieTest(student, 1, classify, courseId, unitId[0], point);
        studentMapper.updateByPrimaryKeySelective(student);
        session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        return ServerResponse.createBySuccess(vo);
    }

    /**
     * 根据学生分数获取响应的说明语
     *
     * @param student
     * @param vo
     * @param point
     * @param pass  及格分数
     * @return
     */
    private String getMessage(Student student, TestResultVo vo, Integer point, int pass) {
        String msg;
        if (point < pass) {
            msg = "很遗憾，闯关失败，再接再厉。";
            vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_LESS_EIGHTY));
        } else if (point < FULL_MARK) {
            msg = "闯关成功，独孤求败！";
            vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_EIGHTY_TO_HUNDRED));
        } else {
            msg = "恭喜你刷新了纪录！";
            vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.UNIT_TEST_HUNDRED));
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
            if (testRecordInfos.size() > 0) {
                try {
                    testRecordInfoMapper.insertList(testRecordInfos);
                } catch (Exception e) {
                    LOGGER.error("学生测试记录详情保存失败：studentId=[{}], testId=[{}], modelType=[{}], error=[{}]",
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
            msg = "id为：" + student.getId() + "的学生在" + commonMethod.getTestType(wordUnitTestDTO.getClassify())
                    + " 模块下的单元闯关测试中首次闯关成功，获得#" + goldCount + "#枚金币";
        } else {
            msg = "id为：" + student.getId() + "的学生在" + model + " 模块下，获得#" + goldCount + "#枚金币";
        }
        RunLog runLog = new RunLog(student.getId(), 4, msg, new Date());
        runLog.setCourseId(student.getCourseId());
        runLog.setUnitId(student.getUnitId());
        runLogMapper.insert(runLog);
        LOGGER.info(msg);
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
            goldCount = getGoldCount(wordUnitTestDTO, student, point);
        } else {
            // 查询当前单元测试历史最高分数
            int betterPoint = testRecordMapper.selectUnitTestMaxPointByStudyModel(student.getId(), wordUnitTestDTO.getUnitId()[0],
                    wordUnitTestDTO.getClassify());

            // 非首次测试成绩本次测试成绩大于历史最高分，超过历史最高分次数 +1并且金币奖励翻倍
            if (betterPoint < wordUnitTestDTO.getPoint()) {
                int betterCount = testRecord.getBetterCount() + 1;
                testRecord.setBetterCount(betterCount);
                goldCount = getGoldCount(wordUnitTestDTO, student, point);
            }
        }
        return goldCount;
    }

    private int getGoldCount(WordUnitTestDTO wordUnitTestDTO, Student student, int point) {
        int goldCount;
        if (point < SIX) {
            goldCount = 0;
        } else if (point < SEVENTY) {
            goldCount = TestAwardGoldConstant.UNIT_TEST_SIXTY_TO_SEVENTY;
        } else if (point < PASS) {
            goldCount = TestAwardGoldConstant.UNIT_TEST_SEVENTY_TO_EIGHTY;
        } else if (point < NINETY_POINT) {
            goldCount = TestAwardGoldConstant.UNIT_TEST_EIGHTY_TO_NINETY;
        } else if (point < FULL_MARK) {
            goldCount = TestAwardGoldConstant.UNIT_TEST_NINETY_TO_FULL;
        } else {
            goldCount = TestAwardGoldConstant.UNIT_TEST_FULL;
        }
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
     * @param testRecordOld     上次单元闯关测试的记录
     * @param goldCount       奖励的金币数
     * @return
     */
    private Long saveTestRecord(Long courseId, Student student, HttpSession session, WordUnitTestDTO wordUnitTestDTO,
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
            if (point >= PASS) {
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
        testRecord.setPoint(wordUnitTestDTO.getPoint());
        testRecord.setQuantity(quantity);
        testRecord.setRightCount(correctWord == null ? 0 : correctWord.length);
        testRecord.setStudentId(student.getId());
        testRecord.setStudyModel(commonMethod.getTestType(wordUnitTestDTO.getClassify()));
        testRecord.setTestEndTime(new Date());
        testRecord.setTestStartTime((Date) session.getAttribute(TimeConstant.BEGIN_START_TIME));
        testRecord.setUnitId(wordUnitTestDTO.getUnitId()[0]);
        testRecord.setAwardGold(goldCount);

        getUnitTestMsg(testRecord, point);
        testRecordMapper.insert(testRecord);
        return testRecord.getId();
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
    public ServerResponse<List<SentenceTranslateVo>> getSentenceUnitTest(HttpSession session, Long unitId, Integer type, Integer pageNum) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        student = studentMapper.selectByPrimaryKey(student.getId());
        if (session.getAttribute(TimeConstant.BEGIN_START_TIME) == null) {
            session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        }
        // 获取当前单元下其中一个例句
        List<Sentence> sentences = sentenceMapper.selectByUnitId(unitId);
        List<SentenceTranslateVo> sentenceTestResults = testResultUtil.getSentenceTestResults(sentences, MathUtil.getRandom(4, 6), type);
        return ServerResponse.createBySuccess(sentenceTestResults);
    }



    @Override
    public ServerResponse<Object> showRecord(String course_id, Integer type, HttpSession session, Integer page, Integer rows) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Long studentId = student.getId();

        if(page == null){
            page = 1;
        }
        if(rows != null){
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
        for(TestRecordVo record: records){
            recordId = record.getId();
            Map<String, Object> map = new HashMap<>(16);
            studyModel = record.getStudyModel().replace("例句","句型");
            map.put("id", recordId);

            if (Objects.equals("单元闯关测试", record.getGenre())) {
                map.put("genre", studyModel);
            } else if (record.getGenre()!= null && record.getGenre().contains("五维测试")) {
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<String> pushGameCourse(HttpSession session, Integer point) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        String courseName = this.pushCourse(point, student, "学前游戏测试");
        return ServerResponse.createBySuccess(courseName);
    }

    /**
     * 学前测试,从课程取30道题
     *  保存的时候只保存测试记录
     *
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> getPreSchoolTest(HttpSession session) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Long courseId = student.getCourseId();

        // 1.题类型
        String[] type = {"英译汉", "汉译英", "听力理解"};
        // 随机取三十道题
        List<Vocabulary> vocabularies = vocabularyMapper.getRandomCourseThirty(courseId);

        // 处理结果
        List<TestResult> testResults = testResultUtil.getWordTestesForCourse(type, vocabularies.size(), vocabularies, courseId);

        // 设置等级测试开始时间
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        return ServerResponse.createBySuccess(testResults);
    }

    /**
     * 保存学前测试, 课程前测
     * @param session
     * @param testRecord
     * @return
     */
    @Override
    public ServerResponse<TestResultVo> savePreSchoolTest(HttpSession session, TestRecord testRecord) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        TestResultVo vo = new TestResultVo();
        vo.setPetUrl(student.getPartUrl());
        // 游戏测试开始时间
        Date gameStartTime = (Date) session.getAttribute(TimeConstant.BEGIN_START_TIME);
        session.removeAttribute(TimeConstant.BEGIN_START_TIME);

        testRecord.setStudentId(student.getId());
        testRecord.setCourseId(student.getCourseId());
        testRecord.setTestStartTime(gameStartTime);
        testRecord.setTestEndTime(new Date());
        testRecord.setGenre("学前测试");
        testRecord.setQuantity(testRecord.getErrorCount() + testRecord.getRightCount());

        int gold = 0;
        int point = testRecord.getPoint();
        if (point < 80) {
            //gold = 2;
             vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, point, "学前测试"));
            // vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.LEVEL_TEST_LESS_EIGHTY));
            // testRecord.setExplain("失败");
            vo.setMsg("你和优秀的人差的不是智商，是努力。");
        } else {
            //gold = 5;
             vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, point, "学前测试"));
            // vo.setPetSay(petSayUtil.getMP3Url(student.getPetName(), PetMP3Constant.LEVEL_TEST_GREATER_NINETY));
            // testRecord.setExplain("成功");
            vo.setMsg("没有激流就称不上勇进，没有山峰则谈不上攀登。");
        }
        testRecord.setAwardGold(gold);
        vo.setGold(gold);

        // 奖励
        if (gold > 0) {
            this.saveLog(student, gold, null, "学前测试");
            session.setAttribute(UserConstant.CURRENT_STUDENT, student);
            studentMapper.updateByPrimaryKeySelective(student);
        }

        // 保存摸底测试记录
        int count = testRecordMapper.insertSelective(testRecord);
        studentMapper.updateByPrimaryKeySelective(student);
        return ServerResponse.createBySuccess(vo);
    }
}
