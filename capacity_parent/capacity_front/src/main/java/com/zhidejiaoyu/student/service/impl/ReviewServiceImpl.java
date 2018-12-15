package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.Vo.student.SentenceTranslateVo;
import com.zhidejiaoyu.common.Vo.student.testCenter.TestCenterVo;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.study.MemoryDifficultyUtil;
import com.zhidejiaoyu.common.study.WordPictureUtil;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.GoldResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.testUtil.TestResult;
import com.zhidejiaoyu.common.utils.testUtil.TestResultUtil;
import com.zhidejiaoyu.student.common.SaveTestLearnAndCapacity;
import com.zhidejiaoyu.student.constant.PetMP3Constant;
import com.zhidejiaoyu.student.constant.TestAwardGoldConstant;
import com.zhidejiaoyu.student.service.ReviewService;
import com.zhidejiaoyu.student.utils.CcieUtil;
import com.zhidejiaoyu.student.utils.CountMyGoldUtil;
import com.zhidejiaoyu.student.utils.PetSayUtil;
import com.zhidejiaoyu.student.utils.PetUrlUtil;
import com.zhidejiaoyu.student.vo.TestResultVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Value("${ftp.prefix}")
    private String ftpPrefix;

    @Autowired
    private TestResultUtil testResultUtil;

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
    private CourseMapper courseMapper;

    @Autowired
    private SaveTestLearnAndCapacity saveTestLearnAndCapacity;

    @Autowired
    private RunLogMapper runLogMapper;

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
    private CountMyGoldUtil countMyGoldUtil;

    @Autowired
    private UnitSentenceMapper unitSentenceMapper;

    @Autowired
    private DurationMapper durationMapper;

    @Autowired
    private StudentUnitMapper studentUnitMapper;

    @Autowired
    private CcieUtil ccieUtil;

    @Override
    public Map<String, Integer> testReview(String unit_id, String student_id) {
        // 封装返回的数据 - 智能记忆智能复习数量
        Map<String, Integer> map = new HashMap<String, Integer>();

        // 当前时间
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = s.format(new Date());

        CapacityReview cr = new CapacityReview();
        cr.setUnit_id(Long.valueOf(unit_id));
        cr.setStudent_id(Long.valueOf(student_id));
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
    public ServerResponse<Object> testCapacityReview(String unit_id, int classify, HttpSession session, boolean pattern) {
        // 封装测试单词
        List<Vocabulary> vocabularies;

        // 获取当前学生信息
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        // 学生id
        Long id = student.getId();

        // 复习测试上一单元
        if(pattern){
            unit_id = learnMapper.getEndUnitIdByStudentId(id);
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
            ca.setUnit_id(Long.valueOf(unit_id));
            ca.setClassify(classify + "");
            ca.setPush(dateTime);
            vocabularies = capacityMapper.selectCapacity(ca);

            // 处理结果
            List<TestResult> testResults = testResultUtil.getWordTestesForUnit(type, vocabularies.size(), vocabularies, Long.valueOf(unit_id));
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
			ca.setStudent_id(id);
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
    public Map<String, Object> ReviewCapacity_memory(Long id, String unit_id, int classify, String course_id, boolean pattern) {

        // 复习上单元
        if(pattern){
            unit_id = learnMapper.getEndUnitIdByStudentId(id);
        }

        Map<String, Object> map = new HashMap<>(16);

        CapacityReview ca = new CapacityReview();
        // 查询条件1:学生id
        ca.setStudent_id(id);

        String model = null;
        if (classify == 1) {
            model = "慧记忆";
        } else if (classify == 2) {
            model = "慧听写";
        } else if (classify == 3) {
            model = "慧默写";
        }

        // 智能复习, 根据单元查询
        if (StringUtils.isNotBlank(unit_id) && StringUtils.isBlank(course_id)) {
            // 查询条件2.1:单元id
            ca.setUnit_id(Long.valueOf(unit_id));

            // 该单元已学单词  ./
            Long count_ = learnMapper.learnCountWord(id, Integer.parseInt(unit_id), model);
            map.put("plan", count_);

            // count单元表单词有多少个
    		Integer count = unitMapper.countWordByUnitid(unit_id);
            map.put("wordCount", count);
        }
        // 任务课程-复习, 根据课程查询
        if (StringUtils.isNotBlank(course_id)) {
            // 查询条件2.2:课程id
            ca.setCourse_id(Long.valueOf(course_id));

            // 根据课程id获取课程名
            String courseName = courseMapper.selectByCourseName(course_id);
            // 课程id

            // 课程名
            map.put("courseName", courseName);

            // 该课程已学单词
            Long count_ = learnMapper.learnCourseCountWord(id, course_id, model);
            map.put("plan", count_);
            // 该课程一共多少单词
            Integer count = unitMapper.countWordByCourse(course_id);
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
        if(StringUtils.isBlank(unit_id)){
            map.put("unitId", vo.getUnit_id());
            unit_id = vo.getUnit_id().toString();
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
            cm.setStudentId(id);
            cm.setUnitId(Long.valueOf(unit_id));
            cm.setVocabularyId(vo.getVocabulary_id());
            cm.setMemoryStrength(vo.getMemory_strength());
            cm.setFaultTime(vo.getFault_time());
            Integer hard = memoryDifficultyUtil.getMemoryDifficulty(cm, 1);
            map.put("memoryDifficulty", hard);
        }

        return map;
    }

    @Override
    public Object ReviewSentence_listen(Long stuId, String unit_id, int classify, String course_id, boolean pattern, Integer type) {

        // 复习上单元
        if(pattern){
            unit_id = learnMapper.getEndUnitIdByStudentId(stuId);
        }

        Map<String, Object> map = new HashMap<>(16);
        // 当前时间
        String dateTime = DateUtil.DateTime();

        Random rand = new Random();
//        int nextInt = rand.nextInt(2)+1;
        int nextInt = 1;
        // 例句id
        CapacityReview vo = null;

        // 1.去记忆追踪中获取需要复习的例句
        // 智能复习 (根据单元查询)
        if (StringUtils.isNotBlank(unit_id) && StringUtils.isBlank(course_id)) {
            if (classify == 4) {
                // 例句听力
                vo = capacityMapper.ReviewSentence_listen(stuId, unit_id, dateTime);
            } else if (classify == 5) {
                // 例句翻译 sentence_translate
                vo = capacityMapper.ReviewSentence_translate(stuId, unit_id, dateTime);
            } else if (classify == 6) {
                // 例句默写 sentence_write
                vo = capacityMapper.ReviewSentence_write(stuId, unit_id, dateTime);
            }

            // 当前单元下一共有多少例句/.
            Long countWord = unitSentenceMapper.selectSentenceCountByUnitId(Long.valueOf(unit_id));
            map.put("sentenceCount", countWord);

            // 当前单元下已学./
            Integer sum = learnMapper.selectNumberByStudentId(stuId, Integer.parseInt(unit_id), classify);
            map.put("plan", sum);
        }
        // 任务课程-复习 (根据课程查询)
        if (StringUtils.isNotBlank(course_id)) {
            if (classify == 4) {
                // 例句听力
                vo = capacityMapper.ReviewSentence_listenCourseId(stuId, course_id, dateTime);
            } else if (classify == 5) {
                // 例句翻译 sentence_translate
                vo = capacityMapper.Reviewsentence_translateCourseId(stuId, course_id, dateTime);
            } else if (classify == 6) {
                // 例句默写 sentence_write
                vo = capacityMapper.ReviewSentence_writeCourseId(stuId, course_id, dateTime);
            }

            // 根据课程id获取课程名
            String courseName = courseMapper.selectByCourseName(course_id);
            map.put("id", course_id);
            map.put("courseName", courseName);

            // 该课程一共多少例句
            Long count_ = unitMapper.countSentenceByCourse(course_id);
            map.put("sentenceCount", count_); // /.
            // 该课程已学例句w
            Integer count = learnMapper.learnCourseCountSentence(stuId, classify, Long.valueOf(course_id));
            map.put("plan", count); // ./
        }

        if (vo == null) {
            logger.info("courseid:{}->unitId:{} 下没有需要复习的句型",course_id, unit_id);
            return null;
        }
        // 2.通过记忆追踪中的例句id-获取例句信息 (vo.getVocabulary_id()是例句id)
        Sentence sentence = sentenceMapper.selectByPrimaryKey(vo.getVocabulary_id());

        String english = sentence.getCentreExample().replace("#", " "); // 分割例句
        String chinese = sentence.getCentreTranslate().replace("*", ""); // 分割翻译

        // 例句id
        map.put("id", sentence.getId());
        // 例句id
        if(unit_id == null){ // 任务课程复习
            map.put("unitId", vo.getUnit_id());
        }else{// 智能复习
            map.put("unitId", unit_id);
        }
        // 例句读音
        map.put("readUrl", baiduSpeak.getLanguagePath(english));
        // 例句翻译
        map.put("word_Chinese", chinese);
        // 例句英文原文
        map.put("sentence", english);
        // 记忆强度
        map.put("memoryStrength", vo.getMemory_strength());
        //if (classify != 6) {
        if (type == 2) {
            if(nextInt == 1){
                // 例句英文乱序集合
                map.put("order", commonMethod.getOrderEnglishList(sentence.getCentreExample(), sentence.getExampleDisturb()));
                // 例句英文正序集合，含有标点
                map.put("rateList", commonMethod.getEnglishList(sentence.getCentreExample()));
            }
            if(nextInt == 2){
                // 例句翻译中含有乱序的中文解释和干扰项
                map.put("order", commonMethod.getOrderChineseList(sentence.getCentreTranslate(), sentence.getTranslateDisturb()));
                // 正确顺序翻译
                map.put("rateList", commonMethod.getChineseList(sentence.getCentreTranslate()));
            }
        } else {
            if(nextInt == 1){
                // 例句英文乱序集合
                map.put("order", commonMethod.getOrderEnglishList(sentence.getCentreExample(), null));
                // 例句英文正序集合，含有标点
                map.put("rateList", commonMethod.getEnglishList(sentence.getCentreExample()));
            }
            if(nextInt == 2){
                // 例句翻译中含有乱序的中文解释和干扰项
                map.put("order", commonMethod.getOrderChineseList(sentence.getCentreTranslate(), null));
                // 正确顺序翻译
                map.put("rateList", commonMethod.getChineseList(sentence.getCentreTranslate()));
            }
        }


        return map;
    }

    @Override
    public ServerResponse<List<TestCenterVo>> testCentreIndex(Long courseId, HttpSession session) {
        // 获取当前学生信息
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Long studentId = student.getId();

        List<Long> courseIds;
        if (courseId == 0) {
            // 学生所有课程id
            courseIds = studentUnitMapper.selectCourseIdsByStudentId(studentId);
        } else {
            courseIds = new ArrayList<>();
            courseIds.add(courseId);
        }

        List<TestCenterVo> testCenterVos = new ArrayList<>();
        TestCenterVo testCenterVo;

        // 单词模块
        for (int i = 0; i <= 6; i++) {
            testCenterVo = new TestCenterVo();
            String classify = commonMethod.getTestType(i);
            // 已学
            Integer learnCount = capacityMapper.countAlreadyStudyWord(studentId, courseIds, classify, i);
            // 生词
            Integer unknownCount = capacityMapper.countAccrueWord(studentId, courseIds, classify, i);
            // 熟词
            Integer knownCount = capacityMapper.countRipeWord(studentId, courseIds, classify, i);

            testCenterVo.setClassify(i);
            testCenterVo.setAlreadyStudy(learnCount);
            testCenterVo.setAccrue(unknownCount);
            testCenterVo.setRipe(knownCount);

            testCenterVos.add(testCenterVo);
        }

        return ServerResponse.createBySuccess(testCenterVos);
    }

    /**
     * 测试中心题
     */
    @Override
    public ServerResponse<Object> testcentre(String course_id, String unitId, int select, int classify, Boolean isTrue, HttpSession session) {
        // 获取当前学生信息
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        // 学生id
        Long student_id = student.getId();

        // true 扣除一金币
        if(isTrue){
            // 1.查询学生剩余金币
            Integer gold = studentMapper.getSystem_gold(student_id);
            if(gold != null && gold>0){
                // 扣除1金币
                int state = studentMapper.updateBySystem_gold((gold-1), student_id);
            }else{
                // 金币不足
                return ServerResponse.createBySuccess(GoldResponseCode.LESS_GOLD.getCode(), "金币不足");
            }
            // false 第一次点击五维测试  1.查询是否做过该课程的五维测试 2.如果做过返回扣除1金币提示
        }else{
            Integer judgeTest = testRecordMapper.selectJudgeTestToModel(course_id, student_id, classify, select);
            if(judgeTest != null){
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
                vocabularies = capacityMapper.alreadyWordStrOne(student_id, unitId, classifyStr);
            } else if (select == 2) {
                // 3.获取生词需要出的测试题
                vocabularies = capacityMapper.accrueWordStrOne(student_id, unitId, classifyStr);
            } else if (select == 3) {
                // 4.获取熟词需要出的测试题
                vocabularies = capacityMapper.ripeWordStrOne(student_id, unitId, classifyStr);
            }

            // 处理结果
            List<TestResult> testResults = testResultUtil.getWordTestesForCourse(type, vocabularies.size(), vocabularies, Long.valueOf(course_id));

            // 获取课程下的单词id-单元id
            List<Long> ids = learnMapper.selectVocabularyIdByStudentIdAndCourseId(student_id, Long.valueOf(course_id), 1);
            Map<Long, Map<Long, Long>> longMapMap = unitMapper.selectIdMapByCourseIdAndWordIds(Long.valueOf(course_id), ids, student_id, classify);

            // 处理结果添加单元id
            for(TestResult reList: testResults){
                // 单词id
                Long wordId = reList.getId();
                // 封装单元id
                if(longMapMap.containsKey(wordId)){
                    reList.setUnitId(longMapMap.get(wordId).get("unitId"));
                }
            }
            return ServerResponse.createBySuccess(testResults);

            // 2,3模块:最多20道题?  - 目前没做题量限制
        } else if (classify == 2 || classify == 3) {
            // select: 1=已学, 2=生词, 3=熟词
            if (select == 1) {
                // 2.获取已学需要出的测试题
                vocabularies = capacityMapper.alreadyWordStr(student_id, unitId, classifyStr);
            } else if (select == 2) {
                // 3.获取生词需要出的测试题
                vocabularies = capacityMapper.accrueWordStr(student_id, unitId, classifyStr);
            } else if (select == 3) {
                // 4.获取熟词需要出的测试题
                vocabularies = capacityMapper.ripeWordStr(student_id, unitId, classifyStr);
            }

            // 慧听写 封装读音

                // 封装所有听写题
                List<Map<String, Object>> li = new ArrayList<>();
                for (Vocabulary vo : vocabularies) {
                    // 封装一条听力题
                    Map map = new HashMap();
                    map.put("id",vo.getId()); // 单词id
                    map.put("word", vo.getWord()); // 单词
                    map.put("unitId", unitId); // 单元id

                    if(classify == 2) {
                        try {
                            // 单词读音
                            map.put("readUrl", baiduSpeak.getLanguagePath(vo.getWord()));
                        } catch (Exception e) {
                            logger.error("获取单词" + vo.getWord() + "读音报错!");
                        }
                    }else{
                        map.put("chinese", vo.getWordChinese()); // 单词翻译
                    }

                    li.add(map);
                }

                return ServerResponse.createBySuccess(li);
            //return ServerResponse.createBySuccess(vocabularies);
        }

        return null;
    }

    @Override
    public List<SentenceTranslateVo> testcentreSentence(String unitId, int select, int classify, HttpSession session, Integer type) {
        // 返回结果集
        Map<String, Object> map = new HashMap<String, Object>();
        // 获取当前学生信息
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        // 学生id
        Long student_id = student.getId();
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
            sentences = capacityMapper.centreReviewSentence_listen(student_id, unitId, classifyStr);
            // 生词
        } else if (select == 2) {
            sentences = capacityMapper.accrueCentreReviewSentence_listen(student_id, unitId, classifyStr);
            // 熟词
        } else if (select == 3) {
            sentences = capacityMapper.ripeCentreReviewSentence_listen(student_id, unitId, classifyStr);
        }

        return testResultUtil.getSentenceTestResults(sentences, classify, type);
    }

    /**
     * 根据课程id和学习id查询learn表
     */
    @Override
    public ServerResponse<Object> testeffect(String course_id, HttpSession session) {
        // 获取当前学生信息
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        // 学生id
        Long student_id = student.getId();

        // 1.题类型
        String[] type = {"英译汉", "汉译英", "听力理解"};

        //
        List<Vocabulary> vocabularies = capacityMapper.testeffect(course_id, student_id);

        // 处理结果
        List<TestResult> testResults = testResultUtil.getWordTestesForCourse(type, vocabularies.size(), vocabularies, Long.valueOf(course_id));
        return ServerResponse.createBySuccess(testResults);
    }

    /**
     * 五维测试
     */
    @Override
    public ServerResponse<Object> fiveDimensionTest(String course_id, boolean isTrue, HttpSession session) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Long studentId = student.getId();

        // true 扣除一金币
        if(isTrue){
            // 1.查询学生剩余金币
            Integer gold = studentMapper.getSystem_gold(studentId);
            if(gold != null && gold>0){
                // 扣除1金币
                int state = studentMapper.updateBySystem_gold((gold-1), studentId);
            }else{
                // 金币不足
                return ServerResponse.createBySuccess(GoldResponseCode.LESS_GOLD.getCode(), "金币不足");
            }
            // false 第一次点击五维测试  1.查询是否做过该课程的五维测试 2.如果做过返回扣除1金币提示
        }else{
            Integer judgeTest = testRecordMapper.selectJudgeTest(course_id, studentId, "单词五维测试");
            if(judgeTest != null){
                // 已经测试过, 提示扣除金币是否测试
                return ServerResponse.createBySuccess(GoldResponseCode.NEED_REDUCE_GOLD.getCode(), "您已参加过该五维测试，再次测试需扣除1金币。");
            }
        }

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        
        // 查询课程下边一共有多少单词
        Integer countWord = unitMapper.countWordByCourse(course_id);
        if(countWord==0){
            return ServerResponse.createByError(500,"该课程下没有单词!");
        }
        
        if(countWord>50) {
        	countWord = 50;
        }
        
        // 平均分配题量 - b:英译汉,汉译英,听力理解需要的题数量, c:听力,默写需要的题数量
        int count = countWord;// 总题量
        int aa = count/5; // 分五份
        int b = aa*3; // 1,2,3
        int c = aa*2; // 4听力,5默写
        // 获取count/5剩余的数量, 加到b,c中
        int countBC = 0; 
    	if(count > (b+c)) {
    		countBC = count - (b+c);
    	}
    	if(countBC == 4) {
    		b+=3;
    		c+=1;
    	}else if(countBC < 4 && countBC >0) {
    		b+=countBC;
    	}
        
        // 从课程中查出*个单词 - 英译汉,汉译英,听力理解 limit 0,b
        //List<Vocabulary> vocabularies = capacityMapper.fiveDimensionTest(course_id, b);

        // 从课程中查出*个单词 - 听写,默写 limit b,c
        //List<Vocabulary> vocabulariesTwo = capacityMapper.fiveDimensionTestTwo(course_id, b, c);

        // 1. 获取课程下的所有打乱顺序的单词
        List<Vocabulary> list = capacityMapper.fiveDimensionTestAll(course_id);

    	// 2.1 从课程中查出*个单词 - 英译汉,汉译英,听力理解 limit 0,b
        List<Vocabulary> vocabularies = list.subList(0, b);

        // 2.2 从课程中查出*个单词 - 听写,默写 limit b,c
        List<Vocabulary> vocabulariesTwo = list.subList(b, b + c);
                // 1.英译汉 2.汉译英 3.听力理解
        String[] type_a = {"英译汉"};
        String[] type_b = {"汉译英"};
        String[] type_c = {"听力理解"};
        List<TestResult> testResults_a = testResultUtil.getWordTestesForCourse(type_a, vocabularies.subList(0, vocabularies.size()/3).size(),
                vocabularies.subList(0,vocabularies.size()/3), Long.valueOf(course_id));
        List<TestResult> testResults_b = testResultUtil.getWordTestesForCourse(type_b, vocabularies.subList(vocabularies.size()/3, (int)BigDecimalUtil.div((double)vocabularies.size(),1.5,0)).size(),
                vocabularies.subList(vocabularies.size()/3, (int)BigDecimalUtil.div((double)vocabularies.size(),1.5,0)), Long.valueOf(course_id));
        List<TestResult> testResults_c = testResultUtil.getWordTestesForCourse(type_c, vocabularies.subList((int)BigDecimalUtil.div((double)vocabularies.size(),1.5,0), vocabularies.size()).size(),
                vocabularies.subList((int)BigDecimalUtil.div((double)vocabularies.size(),1.5,0), vocabularies.size()), Long.valueOf(course_id));

        result.put("testResults_a", testResults_a);
        result.put("testResults_b", testResults_b);
        result.put("testResults_c", testResults_c);

        // 4.听写
        List<Map<String, Object>> hearList = new ArrayList<>();
        // 5.默写
        List<Map<String, Object>> writeList = new ArrayList<>();
        
        int a = 0;
        for (Vocabulary vo : vocabulariesTwo){
            // 用于封装一道题
            Map m = new LinkedHashMap();
            
            // 听写
            if (a < (vocabulariesTwo.size()/2)) {
                m.put("type", "听写");
                m.put("id", vo.getId());
                m.put("word", vo.getWord());
                try {
                    // 单词读音
                    m.put("readUrl", baiduSpeak.getLanguagePath(vo.getWord()));
                }catch (Exception e){
                    logger.error("获取单词"+vo.getWord()+"读音报错!");
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
    public ServerResponse<TestResultVo> saveTestCenter(String[] correctWord, String[] errorWord, Integer[] correctWordId,
                                                       Integer[] errorWordId, Long[] unitId, Integer classify, Long courseId,
                                                       HttpSession session, Integer point, String genre) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        TestResultVo vo = new TestResultVo();

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

        // 把已学测试,生词测试,熟词测试保存慧追踪中
        if (!"单词五维测试".equals(genre) && !"例句五维测试".equals(genre)) {
            // 保存学习记录和慧追踪信息
            ServerResponse<String> serverResponse = saveTestLearnAndCapacity.saveLearnAndCapacity(correctWord, errorWord,
                    correctWordId, errorWordId, session, student, unitId, classify);
            if (serverResponse != null) {
                throw new RuntimeException("无当前模块的学习记录");
            }
        }

        int gold = this.saveTestRecord(quantity, errorCount, rightCount, classify, session, student, point, genre, courseId);
        // 封装提示语
        packagePetSay(gold, point, student, vo, genre);
        countMyGoldUtil.countMyGold(student);
        return ServerResponse.createBySuccess(vo);
    }

    /**
     * 已学测试, 生词测试, 熟词测试, 五维测试
     *
     * @param gold
     * @param point
     * @param student
     * @param vo
     * @param genre
     */
    private void packagePetSay(Integer gold, Integer point, Student student, TestResultVo vo, String genre) {
        String msg = "";
        String petName = student.getPetName();
        switch (genre) {
            case "测试复习":
                if (point < 80) {
                    msg = "闯关失败，请再接再厉！";
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.CAPACITY_REVIEW_LESS_EIGHTY));
                } else {
                    msg = "真让人刮目相看！继续学习吧！";
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.CAPACITY_REVIEW_EIGHTY_TO_HUNDRED));
                }
                vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, point, "智能复习测试"));
                break;
            case "已学测试":
            case "生词测试":
            case "熟词测试":
                if (point < 80) {
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.TEST_CENTER_LESS_EIGHTY));
                } else if (point < 90) {
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.TEST_CENTER_EIGHTY_TO_NINETY));
                } else {
                    vo.setPetSay(petSayUtil.getMP3Url(petName, PetMP3Constant.TEST_CENTER_NINETY_TO_HUNDRED));
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
                }
                msg = point < 90 ? "你的测试未成功，请再接再厉！" : "赞！VERY GOOD!记得学而时习之哦！";
                vo.setPetUrl(PetUrlUtil.getTestPetUrl(student, point, "五维测试"));
                break;
            default:
        }

        vo.setGold(gold);
        vo.setMsg(msg);
    }

    @Override
    public ServerResponse<TestResultVo> saveTestReview(String[] correctWord, String[] errorWord, Integer[] correctWordId,
                                                       Integer[] errorWordId, Long[] unitId, Integer classify, Long courseId,
                                                       HttpSession session, Integer point, String genre) {
        if (correctWord == null && errorWord == null) {
            return ServerResponse.createByErrorMessage("参数错误！");
        }
        return saveTestCenter(correctWord, errorWord, correctWordId, errorWordId, unitId, classify, courseId, session, point, genre);
    }

    @Override
    public ServerResponse<String> saveCapacityReview(HttpSession session, Long[] unitId, Integer classify, String word,
                                                     Long courseId, Long id, boolean isKnown) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);

        String[] correctWord = new String[0];
        String[] errorWord = new String[0];
        Integer[] correctWordId = new Integer[0];
        Integer[] errorWordId = new Integer[0];

        if (isKnown) {
            correctWord = new String[1];
            correctWordId = new Integer[1];
            correctWord[0] = word;
            correctWordId[0] = Integer.parseInt(id.toString());
        } else {
            errorWord = new String[1];
            errorWordId = new Integer[1];
            errorWord[0] = word;
            errorWordId[0] = Integer.parseInt(id.toString());
        }

        // 保存学习记录和慧追踪信息
        ServerResponse<String> serverResponse = saveTestLearnAndCapacity.saveLearnAndCapacity(correctWord, errorWord,
                correctWordId, errorWordId, session, student, unitId, classify);
        if (serverResponse != null) {
            return serverResponse;
        }
        return ServerResponse.createBySuccessMessage("学习记录保存成功！");
    }

    /**
     * 单词图鉴智能复习模块
     * @param studentId 学生id
     * @param unitId 单元id
     * @param model 1=单词图鉴模块
     * @param course_id 课程id
     * @return
     */
    @Override
    public ServerResponse<Map<String, Object>> Reviewcapacity_picture(Long studentId, String unitId, int model, String course_id, String judge, boolean pattern) {

        // 复习上单元
        if(pattern){
            unitId = learnMapper.getEndUnitIdByStudentId(studentId);
        }

        // 1. 根据随机数获取题型, 并查出一道正确的题
        // 1.1 去慧记忆中查询单词图鉴是否有需要复习的单词
        Map<String, Object> correct;
        if(judge != null && StringUtils.isNotEmpty(unitId)){
            // 根据单元查询
            correct = capacityPictureMapper.selectNeedReviewWord(Long.valueOf(unitId), studentId, DateUtil.DateTime());
        }else{
            // 根据课程查询 课程复习模块
            correct = capacityPictureMapper.selectNeedReviewWordCourse(course_id, studentId, DateUtil.DateTime());
            if (correct != null) {
                unitId = correct.get("unit_id").toString();
            }
        }

        // 没有需要复习的了
        if(correct == null){
            return ServerResponse.createBySuccess();
        }
        correct.put("recordpicurl", ftpPrefix + correct.get("recordpicurl"));
        // 记忆强度
        correct.put("memoryStrength", correct.get("memory_strength"));

        // 记忆难度
        CapacityPicture cp = new CapacityPicture();
        cp.setStudentId(studentId);
        cp.setUnitId(Long.valueOf(unitId));
        cp.setVocabularyId(Long.valueOf(correct.get("id").toString()));
        Object fault_time = correct.get("fault_time");
        Object memory_strength = correct.get("memory_strength");
        if(fault_time==null){
            cp.setFaultTime(0);
        }else {
            cp.setFaultTime(Integer.parseInt(fault_time.toString()));
        }
        if(memory_strength==null){
            cp.setMemoryStrength(0.0);
        }else {
            cp.setMemoryStrength(Double.parseDouble(memory_strength.toString()));
        }
        Integer hard = memoryDifficultyUtil.getMemoryDifficulty(cp, 1);
        correct.put("memoryDifficulty", hard);

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
        if (course_id != null) {
            mapErrorVocabulary = vocabularyMapper.getWordIdByCourse(new Long(correct.get("id").toString()), Long.valueOf(course_id), Long.parseLong(unitId));
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
        for(Map m : mapErrorVocabulary){

            boolean b = false;
            if(m.get("word").equals(correct.get("word"))){
                b = true;
            }

            correct.put("type", 2);
            subject.put(m.get("word"), b);
        }
        // 把四个选项添加到correct正确答案数据中
        correct.put("subject", subject);

        // 3. count单元表单词有多少个查询存在图片的
        Integer count = unitMapper.countWordByUnitidByPic(Long.valueOf(unitId));
        correct.put("wordCount", count);
        correct.put("studyNew", false);

        // 4. 该单元已学单词
        Long learnedCount = learnMapper.learnCountWord(studentId, Integer.parseInt(unitId), "单词图鉴");
        correct.put("plan", learnedCount);

        return ServerResponse.createBySuccess(correct);

    }

    /**
     * 单词图鉴测试复习
     * @param unit_id
     * @param classify
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> testReviewWordPic(String unit_id, int classify, HttpSession session, boolean pattern) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Long studentId = student.getId();

        // 复习测试上一单元
        if(pattern){
            unit_id = learnMapper.getEndUnitIdByStudentId(studentId);
        }

        // 获取单元下需要复习的单词
        List<Vocabulary> list = vocabularyMapper.getMemoryWordPicAll(Long.parseLong(unit_id), studentId, DateUtil.DateTime());
        // 随机获取带图片的单词, 正确答案的三倍
        List<Vocabulary> listSelect = vocabularyMapper.getWordIdByAll(list.size() * 4);

        // 分题工具类
        Map<String, Object> map = wordPictureUtil.allocationWord(list, listSelect, null);
        return ServerResponse.createBySuccess(map);
    }

    /**
     * WordPicModel
     *
     * @param courseId
     * @param unitId
     * @param select 选择: 1=已学, 2=生词, 3=熟词
     * @param classify 0=WordPicModel
     * @param isTrue
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object>  testWordPic(String courseId, String unitId, int select, int classify, Boolean isTrue, HttpSession session) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Long student_id = student.getId();

        // true 扣除一金币
        if(isTrue){
            // 1.查询学生剩余金币
            Integer gold = studentMapper.getSystem_gold(student_id);
            if(gold != null && gold>0){
                // 扣除1金币
                int state = studentMapper.updateBySystem_gold((gold-1), student_id);
            }else{
                // 金币不足
                return ServerResponse.createBySuccess(GoldResponseCode.LESS_GOLD.getCode(), "金币不足");
            }
            // false 第一次点击五维测试  1.查询是否做过该课程的五维测试 2.如果做过返回扣除1金币提示
        }else{
            Integer judgeTest = testRecordMapper.selectJudgeTestToModel(courseId, student_id, 0, select);
            if(judgeTest != null){
                // 已经测试过, 提示扣除金币是否测试
                return ServerResponse.createBySuccess(GoldResponseCode.NEED_REDUCE_GOLD.getCode(), "您已参加过该五维测试，再次测试需扣除1金币。");
            }
        }

        List<Vocabulary> vocabularies = null;
        // select: 1=已学, 2=生词, 3=熟词
        if (select == 1) {
            // 2.获取已学需要出的测试题
            vocabularies = capacityMapper.alreadyWordStrOne(student_id, unitId, "单词图鉴");
        } else if (select == 2) {
            // 3.获取生词需要出的测试题
            vocabularies = capacityMapper.accrueWordStrOne(student_id, unitId, "单词图鉴");
        } else if (select == 3) {
            // 4.获取熟词需要出的测试题
            vocabularies = capacityMapper.ripeWordStrOne(student_id, unitId, "单词图鉴");
        }

        // 随机获取带图片的单词, 正确答案的三倍
        List<Vocabulary> listSelect = vocabularyMapper.getWordIdByAll(vocabularies.size() * 4);

        // 分配题型‘看词选图’、‘听音选图’、‘看图选词’ 3:3:4
        //return ServerResponse.createBySuccess(allocationWord(list, listSelect));

        // 获取课程下的单词id-单元id
        List<Long> ids = learnMapper.selectVocabularyIdByStudentIdAndCourseId(student_id, Long.valueOf(courseId), 0);
        Map<Long, Map<Long, Long>> longMapMap = unitMapper.selectIdMapByCourseIdAndWordIds(Long.valueOf(courseId), ids, student_id, classify);

        // 分题工具类
        Map<String, Object> map = wordPictureUtil.allocationWord(vocabularies, listSelect, longMapMap);

        return ServerResponse.createBySuccess(map);
    }

    @Override
    public ServerResponse<List<SentenceTranslateVo>> getSentenceReviewTest(HttpSession session, Long unitId, Integer classify, Integer type, boolean pattern) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);

        // 复习测试上一单元
        if(pattern){
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

    static List<Map<String, Object>> packageLastLoginLearnWordIds(List<Learn> learns) {
        List<Map<String, Object>> maps = new ArrayList<>(learns.size());
        learns.forEach(learn -> {
            Map<String, Object> map = new HashMap<>(16);
            map.put("unitId", learn.getUnitId());
            map.put("wordId", learn.getVocabularyId());
            maps.add(map);
        });
        return maps;
    }

    @Override
    public ServerResponse<Map<String, Object>> getWordReview(HttpSession session, Integer classify, Integer totalCount) {
        Student student = getStudent(session);
        // 上次登录期间学生的单词学习信息
        Duration duration = durationMapper.selectLastLoginDuration(student.getId());
        if (duration != null) {
            List<Learn> learns = learnMapper.selectLastLoginStudy(student.getId(), duration.getLoginTime(), duration.getLoginOutTime());
            if (learns.size() > 0) {
                return packageWordReviewResult(classify, totalCount, student, learns);
            }
        }
        return ServerResponse.createBySuccess();
    }

    private ServerResponse<Map<String, Object>> packageWordReviewResult(Integer classify, Integer totalCount, Student student, List<Learn> learns) {
        // 存储单词id及单元
        List<Map<String, Object>> maps = packageLastLoginLearnWordIds(learns);

        Map<String, Object> map = capacityMapper.selectLastLoginNeedReview(student.getId(), maps, classify);
        int count;
        if (totalCount == null) {
            count = capacityMapper.countLastLoginNeedReview(student.getId(), maps, classify);
        } else {
            count = totalCount;
        }

        // 没有需要复习的单词
        if (map == null || map.size() == 0) {
            return ServerResponse.createBySuccess();
        }

        Vocabulary vocabulary = packageWordReviewContent(map, count, student.getId(), classify);

        // 单词图鉴相关内容
        if (classify == 0) {
            map.put("recordpicurl", ftpPrefix + vocabulary.getRecordpicurl());
            List<Map<String, Object>> mapErrorVocabulary = vocabularyMapper.getWordIdByUnit(new Long(map.get("id").toString()), map.get("unit_id").toString());
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
        Integer hard = memoryDifficultyUtil.getMemoryDifficulty(cm, 1);
        map.put("memoryDifficulty", hard);

        map.put("soundmark", vocabulary.getSoundMark());
        // 读音url
        map.put("readUrl", baiduSpeak.getLanguagePath(map.get("word").toString()));

        map.put("wordCount", count);
        map.put("studyNew", false);

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
    private int saveTestRecord(int quantity, int errorCount, int rightCount, Integer classify, HttpSession
            session,
                               Student student, Integer point, String genre, Long courseId) {
        String studyModel = commonMethod.getTestType(classify);
        StringBuilder msg = new StringBuilder();
        long stuId = student.getId();
        TestRecord testRecord = new TestRecord();
        testRecord.setTestStartTime((Date) session.getAttribute(TimeConstant.BEGIN_START_TIME));
        testRecord.setTestEndTime(new Date());
        testRecord.setStudyModel(studyModel);
        testRecord.setStudentId(stuId);
        testRecord.setRightCount(rightCount);
        testRecord.setQuantity(quantity);
        testRecord.setPoint(point);
        testRecord.setGenre(genre);
        testRecord.setErrorCount(errorCount);
        testRecord.setCourseId(courseId);

        int gold = 0;

        if ("已学测试".equals(genre) || "生词测试".equals(genre) || "熟词测试".equals(genre) || genre.contains("五维测试")) {
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
                gold = decideFiveD(point, genre, student, msg, studyModel, testRecord);
            } else {
                TestRecord preTestRecord = testRecords.get(0);
                if (preTestRecord.getPoint() < point) {
                    if (point >= 80) {
                        testRecord.setBetterCount(preTestRecord.getBetterCount() + 1);
                    } else {
                        testRecord.setBetterCount(preTestRecord.getBetterCount());
                    }
                    msg.append("id 为 ").append(stuId).append(" 的学生在 测试中心 -> ");
                    gold = decideFiveD(point, genre, student, msg, studyModel, testRecord);
                }
            }
            ccieUtil.saveCcieTest(student, 6,  classify);
        } else if ("已学测试".equals(genre) || "生词测试".equals(genre) || "熟词测试".equals(genre)) {
            // 判断学生之前是否已经在当前课程有过“已学测试”或者“生词测试”或者“熟词测试”
            List<TestRecord> testRecords = testRecordMapper.selectMaxPointByStudyModel(stuId, courseId, genre, studyModel);
            if (testRecords.size() == 0) {
                initTestCenterBetterCount(point, testRecord);
                msg.append("id 为 ").append(stuId).append(" 的学生在 测试中心 -> ");
                gold = decideLearnedUnKnown(point, genre, student, msg, studyModel, testRecord);
            } else {
                TestRecord preTestRecord = testRecords.get(0);
                if (preTestRecord.getPoint() < point) {
                    if (point >= 80) {
                        testRecord.setBetterCount(preTestRecord.getBetterCount() + 1);
                    } else {
                        testRecord.setBetterCount(preTestRecord.getBetterCount());
                    }
                    msg.append("id 为 ").append(stuId).append(" 的学生在 测试中心 -> ");
                    gold = decideLearnedUnKnown(point, genre, student, msg, studyModel, testRecord);
                }
            }
            int testType = 3;
            if ("生词测试".equals(genre)) {
                testType = 4;
            } else if ("熟词测试".equals(genre)) {
                testType = 5;
            }
            ccieUtil.saveCcieTest(student, testType, classify);
        } else if ("复习测试".equals(genre)) {
            // 判断学生之前是否已经在当前课程有过“已学测试”或者“生词测试”或者“熟词测试”
            List<TestRecord> testRecords = testRecordMapper.selectMaxPointByStudyModel(stuId, courseId, genre, studyModel);
            if (testRecords.size() == 0) {
                initTestCenterBetterCount(point, testRecord);
            } else {
                TestRecord preTestRecord = testRecords.get(0);
                if (preTestRecord.getPoint() < point && point >= 90) {
                    testRecord.setBetterCount(preTestRecord.getBetterCount() + 1);
                } else {
                    testRecord.setBetterCount(preTestRecord.getBetterCount());
                }
            }
            // 奖励金币信息
            gold = reviewGold(point, genre, student, msg, studyModel, testRecord);
            ccieUtil.saveCcieTest(student, 2,  classify);
        }

        studentMapper.updateByPrimaryKeySelective(student);
        session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        testRecordMapper.insert(testRecord);
        if (msg.length() > 0) {
            logger.info(msg.toString());
            RunLog runLog = new RunLog(stuId, 4, msg.toString(), new Date());
            runLog.setUnitId(student.getUnitId());
            runLog.setCourseId(student.getCourseId());
            runLogMapper.insert(runLog);
        }
        return gold;
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
     * 判断 复习测试 奖励金
     *
     * @param point
     * @param genre
     * @param student
     * @param msg
     * @param studyModel
     * @param testRecord
     */
    private int reviewGold(Integer point, String genre, Student student, StringBuilder msg, String
            studyModel, TestRecord testRecord) {
        int gold = 0;
        if (point >= 80) {
            // 奖励1金币
            gold = testRecord.getBetterCount() * TestAwardGoldConstant.REVIEW_TEST_NINETY_TO_FULL;
            student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), gold));
            testRecord.setAwardGold(gold);
            msg.append("id 为 ").append(student.getId()).append(" 的学生在 ").append(genre).append(studyModel)
                    .append(" 中获得#").append(gold).append("#金币。");
        } else {
            testRecord.setAwardGold(0);
        }
        return gold;
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
    private int decideLearnedUnKnown(Integer point, String genre, Student student, StringBuilder msg, String
            studyModel, TestRecord testRecord) {
        int gold = 0;
        msg.append(genre).append(studyModel);
        if (point < 90 && point >= 80) {
            // 奖励2金币
            gold = testRecord.getBetterCount() * TestAwardGoldConstant.TEST_CENTER_ENGHTY_TO_NINETY;
            student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), gold));
            testRecord.setAwardGold(gold);
            msg.append(" 中获得#").append(gold).append("#金币。");
        } else if (point >= 90 && point <= 100) {
            // 奖励5枚金币
            gold = testRecord.getBetterCount() * TestAwardGoldConstant.TEST_CENTER_NINETY_TO_FULL;
            student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), gold));
            testRecord.setAwardGold(gold);
            msg.append(" 中获得#").append(gold).append("#金币。");
        } else {
            testRecord.setAwardGold(0);
            msg.append(" 中未获得金币");
        }
        return gold;
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
    private int decideFiveD(Integer point, String genre, Student student, StringBuilder msg, String
            studyModel, TestRecord testRecord) {
        int gold = 0;
        msg.append(genre).append(studyModel);
        if (point < 90 && point >= 80) {
            // 奖励10金币
            gold = testRecord.getBetterCount() * TestAwardGoldConstant.FIVE_TEST_EIGHTY_TO_NINETY;
            student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), gold));
            testRecord.setAwardGold(gold);
            msg.append(" 中获得#").append(gold).append("#金币。");
        } else if (point >= 90 && point <= 100) {
            // 奖励20枚金币
            gold = testRecord.getBetterCount() * TestAwardGoldConstant.FIVE_TEST_NINETY_TO_FULL;
            student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), gold));
            testRecord.setAwardGold(gold);
            msg.append(" 中获得#").append(gold).append("#金币。");
        } else {
            testRecord.setAwardGold(0);
            msg.append(" 中未获得金币。");
        }
        return gold;

    }

}
