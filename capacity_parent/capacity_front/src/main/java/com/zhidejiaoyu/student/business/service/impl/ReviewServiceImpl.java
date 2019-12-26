package com.zhidejiaoyu.student.business.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhidejiaoyu.common.vo.student.SentenceTranslateVo;
import com.zhidejiaoyu.common.vo.student.testCenter.TestCenterVo;
import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.annotation.TestChangeAnnotation;
import com.zhidejiaoyu.common.constant.TestAwardGoldConstant;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.study.MemoryDifficultyUtil;
import com.zhidejiaoyu.common.study.TestPointUtil;
import com.zhidejiaoyu.common.study.WordPictureUtil;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.PictureUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.goldUtil.TestGoldUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.GoldResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.testVo.TestResultVO;
import com.zhidejiaoyu.common.utils.testUtil.TestResultUtil;
import com.zhidejiaoyu.common.utils.learn.PerceiveEngineUtil;
import com.zhidejiaoyu.student.common.SaveTestLearnAndCapacity;
import com.zhidejiaoyu.common.constant.PetMP3Constant;
import com.zhidejiaoyu.common.dto.WordUnitTestDTO;
import com.zhidejiaoyu.student.business.service.ReviewService;
import com.zhidejiaoyu.student.business.service.SentenceService;
import com.zhidejiaoyu.common.utils.CcieUtil;
import com.zhidejiaoyu.common.utils.pet.PetSayUtil;
import com.zhidejiaoyu.common.utils.pet.PetUrlUtil;
import com.zhidejiaoyu.common.vo.TestResultVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 智能复习业务逻辑层
 *
 * @author qizhentao
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ReviewServiceImpl extends BaseServiceImpl<CapacityMemoryMapper, CapacityMemory> implements ReviewService {

    private Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);

    @Autowired
    private TestResultUtil testResultUtil;

    @Autowired
    private UnitVocabularyMapper unitVocabularyMapper;

    /**
     * 记忆追踪mapper
     */
    @Autowired
    private CapacityReviewMapper capacityMapper;

    @Autowired
    private TestRecordMapper testRecordMapper;

    @Autowired
    private SentenceMapper sentenceMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private CommonMethod commonMethod;

    @Autowired
    private BaiduSpeak baiduSpeak;

    @Autowired
    private SentenceService sentenceService;

    @Autowired
    private SaveTestLearnAndCapacity saveTestLearnAndCapacity;

    @Autowired
    private RunLogMapper runLogMapper;
    @Autowired
    private TestRecordInfoMapper testRecordInfoMapper;


    @Autowired
    private SentenceListenMapper sentenceListenMapper;
    /**
     * 记忆难度
     */
    @Autowired
    private MemoryDifficultyUtil memoryDifficultyUtil;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private CapacityPictureMapper capacityPictureMapper;

    @Autowired
    private WordPictureUtil wordPictureUtil;

    @Autowired
    private PetSayUtil petSayUtil;

    @Autowired
    private DurationMapper durationMapper;

    @Autowired
    private CcieUtil ccieUtil;

    @Autowired
    private TestGoldUtil testGoldUtil;

    @Autowired
    private SentenceTranslateMapper sentenceTranslateMapper;


    @Override
    public Map<String, Integer> testReview(String unitId, String studentId) {
        // 封装返回的数据 - 智能记忆智能复习数量
        Map<String, Integer> map = new HashMap<String, Integer>();

        // 当前时间
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = s.format(new Date());

        CapacityReview cr = new CapacityReview();
        cr.setUnit_id(Long.valueOf(unitId));
        cr.setStudent_id(Long.valueOf(studentId));
        cr.setPush(dateTime);
        // 慧记忆模块许复习量
        cr.setClassify("1");
        Integer a = capacityMapper.countCapacity_memory(cr);
        // 慧听写模块许复习量
        cr.setClassify("2");
        Integer b = capacityMapper.countCapacity_memory(cr);
        // 慧默写模块许复习量
        cr.setClassify("3");
        Integer c = capacityMapper.countCapacity_memory(cr);
        // 听力模块许复习量
        cr.setClassify("4");
        Integer d = capacityMapper.countCapacity_memory(cr);
        // 翻译模块许复习量
        cr.setClassify("5");
        Integer e = capacityMapper.countCapacity_memory(cr);
        // 默写模块许复习量
        cr.setClassify("6");
        Integer f = capacityMapper.countCapacity_memory(cr);

        map.put("1", a);
        map.put("2", b);
        map.put("3", c);
        map.put("4", d);
        map.put("5", e);
        map.put("6", f);

        return map;
    }

    @Override
    public ServerResponse<Object> testCapacityReview(String unitId, int classify, HttpSession session, boolean pattern) {
        // 封装测试单词
        List<Vocabulary> vocabularies;

        // 获取当前学生信息
        Student student = getStudent(session);
        // 学生id
        Long id = student.getId();

        // 复习测试上一单元
        if (pattern) {
            unitId = learnMapper.getEndUnitIdByStudentId(id);
        }

        // 当前时间
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = s.format(new Date());
        // 设置游戏测试开始时间
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        String[] type = new String[1];
        // 测试词汇
        if (classify == 1) {
            type[0] = "英译汉";
        } else if (classify == 2) {
            type[0] = "听力理解";
        } else if (classify == 3) {
            type[0] = "汉译英";
        } else if (classify == 4) {
            type[0] = "听力理解";
        } else if (classify == 5) {
            type[0] = "英译汉";
        } else if (classify == 6) {
            type[0] = "汉译英";
        }

        if (classify == 1 || classify == 2 || classify == 3) {
            // 查询记忆追踪需要复习的单词
            CapacityReview ca = new CapacityReview();
            ca.setStudent_id(id);
            ca.setUnit_id(Long.valueOf(unitId));
            ca.setClassify(classify + "");
            ca.setPush(dateTime);
            vocabularies = capacityMapper.selectCapacity(ca);

            // 处理结果
            List<TestResultVO> testResults = testResultUtil.getWordTestesForUnit(type, vocabularies.size(), vocabularies, Long.valueOf(unitId));
            return ServerResponse.createBySuccess(testResults);
        } /*else if (classify >= 4) {
            List<CapacityReview> reviews = capacityMapper.selectSentenceCapacity(id, unit_id, classify);
            List<SentenceTranslateVo> vos = new ArrayList<>(reviews.size());
            if (classify == 4) {

            } else if (classify == 5) {

            } else if (classify == 6) {

            }
        }*/
		/*else if() {
			// 查询记忆追踪需要复习的例句
			CapacityReview ca = new CapacityReview();
			ca.setstudentId(id);
			ca.setUnit_id(Long.valueOf(unit_id));
			ca.setClassify(classify+"");
			vocabularies = capacityMapper.selectCapacity(ca);

			// 处理结果
			List<TestResult> testResults = testResultUtil.getWordTestes(type, vocabularies.size(), vocabularies);
			return ServerResponse.createBySuccess(testResults);
		}*/
        return ServerResponse.createByErrorMessage("选择模块后进行测试!");
    }

    @Override
    public Map<String, Object> reviewCapacityMemory(Student student, String unitId, int classify, String courseId) {

        Long studentId = student.getId();

        Map<String, Object> map = new HashMap<>(16);

        CapacityReview ca = new CapacityReview();
        // 查询条件1:学生id
        ca.setStudent_id(studentId);

        // 智能复习, 根据单元查询
        if (StringUtils.isNotBlank(unitId)) {
            // 查询条件2.1:单元id
            ca.setUnit_id(Long.valueOf(unitId));

            // count单元表单词有多少个
            Integer count = capacityMapper.countNeedReviewByCourseIdOrUnitId(student, null, Long.valueOf(unitId), commonMethod.getTestType(classify));
            map.put("wordCount", count);
        }
        // 查询条件3:模块
        ca.setClassify(classify + "");
        // 查询条件4:当前时间
        ca.setPush(DateUtil.DateTime());

        // 查询一条需要复习的单词数据
        CapacityReview vo = capacityMapper.ReviewCapacity_memory(ca);
        // 没有需要复习的单词(如果没有需要复习的单词web页面不能走该接口)
        if (vo == null) {
            return null;
        }
        String word = vo.getWord();
        // 单词id
        map.put("id", vo.getVocabulary_id());
        // 单词
        map.put("word", word);
        // 翻译
        map.put("wordChinese", vo.getWord_chinese());
        // 音节
        map.put("wordyj", vo.getSyllable());
        if (StringUtils.isBlank(unitId)) {
            map.put("unitId", vo.getUnit_id());
            unitId = vo.getUnit_id().toString();
        }
        // 如果音节为空
        if (vo.getSyllable() == null) {
            // 单词
            map.put("wordyj", word);
        }
        Vocabulary vocabulary = vocabularyMapper.selectByPrimaryKey(vo.getVocabulary_id());
        if (vocabulary != null) {
            map.put("soundmark", vocabulary.getSoundMark());
        }
        // 读音
        map.put("readUrl", baiduSpeak.getLanguagePath(word));

        // 记忆强度
        map.put("memoryStrength", vo.getMemory_strength());
        // 记忆难度
        if (classify == 1) {
            CapacityMemory cm = new CapacityMemory();
            cm.setStudentId(studentId);
            cm.setUnitId(Long.valueOf(unitId));
            cm.setVocabularyId(vo.getVocabulary_id());
            cm.setMemoryStrength(vo.getMemory_strength());
            cm.setFaultTime(vo.getFault_time());
            int hard = memoryDifficultyUtil.getMemoryDifficulty(cm, 1);
            map.put("memoryDifficulty", hard);
            map.put("engine", PerceiveEngineUtil.getPerceiveEngine(hard, vo.getMemory_strength()));
            map.put("wordChineseList", getInterferenceChinese(Long.parseLong(unitId), vo.getVocabulary_id(), vo.getWord_chinese(), unitVocabularyMapper, unitMapper));
        }
        map.put("studyNew", false);

        return map;
    }

    /**
     * 封装中文干扰项
     *
     * @param unitId
     * @param vocabularyId
     * @param wordChinese
     * @return
     */
    private List<Map<String, Boolean>> getChinese(Long unitId, Long vocabularyId, String wordChinese) {
        return getInterferenceChinese(unitId, vocabularyId, wordChinese, unitVocabularyMapper, unitMapper);
    }

    /**
     * 获取中文选项
     *
     * @param unitId
     * @param vocabularyId
     * @param wordChinese
     * @param unitVocabularyMapper
     * @param unitMapper
     * @return
     */
    static List<Map<String, Boolean>> getInterferenceChinese(Long unitId, Long vocabularyId, String wordChinese, UnitVocabularyMapper unitVocabularyMapper, UnitMapper unitMapper) {
        List<String> chinese = unitVocabularyMapper.selectWordChineseByUnitIdAndCurrentWordId(unitId, vocabularyId);
        if (chinese.size() < 3) {
            Unit unit = unitMapper.selectById(unitId);
            chinese.addAll(unitVocabularyMapper.selectWordChineseByCourseIdAndNotInUnitId(unit.getCourseId(), unitId, 3 - chinese.size()));
        }
        Collections.shuffle(chinese);
        List<Map<String, Boolean>> wordChineseList = chinese.stream().limit(3).map(result -> {
            Map<String, Boolean> map = new HashMap<>(16);
            map.put(result, false);
            return map;
        }).collect(Collectors.toList());
        Map<String, Boolean> map = new HashMap<>(16);
        map.put(wordChinese, true);
        wordChineseList.add(map);
        Collections.shuffle(wordChineseList);
        return wordChineseList;
    }

    @Override
    public Object reviewSentenceListen(Student student, String unitId, int classify, String courseId, Integer type) {

        Long studentId = student.getId();
        Map<String, Object> map = new HashMap<>(16);
        // 当前时间
        String dateTime = DateUtil.DateTime();

        int nextInt = 1;
        // 例句id
        CapacityReview vo = null;

        // 1.去记忆追踪中获取需要复习的例句
        // 智能复习 (根据单元查询)
        if (StringUtils.isNotBlank(unitId)) {
            if (classify == 4) {
                // 例句听力
                vo = capacityMapper.ReviewSentence_listen(studentId, unitId, dateTime);
            } else if (classify == 5) {
                // 例句翻译 sentence_translate
                vo = capacityMapper.ReviewSentence_translate(studentId, unitId, dateTime);
            } else if (classify == 6) {
                // 例句默写 sentence_write
                vo = capacityMapper.ReviewSentence_write(studentId, unitId, dateTime);
            }

            // 需要复习的例句个数
            int sentenceCount = capacityMapper.countNeedReviewByCourseIdOrUnitId(student, null,
                    Long.valueOf(unitId), commonMethod.getTestType(classify));
            map.put("sentenceCount", sentenceCount);
        }

        if (vo == null) {
            logger.info("courseid:{}->unitId:{} 下没有需要复习的句型", courseId, unitId);
            return null;
        }
        // 2.通过记忆追踪中的例句id-获取例句信息 (vo.getVocabulary_id()是例句id)
        Sentence sentence = sentenceMapper.selectByPrimaryKey(vo.getVocabulary_id());

        // 分割例句
        String english = sentence.getCentreExample().replace("#", " ").replace("$", "");
        // 分割翻译
        String chinese = sentence.getCentreTranslate().replace("*", "");

        // 例句id
        map.put("id", sentence.getId());
        // 例句id
        // 任务课程复习
        if (unitId == null) {
            map.put("unitId", vo.getUnit_id());
        } else {// 智能复习
            map.put("unitId", unitId);
        }
        // 例句读音
        map.put("readUrl", baiduSpeak.getSentencePath(sentence.getCentreExample()));
        // 例句翻译
        map.put("chinese", chinese);
        // 例句英文原文
        map.put("sentence", english);
        // 记忆强度
        map.put("memoryStrength", vo.getMemory_strength());
        //if (classify != 6) {
        if (nextInt == 1) {
            testResultUtil.getOrderEnglishList(map, sentence.getCentreExample(), sentence.getExampleDisturb(), type);
        } else {
            testResultUtil.getOrderChineseList(map, sentence.getCentreTranslate(), sentence.getTranslateDisturb(), type);
        }
        return map;
    }

    @Override
    public ServerResponse<List<TestCenterVo>> testCentreIndex(Long unitId, Integer type, HttpSession session) {
        // 获取当前学生信息
        Student student = getStudent(session);
        Long studentId = student.getId();

        List<TestCenterVo> testCenterVos = new ArrayList<>();

        // 单词模块
        if (type == 1) {
            for (int i = 0; i <= 3; i++) {
                packageTestCenterInfo(unitId, studentId, testCenterVos, i);
            }
        } else {
            for (int i = 4; i <= 6; i++) {
                packageTestCenterInfo(unitId, studentId, testCenterVos, i);
            }
        }


        return ServerResponse.createBySuccess(testCenterVos);
    }

    private void packageTestCenterInfo(Long unitId, Long studentId, List<TestCenterVo> testCenterVos, int i) {
        TestCenterVo testCenterVo = new TestCenterVo();
        String classify = commonMethod.getTestType(i);
        // 已学
        Integer learnCount = capacityMapper.countAlreadyStudyWord(studentId, unitId, classify, i);
        // 生词
        Integer unknownCount = capacityMapper.countAccrueWord(studentId, unitId, classify, i);
        // 熟词
        Integer knownCount = capacityMapper.countRipeWord(studentId, unitId, classify, i);

        testCenterVo.setClassify(i);
        testCenterVo.setAlreadyStudy(learnCount);
        testCenterVo.setAccrue(unknownCount);
        testCenterVo.setRipe(knownCount);

        testCenterVos.add(testCenterVo);
    }

    /**
     * 测试中心题
     */
    @Override
    public ServerResponse<Object> testcentre(String courseId, String unitId, int select, int classify, Boolean isTrue, HttpSession session) {
        // 获取当前学生信息
        Student student = getStudent(session);
        // 学生id
        Long studentId = student.getId();

        // true 扣除一金币
        if (isTrue) {
            // 1.查询学生剩余金币
            Integer gold = studentMapper.getSystem_gold(studentId);
            if (gold != null && gold > 0) {
                // 扣除1金币
                int state = studentMapper.updateBySystem_gold((gold - 1), studentId);
            } else {
                // 金币不足
                return ServerResponse.createBySuccess(GoldResponseCode.LESS_GOLD.getCode(), "金币不足");
            }
            // false 第一次点击五维测试  1.查询是否做过该课程的五维测试 2.如果做过返回扣除1金币提示
        } else {
            Integer judgeTest = testRecordMapper.selectJudgeTestToModel(courseId, studentId, classify, select);
            if (judgeTest != null) {
                // 已经测试过, 提示扣除金币是否测试
                return ServerResponse.createBySuccess(GoldResponseCode.NEED_REDUCE_GOLD.getCode(), "您已参加过该五维测试，再次测试需扣除1金币。");
            }
        }

        // 模块
        String classifyStr = "";
        if (classify == 1) {
            classifyStr = "慧记忆";
        } else if (classify == 2) {
            classifyStr = "慧听写";
        } else if (classify == 3) {
            classifyStr = "慧默写";
        }

        // 封装测试题单词
        List<Vocabulary> vocabularies = new ArrayList<>();

        // 词汇
        // 1模块:最多30道题? - 目前没做题量限制
        if (classify == 1) {
            // 1.题类型
            String[] type = {"英译汉", "汉译英", "听力理解"};

            // select: 1=已学, 2=生词, 3=熟词
            if (select == 1) {
                // 2.获取已学需要出的测试题
                vocabularies = capacityMapper.alreadyWordStrOne(studentId, unitId, classifyStr);
            } else if (select == 2) {
                // 3.获取生词需要出的测试题
                vocabularies = capacityMapper.accrueWordStrOne(studentId, unitId, classifyStr);
            } else if (select == 3) {
                // 4.获取熟词需要出的测试题
                vocabularies = capacityMapper.ripeWordStrOne(studentId, unitId, classifyStr);
            }

            // 处理结果
            List<TestResultVO> testResults = testResultUtil.getWordTestesForCourse(type, vocabularies.size(), vocabularies, Long.valueOf(courseId));

            // 获取课程下的单词id-单元id
            List<Long> ids = learnMapper.selectVocabularyIdByStudentIdAndCourseId(studentId, Long.valueOf(courseId), 1);
            Map<Long, Map<Long, Long>> longMapMap = unitMapper.selectIdMapByCourseIdAndWordIds(Long.valueOf(courseId), ids, studentId, classify);

            // 处理结果添加单元id
            for (TestResultVO reList : testResults) {
                // 单词id
                Long wordId = reList.getId();
                // 封装单元id
                if (longMapMap.containsKey(wordId)) {
                    reList.setUnitId(longMapMap.get(wordId).get("unitId"));
                }
            }
            return ServerResponse.createBySuccess(testResults);

            // 2,3模块:最多20道题?  - 目前没做题量限制
        } else if (classify == 2 || classify == 3) {
            // select: 1=已学, 2=生词, 3=熟词
            if (select == 1) {
                // 2.获取已学需要出的测试题
                vocabularies = capacityMapper.alreadyWordStr(studentId, unitId, classifyStr);
            } else if (select == 2) {
                // 3.获取生词需要出的测试题
                vocabularies = capacityMapper.accrueWordStr(studentId, unitId, classifyStr);
            } else if (select == 3) {
                // 4.获取熟词需要出的测试题
                vocabularies = capacityMapper.ripeWordStr(studentId, unitId, classifyStr);
            }

            // 慧听写 封装读音

            // 封装所有听写题
            List<Map<String, Object>> li = new ArrayList<>();
            for (Vocabulary vo : vocabularies) {
                // 封装一条听力题
                Map map = new HashMap();
                // 单词id
                map.put("id", vo.getId());
                // 单词
                map.put("word", vo.getWord());
                // 单元id
                map.put("unitId", unitId);

                if (classify == 2) {
                    try {
                        // 单词读音
                        map.put("readUrl", baiduSpeak.getLanguagePath(vo.getWord()));
                    } catch (Exception e) {
                        logger.error("获取单词" + vo.getWord() + "读音报错!");
                    }
                } else {
                    // 单词翻译
                    map.put("chinese", vo.getWordChinese());
                }

                li.add(map);
            }

            return ServerResponse.createBySuccess(li);
        }

        return null;
    }

    @Override
    public List<SentenceTranslateVo> testcentreSentence(String unitId, int select, int classify, HttpSession session, Integer type) {
        // 返回结果集
        Map<String, Object> map = new HashMap<String, Object>();
        // 获取当前学生信息
        Student student = getStudent(session);
        // 学生id
        Long studentId = student.getId();
        // 学生年级
        String grade = student.getGrade();

        // 模块
        String classifyStr = "";
        if (classify == 4) {
            classifyStr = "例句听力";
        } else if (classify == 5) {
            classifyStr = "例句翻译";
        } else if (classify == 6) {
            classifyStr = "例句默写";
        }

        List<Sentence> sentences = new ArrayList<>();

        // 已学
        if (select == 1) {
            // 查询 例句,例句翻译
            sentences = capacityMapper.centreReviewSentence_listen(studentId, unitId, classifyStr);
            // 生词
        } else if (select == 2) {
            sentences = capacityMapper.accrueCentreReviewSentence_listen(studentId, unitId, classifyStr);
            // 熟词
        } else if (select == 3) {
            sentences = capacityMapper.ripeCentreReviewSentence_listen(studentId, unitId, classifyStr);
        }
        return testResultUtil.getSentenceTestResults(sentences, classify, type);
    }

    /**
     * 根据课程id和学习id查询learn表
     */
    @Override
    public ServerResponse<Object> testeffect(String courseId, HttpSession session) {
        // 获取当前学生信息
        Student student = getStudent(session);
        // 学生id
        Long studentId = student.getId();

        // 1.题类型
        String[] type = {"英译汉", "汉译英", "听力理解"};

        //
        List<Vocabulary> vocabularies = capacityMapper.testeffect(courseId, studentId);

        // 处理结果
        List<TestResultVO> testResults = testResultUtil.getWordTestesForCourse(type, vocabularies.size(), vocabularies, Long.valueOf(courseId));
        return ServerResponse.createBySuccess(testResults);
    }

    /**
     * 五维测试
     */
    @Override
    public ServerResponse<Object> fiveDimensionTest(String courseId, boolean isTrue, HttpSession session) {
        Student student = getStudent(session);
        Long studentId = student.getId();

        // true 扣除一金币
        if (isTrue) {
            // 1.查询学生剩余金币
            Integer gold = studentMapper.getSystem_gold(studentId);
            if (gold != null && gold > 0) {
                // 扣除1金币
                studentMapper.updateBySystem_gold((gold - 1), studentId);
            } else {
                // 金币不足
                return ServerResponse.createBySuccess(GoldResponseCode.LESS_GOLD.getCode(), "金币不足");
            }
            // false 第一次点击五维测试  1.查询是否做过该课程的五维测试 2.如果做过返回扣除1金币提示
        } else {
            Integer judgeTest = testRecordMapper.selectJudgeTest(courseId, studentId, "单词五维测试");
            if (judgeTest != null) {
                // 已经测试过, 提示扣除金币是否测试
                return ServerResponse.createBySuccess(GoldResponseCode.NEED_REDUCE_GOLD.getCode(), "您已参加过该五维测试，再次测试需扣除1金币。");
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();

        // 查询课程下边一共有多少单词
        Integer countWord = unitMapper.countWordByCourse(courseId);
        if (countWord == 0) {
            return ServerResponse.createByError(500, "该课程下没有单词!");
        }

        if (countWord > 50) {
            countWord = 50;
        }

        // 平均分配题量 - b:英译汉,汉译英,听力理解需要的题数量, c:听力,默写需要的题数量
        // 总题量
        int count = countWord;
        // 分五份
        int aa = count / 5;
        // 1,2,3
        int b = aa * 3;
        // 4听力,5默写
        int c = aa * 2;
        // 获取count/5剩余的数量, 加到b,c中
        int countBC = 0;
        if (count > (b + c)) {
            countBC = count - (b + c);
        }
        if (countBC == 4) {
            b += 3;
            c += 1;
        } else if (countBC < 4 && countBC > 0) {
            b += countBC;
        }

        // 1. 获取课程下的所有打乱顺序的单词
        List<Vocabulary> list = capacityMapper.fiveDimensionTestAll(courseId);

        // 2.1 从课程中查出*个单词 - 英译汉,汉译英,听力理解 limit 0,b
        List<Vocabulary> vocabularies = list.subList(0, b);

        // 2.2 从课程中查出*个单词 - 听写,默写 limit b,c
        List<Vocabulary> vocabulariesTwo = list.subList(b, b + c);
        // 1.英译汉 2.汉译英 3.听力理解
        String[] typeA = {"英译汉"};
        String[] typeB = {"汉译英"};
        String[] typeC = {"听力理解"};
        List<TestResultVO> testResultsA = testResultUtil.getWordTestesForCourse(typeA, vocabularies.subList(0, vocabularies.size() / 3).size(),
                vocabularies.subList(0, vocabularies.size() / 3), Long.valueOf(courseId));
        List<TestResultVO> testResultsB = testResultUtil.getWordTestesForCourse(typeB, vocabularies.subList(vocabularies.size() / 3, (int) BigDecimalUtil.div((double) vocabularies.size(), 1.5, 0)).size(),
                vocabularies.subList(vocabularies.size() / 3, (int) BigDecimalUtil.div((double) vocabularies.size(), 1.5, 0)), Long.valueOf(courseId));
        List<TestResultVO> testResultsC = testResultUtil.getWordTestesForCourse(typeC, vocabularies.subList((int) BigDecimalUtil.div((double) vocabularies.size(), 1.5, 0), vocabularies.size()).size(),
                vocabularies.subList((int) BigDecimalUtil.div((double) vocabularies.size(), 1.5, 0), vocabularies.size()), Long.valueOf(courseId));

        result.put("testResults_a", testResultsA);
        result.put("testResults_b", testResultsB);
        result.put("testResults_c", testResultsC);

        // 4.听写
        List<Map<String, Object>> hearList = new ArrayList<>();
        // 5.默写
        List<Map<String, Object>> writeList = new ArrayList<>();

        int a = 0;
        for (Vocabulary vo : vocabulariesTwo) {
            // 用于封装一道题
            Map<String, Object> m = new LinkedHashMap<>(16);

            // 听写
            if (a < (vocabulariesTwo.size() / 2)) {
                m.put("type", "听写");
                m.put("id", vo.getId());
                m.put("word", vo.getWord());
                try {
                    // 单词读音
                    m.put("readUrl", baiduSpeak.getLanguagePath(vo.getWord()));
                } catch (Exception e) {
                    logger.error("获取单词" + vo.getWord() + "读音报错!");
                }
                hearList.add(m);
            } else {
                // 默写
                m.put("type", "默写");
                m.put("id", vo.getId());
                m.put("word", vo.getWord());
                m.put("chinese", vo.getWordChinese());
                writeList.add(m);
            }
            a++;
        }

        result.put("hear", hearList);
        result.put("write", writeList);

        return ServerResponse.createBySuccess(result);
    }

    @Override
    @GoldChangeAnnotation
    @TestChangeAnnotation(isUnitTest = false)
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<TestResultVo> saveTestCenter(String[] correctWord, String[] errorWord, Long[] correctWordId,
                                                       Long[] errorWordId, Long[] unitId, Integer classify, Long courseId,
                                                       HttpSession session, Integer point, String genre, String testDetail) {
        Student student = getStudent(session);

        // 保存测试记录
        int quantity;
        if (correctWord != null && errorWord != null) {
            quantity = correctWord.length + errorWord.length;
        } else if (correctWord != null) {
            quantity = correctWord.length;
        } else {
            quantity = errorWord.length;
        }
        int errorCount = errorWord == null ? 0 : errorWord.length;
        int rightCount = correctWord == null ? 0 : correctWord.length;
        Long uID = (unitId == null || unitId.length == 0) ? null : unitId[0];
        //获取单元闯关获取的能量数量
        TestResultVo vo = new TestResultVo();
        int number =0;
        if(uID!=null){
            number=testRecordMapper.selCount(student.getId(), courseId, unitId[0],
                    commonMethod.getTestType(classify), genre);
        }
        vo.setEnergy(getEnergy(student, point, number));

        // 把已学测试,生词测试,熟词测试保存慧追踪中
        if (!"单词五维测试".equals(genre) && !"例句五维测试".equals(genre)) {
            // 保存学习记录和慧追踪信息
            saveTestLearnAndCapacity.saveLearnAndCapacity(correctWord, errorWord, correctWordId, errorWordId, session, student, unitId, classify);
        }
        WordUnitTestDTO wordUnitTestDTO = new WordUnitTestDTO();
        wordUnitTestDTO.setPoint(point);
        wordUnitTestDTO.setUnitId(unitId);
        wordUnitTestDTO.setCourseId(courseId);
        wordUnitTestDTO.setClassify(classify);

        TestRecord testRecord = this.saveTestRecord(quantity, errorCount, rightCount, classify, session, student, point, genre, courseId, unitId);


        // 封装提示语
        packagePetSay(testRecord, wordUnitTestDTO, student, vo, genre);



        testRecordMapper.insert(testRecord);
        if (testDetail != null) {
            // 根据不同分数奖励学生金币
            this.saveTestDetail(testDetail, testRecord.getId(), classify, student);
        }

        return ServerResponse.createBySuccess(vo);
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
                    // 试卷题目，选择题
                    JSONObject subject = object.getJSONObject("subject");
                    final int[] j = {0};
                    TestRecordInfo finalTestRecordInfo = testRecordInfo;
                    JSONObject finalObject = object;
                    subject.forEach((key, val) -> {
                        if (Objects.equals(subject.get(key), true)) {
                            finalTestRecordInfo.setAnswer(TestServiceImpl.matchSelected(j[0]));
                        }
                        this.setOptions(finalTestRecordInfo, j[0], key);
                        if (finalObject.getJSONObject("userInput") != null) {
                            selected[0] = TestServiceImpl.matchSelected(finalObject.getJSONObject("userInput").getInteger("optionIndex"));
                        }
                        finalTestRecordInfo.setWord(word);
                        j[0]++;
                    });
                } else if (modelType == 2) {
                    // 慧听写
                    selected[0] = object.getString("userInput");
                    testRecordInfo.setWord(object.getString("readUrl"));
                    testRecordInfo.setAnswer(object.getString("word"));
                } else if (modelType == 3) {
                    // 慧默写
                    selected[0] = object.getString("userInput");
                    testRecordInfo.setWord(object.getString("chinese"));
                    testRecordInfo.setAnswer(object.getString("word"));
                }

                testRecordInfo.setTestId(testRecordId);
                testRecordInfo.setSelected(selected[0]);
                testRecordInfos.add(testRecordInfo);
            }
            if (testRecordInfos.size() > 0) {
                try {
                    testRecordInfoMapper.insertList(testRecordInfos);
                } catch (Exception e) {
                    logger.error("学生测试记录详情保存失败：studentId=[{}], testId=[{}], modelType=[{}], error=[{}]",
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
     * 已学测试, 生词测试, 熟词测试, 五维测试
     *
     * @param testRecord
     * @param dto
     * @param student
     * @param vo
     * @param genre
     */
    private void packagePetSay(TestRecord testRecord, WordUnitTestDTO dto, Student student, TestResultVo vo, String genre) {
        String msg = "";
        String petName = student.getPetName();
        int point = dto.getPoint();
        Long unitId = dto.getUnitId() == null ? null : dto.getUnitId()[0];
        long courseId = dto.getCourseId();
        int classify = dto.getClassify();
        switch (genre) {
            // 没有该模块
            case "测试复习":
                if (point < 80) {
                    msg = "闯关失败，请再接再厉！";
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.CAPACITY_REVIEW_LESS_EIGHTY));
                    vo.setBackMsg(new String[]{"别气馁，已经超越了", TestPointUtil.getPercentage(point), "的同学，继续努力吧！"});
                    testRecord.setPass(2);
                } else {
                    msg = "真让人刮目相看！继续学习吧！";
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.CAPACITY_REVIEW_EIGHTY_TO_HUNDRED));
                    ccieUtil.saveCcieTest(student, 6, classify, courseId, unitId, point);
                    vo.setBackMsg(new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(point), "的同学，再接再励！"});
                    testRecord.setPass(1);
                }
                vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, point, "智能复习测试"));
                break;
            case "已学测试":
                if (point < 80) {
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.TEST_CENTER_LESS_EIGHTY));
                } else if (point < 90) {
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.TEST_CENTER_EIGHTY_TO_NINETY));
                } else {
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.TEST_CENTER_NINETY_TO_HUNDRED));
                    ccieUtil.saveCcieTest(student, 3, classify, courseId, unitId, point);
                }
                if (point < 90) {
                    vo.setBackMsg(new String[]{"别气馁，已经超越了", TestPointUtil.getPercentage(point), "的同学，继续努力吧！"});
                    testRecord.setPass(2);
                } else {
                    vo.setBackMsg(new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(point), "的同学，再接再励！"});
                    testRecord.setPass(1);
                }

                msg = point < 90 ? "你的测试未通过，请再接再厉！" : "赞！VERY GOOD!记得学而时习之哦！";
                vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, point, genre));
                break;
            case "生词测试":
                if (point < 80) {
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.TEST_CENTER_LESS_EIGHTY));
                } else if (point < 90) {
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.TEST_CENTER_EIGHTY_TO_NINETY));
                } else {
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.TEST_CENTER_NINETY_TO_HUNDRED));
                    ccieUtil.saveCcieTest(student, 4, classify, courseId, unitId, point);
                }
                if (point < 90) {
                    vo.setBackMsg(new String[]{"别气馁，已经超越了", TestPointUtil.getPercentage(point), "的同学，继续努力吧！"});
                    testRecord.setPass(2);
                } else {
                    vo.setBackMsg(new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(point), "的同学，再接再励！"});
                    testRecord.setPass(1);
                }

                msg = point < 90 ? "你的测试未通过，请再接再厉！" : "赞！VERY GOOD!记得学而时习之哦！";
                vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, point, genre));
                break;
            case "熟词测试":
                if (point < 80) {
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.TEST_CENTER_LESS_EIGHTY));
                } else if (point < 90) {
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.TEST_CENTER_EIGHTY_TO_NINETY));
                } else {
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.TEST_CENTER_NINETY_TO_HUNDRED));
                    ccieUtil.saveCcieTest(student, 5, classify, courseId, unitId, point);
                }
                if (point < 90) {
                    vo.setBackMsg(new String[]{"别气馁，已经超越了", TestPointUtil.getPercentage(point), "的同学，继续努力吧！"});
                    testRecord.setPass(2);
                } else {
                    vo.setBackMsg(new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(point), "的同学，再接再励！"});
                    testRecord.setPass(1);
                }

                msg = point < 90 ? "你的测试未通过，请再接再厉！" : "赞！VERY GOOD!记得学而时习之哦！";
                vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, point, genre));
                break;
            case "生句测试":
                if (point < 80) {
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.TEST_CENTER_LESS_EIGHTY));
                } else if (point < 90) {
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.TEST_CENTER_EIGHTY_TO_NINETY));
                } else {
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.TEST_CENTER_NINETY_TO_HUNDRED));
                    ccieUtil.saveCcieTest(student, 4, classify, courseId, unitId, point);
                }
                if (point < 90) {
                    vo.setBackMsg(new String[]{"别气馁，已经超越了", TestPointUtil.getPercentage(point), "的同学，继续努力吧！"});
                    testRecord.setPass(2);
                } else {
                    vo.setBackMsg(new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(point), "的同学，再接再励！"});
                    testRecord.setPass(1);
                }

                msg = point < 90 ? "你的测试未通过，请再接再厉！" : "赞！VERY GOOD!记得学而时习之哦！";
                vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, point, genre));
                break;
            case "熟句测试":
                if (point < 80) {
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.TEST_CENTER_LESS_EIGHTY));
                } else if (point < 90) {
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.TEST_CENTER_EIGHTY_TO_NINETY));
                } else {
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.TEST_CENTER_NINETY_TO_HUNDRED));
                    ccieUtil.saveCcieTest(student, 5, classify, courseId, unitId, point);
                }
                if (point < 90) {
                    vo.setBackMsg(new String[]{"别气馁，已经超越了", TestPointUtil.getPercentage(point), "的同学，继续努力吧！"});
                    testRecord.setPass(2);
                } else {
                    vo.setBackMsg(new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(point), "的同学，再接再励！"});
                    testRecord.setPass(1);
                }

                msg = point < 90 ? "你的测试未通过，请再接再厉！" : "赞！VERY GOOD!记得学而时习之哦！";
                vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, point, genre));
                break;
            case "单词五维测试":
            case "例句五维测试":
                if (point < 80) {
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.FIVE_TEST_LESS_EIGHTY));
                } else {
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.FIVE_TEST_EIGHTY_TO_HUNDRED));
                    ccieUtil.saveCcieTest(student, 6, -1, courseId, unitId, point);
                }
                if (point < 90) {
                    vo.setBackMsg(new String[]{"别气馁，已经超越了", TestPointUtil.getPercentage(point), "的同学，继续努力吧！"});
                    testRecord.setPass(2);
                } else {
                    vo.setBackMsg(new String[]{"恭喜你，已经超过", TestPointUtil.getPercentage(point), "的同学，再接再励！"});
                    testRecord.setPass(1);
                }
                msg = point < 90 ? "你的测试未成功，请再接再厉！" : "赞！VERY GOOD!记得学而时习之哦！";
                vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, point, "五维测试"));
                break;
            default:
        }

        vo.setGold(testRecord.getAwardGold());
        vo.setMsg(msg);
    }

    @Override
    public ServerResponse<TestResultVo> saveTestReview(String[] correctWord, String[] errorWord, Long[] correctWordId,
                                                       Long[] errorWordId, Long[] unitId, Integer classify, Long courseId,
                                                       HttpSession session, Integer point, String genre, String testDetail) {
        if (correctWord == null && errorWord == null) {
            return ServerResponse.createByErrorMessage("参数错误！");
        }
        return saveTestCenter(correctWord, errorWord, correctWordId, errorWordId, unitId, classify, courseId, session, point, genre, testDetail);
    }

    @Override
    public ServerResponse<String> saveCapacityReview(HttpSession session, Long[] unitId, Integer classify, String word,
                                                     Long courseId, Long id, boolean isKnown) {
        Student student = getStudent(session);

        String[] correctWord = new String[0];
        String[] errorWord = new String[0];
        Long[] correctWordId = new Long[0];
        Long[] errorWordId = new Long[0];

        if (isKnown) {
            correctWord = new String[1];
            correctWordId = new Long[1];
            correctWord[0] = word;
            correctWordId[0] = id;
        } else {
            errorWord = new String[1];
            errorWordId = new Long[1];
            errorWord[0] = word;
            errorWordId[0] = id;
        }

        // 保存学习记录和慧追踪信息
        saveTestLearnAndCapacity.saveLearnAndCapacity(correctWord, errorWord, correctWordId, errorWordId, session, student, unitId, classify);
        return ServerResponse.createBySuccessMessage("学习记录保存成功！");
    }

    @Override
    public ServerResponse<Map<String, Object>> reviewCapacityPicture(Student student, String unitId, int model, String courseId, String judge) {

        Long studentId = student.getId();
        // 1. 根据随机数获取题型, 并查出一道正确的题
        // 1.1 去慧记忆中查询单词图鉴是否有需要复习的单词
        Map<String, Object> correct;
        if (judge != null && StringUtils.isNotEmpty(unitId)) {
            // 根据单元查询
            correct = capacityPictureMapper.selectNeedReviewWord(Long.valueOf(unitId), studentId, DateUtil.DateTime());
        } else {
            // 根据课程查询 课程复习模块
            correct = capacityPictureMapper.selectNeedReviewWordCourse(courseId, studentId, DateUtil.DateTime());
            if (correct != null) {
                unitId = correct.get("unit_id").toString();
            }
        }

        // 没有需要复习的了
        if (correct == null) {
            return ServerResponse.createBySuccess();
        }

        correct.put("recordpicurl", PictureUtil.getPictureByUnitId(packagePictureUrl(correct), unitId == null ? null : Long.parseLong(unitId)));

        // 记忆强度
        correct.put("memoryStrength", correct.get("memory_strength"));

        // 记忆难度
        CapacityPicture cp = new CapacityPicture();
        cp.setStudentId(studentId);
        cp.setUnitId(Long.valueOf(unitId));
        cp.setVocabularyId(Long.valueOf(correct.get("id").toString()));
        Object fault_time = correct.get("fault_time");
        Object memory_strength = correct.get("memory_strength");
        if (fault_time == null) {
            cp.setFaultTime(0);
        } else {
            cp.setFaultTime(Integer.parseInt(fault_time.toString()));
        }
        if (memory_strength == null) {
            cp.setMemoryStrength(0.0);
        } else {
            cp.setMemoryStrength(Double.parseDouble(memory_strength.toString()));
        }
        Integer hard = memoryDifficultyUtil.getMemoryDifficulty(cp, 1);
        correct.put("memoryDifficulty", hard);
        correct.put("engine", PerceiveEngineUtil.getPerceiveEngine(hard, cp.getMemoryStrength()));

        try {
            Vocabulary vocabulary = vocabularyMapper.selectByPrimaryKey(cp.getVocabularyId());
            correct.put("soundmark", vocabulary.getSoundMark());
            // 读音url
            correct.put("readUrl", baiduSpeak.getLanguagePath(correct.get("word").toString()));
        } catch (Exception e) {
            logger.error("ReviewServiceImpl -> Reviewcapacity_picture (153行)");
        }

        List<Map<String, Object>> mapErrorVocabulary;
        // 2. 从课程下随机获取三个题, 三个作为错题, 并且id不等于正确题id
        if (courseId != null) {
            mapErrorVocabulary = vocabularyMapper.getWordIdByCourse(new Long(correct.get("id").toString()), Long.valueOf(courseId), Long.parseLong(unitId));
        } else {
            //  从单元下随机获取三个题, 三个作为错题, 并且id不等于正确题id
            mapErrorVocabulary = vocabularyMapper.getWordIdByUnit(new Long(correct.get("id").toString()), unitId);
        }
        // 四道题
        mapErrorVocabulary.add(correct);
        // 随机打乱顺序
        Collections.shuffle(mapErrorVocabulary);

        // 封装四个选项
        Map<Object, Object> subject = new HashMap<>(16);
        for (Map m : mapErrorVocabulary) {

            boolean b = false;
            if (m.get("word").equals(correct.get("word"))) {
                b = true;
            }

            correct.put("type", 2);
            subject.put(m.get("word"), b);
        }
        // 把四个选项添加到correct正确答案数据中
        correct.put("subject", subject);

        // 需要复习的单词总数
        Integer count = capacityMapper.countNeedReviewByCourseIdOrUnitId(student, Long.valueOf(courseId),
                Long.valueOf(unitId), commonMethod.getTestType(0));
        correct.put("wordCount", count);
        correct.put("studyNew", false);

        return ServerResponse.createBySuccess(correct);

    }

    public static Vocabulary packagePictureUrl(Map<String, Object> correct) {
        Vocabulary wordPictureVocabulary = new Vocabulary();
        wordPictureVocabulary.setSmallPictureUrl(correct.get("smallPictureUrl") == null ? null : correct.get("smallPictureUrl").toString());
        wordPictureVocabulary.setMiddlePictureUrl(correct.get("middlePictureUrl") == null ? null : correct.get("middlePictureUrl").toString());
        wordPictureVocabulary.setHighPictureUrl(correct.get("highPictureUrl") == null ? null : correct.get("highPictureUrl").toString());
        return wordPictureVocabulary;
    }

    /**
     * 单词图鉴测试复习
     *
     * @param unitId
     * @param classify
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> testReviewWordPic(String unitId, int classify, HttpSession session, boolean pattern) {
        Student student = getStudent(session);
        Long studentId = student.getId();

        // 复习测试上一单元
        if (pattern) {
            unitId = learnMapper.getEndUnitIdByStudentId(studentId);
        }

        // 获取单元下需要复习的单词
        List<Vocabulary> list = vocabularyMapper.getMemoryWordPicAll(Long.parseLong(unitId), studentId, DateUtil.DateTime());
        // 随机获取带图片的单词, 正确答案的三倍
        List<Vocabulary> listSelect = vocabularyMapper.getWordIdByAll(list.size() * 4);

        // 分题工具类
        Map<String, Object> map = wordPictureUtil.allocationWord(list, listSelect, null, Long.valueOf(unitId));
        return ServerResponse.createBySuccess(map);
    }

    /**
     * WordPicModel
     *
     * @param courseId
     * @param unitId
     * @param select   选择: 1=已学, 2=生词, 3=熟词
     * @param classify 0=WordPicModel
     * @param isTrue
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> testWordPic(String courseId, String unitId, int select, int classify, Boolean isTrue, HttpSession session) {
        Student student = getStudent(session);
        Long studentId = student.getId();

        // true 扣除一金币
        if (isTrue) {
            // 1.查询学生剩余金币
            Integer gold = studentMapper.getSystem_gold(studentId);
            if (gold != null && gold > 0) {
                // 扣除1金币
                int state = studentMapper.updateBySystem_gold((gold - 1), studentId);
            } else {
                // 金币不足
                return ServerResponse.createBySuccess(GoldResponseCode.LESS_GOLD.getCode(), "金币不足");
            }
            // false 第一次点击五维测试  1.查询是否做过该课程的五维测试 2.如果做过返回扣除1金币提示
        } else {
            Integer judgeTest = testRecordMapper.selectJudgeTestToModel(courseId, studentId, 0, select);
            if (judgeTest != null) {
                // 已经测试过, 提示扣除金币是否测试
                return ServerResponse.createBySuccess(GoldResponseCode.NEED_REDUCE_GOLD.getCode(), "您已参加过该五维测试，再次测试需扣除1金币。");
            }
        }

        List<Vocabulary> vocabularies = null;
        // select: 1=已学, 2=生词, 3=熟词
        if (select == 1) {
            // 2.获取已学需要出的测试题
            vocabularies = capacityMapper.alreadyWordStrOne(studentId, unitId, "单词图鉴");
        } else if (select == 2) {
            // 3.获取生词需要出的测试题
            vocabularies = capacityMapper.accrueWordStrOne(studentId, unitId, "单词图鉴");
        } else {
            // 4.获取熟词需要出的测试题
            vocabularies = capacityMapper.ripeWordStrOne(studentId, unitId, "单词图鉴");
        }

        // 随机获取带图片的单词, 正确答案的三倍
        List<Vocabulary> listSelect = vocabularyMapper.getWordIdByAll(vocabularies.size() * 4);

        // 分配题型‘看词选图’、‘听音选图’、‘看图选词’ 3:3:4
        //return ServerResponse.createBySuccess(allocationWord(list, listSelect));

        // 获取课程下的单词id-单元id
        List<Long> ids = learnMapper.selectVocabularyIdByStudentIdAndCourseId(studentId, Long.valueOf(courseId), 0);
        Map<Long, Map<Long, Long>> longMapMap = unitMapper.selectIdMapByCourseIdAndWordIds(Long.valueOf(courseId), ids, studentId, classify);

        // 分题工具类
        Map<String, Object> map = wordPictureUtil.allocationWord(vocabularies, listSelect, longMapMap, Long.valueOf(unitId));

        return ServerResponse.createBySuccess(map);
    }

    @Override
    public ServerResponse<List<SentenceTranslateVo>> getSentenceReviewTest(HttpSession session, Long unitId, Integer classify, Integer type, boolean pattern) {
        Student student = getStudent(session);

        // 复习测试上一单元
        if (pattern) {
            unitId = Long.parseLong(learnMapper.getEndUnitIdByStudentId(student.getId()));
        }

        List<CapacityReview> reviews = capacityMapper.selectSentenceCapacity(student.getId(), unitId, classify);
        List<SentenceTranslateVo> vos = new ArrayList<>(reviews.size());
        if (reviews.size() > 0) {
            List<Long> sentenceIds = new ArrayList<>(reviews.size());
            reviews.forEach(review -> sentenceIds.add(review.getVocabulary_id()));
            List<Sentence> sentences = sentenceMapper.selectByIds(sentenceIds);
            vos = testResultUtil.getSentenceTestResults(sentences, classify, type);
        }
        if (vos.size() == 0) {
            return ServerResponse.createByErrorMessage("暂无需要复习的内容！");
        }
        return ServerResponse.createBySuccess(vos);
    }

    private CapacityMemory getReviewObject(Integer classify) {
        CapacityMemory cm;
        switch (classify) {
            case 0:
                cm = new CapacityPicture();
                break;
            case 1:
                cm = new CapacityMemory();
                break;
            case 2:
                cm = new CapacityListen();
                break;
            case 3:
                cm = new CapacityWrite();
                break;
            default:
                cm = new CapacityMemory();
        }
        return cm;
    }

    public static List<Map<String, Object>> packageLastLoginLearnWordIds(List<Learn> learns) {
        List<Map<String, Object>> maps = new ArrayList<>(learns.size());
        learns.forEach(learn -> {
            Map<String, Object> map = new HashMap<>(16);
            map.put("unitId", learn.getUnitId());
            map.put("wordId", learn.getVocabularyId());
            map.put("courseId", learn.getCourseId());
            maps.add(map);
        });
        return maps;
    }

    @Override
    public ServerResponse<Map<String, Object>> getWordReview(HttpSession session, Integer classify) {
        Student student = getStudent(session);
        // 上次登录期间学生的单词学习信息
        Duration duration = durationMapper.selectLastLoginDuration(student.getId());
        if (duration != null) {
            List<Learn> learns = learnMapper.selectLastLoginStudy(student.getId(), duration.getLoginTime(), duration.getLoginOutTime(), classify);
            if (learns.size() > 0) {
                return packageWordReviewResult(classify, student, learns);
            }
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<Map<String, Object>> getAllCapacityReview(HttpSession session, Integer classify) {
        Student student = getStudent(session);
        List<Learn> learns = learnMapper.selectAllCapacityReview(student.getId(), classify);
        if (learns.size() > 0) {
            return packageWordReviewResult(classify, student, learns);
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse getAllSentenceReview(HttpSession session, Integer classify) {
        Student student = getStudent(session);
        CapacityReview reviews = capacityMapper.selectSentenceCapacitys(student.getId(), classify);
        //获取总复习数量
        Integer count = capacityMapper.selSentenceCountCapacitys(student.getId(), classify);
        if (reviews == null) {
            return ServerResponse.createByErrorMessage("暂无需要复习的内容！");
        } else {
            // 转换类型
            String classifyString = commonMethod.getTestType(classify);
            if ("例句翻译".equals(classifyString)) {
                SentenceTranslate sentenceTranslate = sentenceTranslateMapper.selectByPrimaryKey(reviews.getId());
                return sentenceService.returnGoldWord(sentenceTranslate, 1L, false, count.longValue(), null, null, 1);
            } else if ("例句听力".equals(classifyString)) {
                SentenceListen sentenceListen = sentenceListenMapper.selectByPrimaryKey(reviews.getId());
                return sentenceService.returnGoldWord(null, 1L, false, count.longValue(), sentenceListen, null, 1);
            }
        }
        return ServerResponse.createBySuccess();
    }

    private ServerResponse<Map<String, Object>> packageWordReviewResult(Integer classify, Student student, List<Learn> learns) {
        // 存储单词id及单元
        List<Map<String, Object>> maps = packageLastLoginLearnWordIds(learns);

        Map<String, Object> map = capacityMapper.selectLastLoginNeedReview(student.getId(), maps, classify);
        int count = capacityMapper.countLastLoginNeedReview(student.getId(), maps, classify);

        // 没有需要复习的单词
        if (map == null || map.size() == 0) {
            return ServerResponse.createBySuccess();
        }

        Vocabulary vocabulary = packageWordReviewContent(map, count, student.getId(), classify);

        // 单词图鉴相关内容
        if (classify == 0) {
            map.put("recordpicurl", PictureUtil.getPictureByCourseId(vocabulary, map.get("course_id") == null ? null : Long.parseLong(map.get("course_id").toString())));
            List<Map<String, Object>> mapErrorVocabulary = vocabularyMapper.getWordIdByUnit(new Long(map.get("id").toString()), map.get("unit_id").toString());
            if (mapErrorVocabulary.size() < 3) {
                List<Map<String, Object>> otherErrorVocabulary = vocabularyMapper.selectPictureWordFromLearned(student.getId(), 3 - mapErrorVocabulary.size());
                mapErrorVocabulary.addAll(otherErrorVocabulary);
            }
            // 四道题
            mapErrorVocabulary.add(map);
            // 随机打乱顺序
            Collections.shuffle(mapErrorVocabulary);

            // 封装四个选项
            Map<Object, Object> subject = new HashMap<>(16);
            for (Map m : mapErrorVocabulary) {
                boolean b = false;
                if (m.get("word").equals(map.get("word"))) {
                    b = true;
                }
                subject.put(m.get("word"), b);
            }
            map.put("type", 2);
            // 把四个选项添加到correct正确答案数据中
            map.put("subject", subject);
        } else if (classify == 1) {
            map.put("wordChineseList", this.getChinese(Long.parseLong(map.get("unit_id").toString()), vocabulary == null ? null : vocabulary.getId(), map.get("wordChinese").toString()));
            map.put("recordpicurl", PictureUtil.getPictureByCourseId(vocabulary, map.get("course_id") == null ? null : Long.parseLong(map.get("course_id").toString())));
        }
        return ServerResponse.createBySuccess(map);
    }

    /**
     * 封装单词智能复习响应内容
     *
     * @param map
     * @param count
     * @param studentId
     * @param classify
     * @return
     */
    private Vocabulary packageWordReviewContent(Map<String, Object> map, int count, Long studentId, Integer classify) {
        Vocabulary vocabulary = vocabularyMapper.selectById((Serializable) map.get("id"));

        // 记忆难度
        Object faultTime = map.get("fault_time");
        Object memoryStrength = map.get("memory_strength");
        CapacityMemory cm = getReviewObject(classify);
        cm.setFaultTime(faultTime == null ? 0 : (Integer) faultTime);
        cm.setMemoryStrength(memoryStrength == null ? 0.0 : (Integer) memoryStrength);
        cm.setStudentId(studentId);
        cm.setUnitId(map.get("unit_id") == null ? null : (Long) map.get("unit_id"));
        cm.setVocabularyId(map.get("id") == null ? null : (Long) map.get("id"));
        int hard = memoryDifficultyUtil.getMemoryDifficulty(cm, 1);
        map.put("memoryDifficulty", hard);

        map.put("soundmark", vocabulary == null ? null : vocabulary.getSoundMark());
        // 读音url
        map.put("readUrl", baiduSpeak.getLanguagePath(map.get("word").toString()));

        map.put("wordCount", count);
        map.put("studyNew", false);
        map.put("engine", PerceiveEngineUtil.getPerceiveEngine(hard, map.get("memoryStrength") == null ? 0 : Double.parseDouble(map.get("memoryStrength").toString())));
        map.put("plan", 0);
        return vocabulary;
    }

    /**
     * 保存测试记录和奖励信息
     *
     * @param quantity
     * @param errorCount
     * @param rightCount
     * @param classify
     * @param session
     * @param student
     * @param point
     * @param genre
     * @param courseId
     * @return 学生获得的金币数
     */
    private TestRecord saveTestRecord(int quantity, int errorCount, int rightCount, Integer classify, HttpSession
            session, Student student, Integer point, String genre, Long courseId, Long[] unitIds) {
        Long unitId = (unitIds == null || unitIds.length == 0) ? null : unitIds[0];
        String studyModel = commonMethod.getTestType(classify);
        StringBuilder msg = new StringBuilder();
        long stuId = student.getId();
        TestRecord testRecord = new TestRecord();
        Object timeSession = session.getAttribute(TimeConstant.BEGIN_START_TIME);
        testRecord.setTestStartTime(timeSession == null ? new Date() : (Date) timeSession);
        testRecord.setTestEndTime(new Date());
        testRecord.setStudyModel(studyModel);
        testRecord.setStudentId(stuId);
        testRecord.setRightCount(rightCount);
        testRecord.setQuantity(quantity);
        testRecord.setPoint(point);
        testRecord.setGenre(genre);
        testRecord.setErrorCount(errorCount);
        testRecord.setCourseId(courseId);
        testRecord.setUnitId(unitId);

        if ("已学测试".equals(genre) || "生词测试".equals(genre) || "熟词测试".equals(genre) || genre.contains("五维测试")
                || genre.contains("生句测试") || genre.contains("熟句测试")) {
            if (point < 80) {
                testRecord.setExplain("你的测试未通过，请再接再厉！");
            } else {
                testRecord.setExplain("赞！VERY GOOD!记得学而时习之哦！");
            }
        }

        if (genre.contains("五维测试")) {
            // 判断学生之前是否已经在当前课程有过“五维测试”记录
            List<TestRecord> testRecords = testRecordMapper.selectMaxPointByStudyModel(stuId, courseId, genre, studyModel);
            if (testRecords.size() == 0) {
                initTestCenterBetterCount(point, testRecord);
                msg.append("id 为 ").append(stuId).append(" 的学生在 测试中心 -> ");
                decideFiveD(point, genre, student, msg, studyModel, testRecord);
            } else {
                TestRecord preTestRecord = testRecords.get(0);
                if (preTestRecord.getPoint() < point) {
                    if (point >= 80) {
                        testRecord.setBetterCount(preTestRecord.getBetterCount() + 1);
                    } else {
                        testRecord.setBetterCount(preTestRecord.getBetterCount());
                    }
                    msg.append("id 为 ").append(stuId).append(" 的学生在 测试中心 -> ");
                    decideFiveD(point, genre, student, msg, studyModel, testRecord);
                }
            }
        } else if ("已学测试".equals(genre) || "生词测试".equals(genre) || "熟词测试".equals(genre) || "熟句测试".equals(genre) || "生句测试".equals(genre)) {
            // 判断学生之前是否已经在当前课程有过“已学测试”或者“生词测试”或者“熟词测试”
            List<TestRecord> testRecords = testRecordMapper.selectMaxPointByStudyModel(stuId, courseId, genre, studyModel);
            if (testRecords.size() == 0) {
                initTestCenterBetterCount(point, testRecord);
                msg.append("id 为 ").append(stuId).append(" 的学生在 测试中心 -> ");
                decideLearnedUnKnown(point, genre, student, msg, studyModel, testRecord);
            } else {
                TestRecord preTestRecord = testRecords.get(0);
                if (preTestRecord.getPoint() < point) {
                    if (point >= 80) {
                        testRecord.setBetterCount(preTestRecord.getBetterCount() + 1);
                    } else {
                        testRecord.setBetterCount(preTestRecord.getBetterCount());
                    }
                    msg.append("id 为 ").append(stuId).append(" 的学生在 测试中心 -> ");
                    decideLearnedUnKnown(point, genre, student, msg, studyModel, testRecord);
                }
            }
        }

        studentMapper.updateByPrimaryKeySelective(student);
        session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        if (msg.length() > 0) {
            logger.info(msg.toString());
            RunLog runLog = new RunLog(stuId, 4, msg.toString(), new Date());
            runLog.setUnitId(student.getUnitId());
            runLog.setCourseId(student.getCourseId());
            runLogMapper.insert(runLog);
        }
        return testRecord;
    }

    /**
     * 初始化测试中心中测试相关的超过历史最高分测试
     *
     * @param point
     * @param testRecord
     */
    private void initTestCenterBetterCount(Integer point, TestRecord testRecord) {
        if (point >= 80) {
            testRecord.setBetterCount(1);
        } else {
            testRecord.setBetterCount(0);
        }
    }

    /**
     * 判断 已学测试 和 生词测试 或者“熟词测试” 奖励金
     *
     * @param point
     * @param genre
     * @param student
     * @param msg
     * @param studyModel
     * @param testRecord
     */
    private void decideLearnedUnKnown(Integer point, String genre, Student student, StringBuilder msg, String
            studyModel, TestRecord testRecord) {
        msg.append(genre).append(studyModel);
        int gold = 0;
        if (point < 90 && point >= 80) {
            // 奖励2金币
            gold = testRecord.getBetterCount() * TestAwardGoldConstant.TEST_CENTER_EIGHTY_TO_NINETY;
        } else if (point >= 90 && point <= 100) {
            // 奖励5枚金币
            gold = testRecord.getBetterCount() * TestAwardGoldConstant.TEST_CENTER_NINETY_TO_FULL;
        } else {
            testRecord.setAwardGold(0);
        }
        int addGold = testGoldUtil.addGold(student, gold);
        if (student.getBonusExpires() != null && System.currentTimeMillis() < student.getBonusExpires().getTime()) {
            Double doubleGold = gold * 0.2;
            addGold = doubleGold.intValue() + addGold;
        }
        student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), addGold));
        testRecord.setAwardGold(addGold);
        msg.append(" 中获得#").append(addGold).append("#金币。");
    }

    /**
     * 判断五维测试奖励金
     *
     * @param point
     * @param genre
     * @param student
     * @param msg
     * @param studyModel
     * @param testRecord
     */
    private void decideFiveD(Integer point, String genre, Student student, StringBuilder msg, String
            studyModel, TestRecord testRecord) {
        msg.append(genre).append(studyModel);
        int gold = 0;
        if (point < 90 && point >= 80) {
            // 奖励10金币
            gold = testRecord.getBetterCount() * TestAwardGoldConstant.FIVE_TEST_EIGHTY_TO_NINETY;

        } else if (point >= 90 && point <= 100) {
            // 奖励20枚金币
            gold = testRecord.getBetterCount() * TestAwardGoldConstant.FIVE_TEST_NINETY_TO_FULL;
        }
        testRecord.setAwardGold(gold);
        int addGold = testGoldUtil.addGold(student, gold);
        student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), addGold));
        msg.append(" 中获得#").append(addGold).append("#金币。");
    }

}
