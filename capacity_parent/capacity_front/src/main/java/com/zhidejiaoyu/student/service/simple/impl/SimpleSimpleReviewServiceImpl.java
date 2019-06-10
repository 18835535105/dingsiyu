package com.zhidejiaoyu.student.service.simple.impl;

import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.simple.SimpleCommonMethod;
import com.zhidejiaoyu.common.study.simple.SimpleWordPictureUtil;
import com.zhidejiaoyu.common.utils.simple.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.simple.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.simple.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.simple.language.YouDaoTranslate;
import com.zhidejiaoyu.common.utils.simple.server.GoldResponseCode;
import com.zhidejiaoyu.common.utils.simple.server.ServerResponse;
import com.zhidejiaoyu.common.utils.simple.testUtil.TestResult;
import com.zhidejiaoyu.common.utils.simple.testUtil.TestResultUtil;
import com.zhidejiaoyu.student.service.simple.SimpleReviewService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
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
public class SimpleSimpleReviewServiceImpl implements SimpleReviewService {

    private Logger logger = LoggerFactory.getLogger(SimpleSimpleReviewServiceImpl.class);

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
    private YouDaoTranslate youDaoTranslate;

    @Autowired
    private SimpleCommonMethod simpleCommonMethod;

    @Autowired
    private BaiduSpeak baiduSpeak;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private CapacityPictureMapper capacityPictureMapper;

    @Autowired
    private SimpleWordPictureUtil simpleWordPictureUtil;

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
    public ServerResponse<Object> testCapacityReview(String unit_id, int classify, HttpSession session) {
        // 封装测试单词
        List<Vocabulary> vocabularies;

        // 获取当前学生信息
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        // 学生id
        Long id = student.getId();
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

        if (classify == 1 || classify == 2 || classify == 3 || classify == 4 || classify == 5 || classify == 6) {
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
        }
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
    public Map<String, Object> ReviewCapacity_memory(Long id, String unit_id, int classify, String course_id) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

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
            map.put("plan", count_); // ./

            // count单元表单词有多少个    /.
    		Integer count = unitMapper.countWordByUnitid(unit_id);
    		map.put("wordCount", count); // /.
        }
        // 任务课程-复习, 根据课程查询
        if (StringUtils.isNotBlank(course_id)) {
            // 查询条件2.2:课程id
            ca.setCourse_id(Long.valueOf(course_id));

            // 根据课程id获取课程名
            String courseName = courseMapper.selectByCourseName(course_id);
            map.put("courseId", course_id); // 课程id
            map.put("courseName", courseName); // 课程名

            // 该课程已学单词
            Long count_ = learnMapper.learnCourseCountWord(id, course_id, model);
            map.put("plan", count_); // ./
            // 该课程一共多少单词
            Integer count = unitMapper.countWordByCourse(course_id);
            map.put("wordCount", count); // /.
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
        map.put("id", vo.getVocabulary_id()); // 单词id
        map.put("word", word); // 单词
        map.put("wordChinese", vo.getWord_chinese()); // 翻译
        map.put("wordyj", vo.getSyllable()); // 音节
        if(StringUtils.isBlank(unit_id)){
            map.put("unitId", vo.getUnit_id());
            unit_id = vo.getUnit_id().toString();
        }
        // 如果音节为空
        if (vo.getSyllable() == null) {
            map.put("wordyj", word); // 单词
        }
        try {
            Map<String, String> resultMap = youDaoTranslate.getResultMap(word);
            // 音标
            String phonetic = resultMap.get("phonetic");
            // 判断音标是否为null
            if(StringUtils.isNotBlank(phonetic) && !"null".equals(phonetic)){
                map.put("soundmark", " [" + phonetic + "]");
            }else{
                map.put("soundmark", null);
            }
            map.put("readUrl", baiduSpeak.getLanguagePath(word)); // 读音
        } catch (Exception e) {
        }

        // 根据单词id获取音节
//        String syllable = vocabularyMapper.getSyllableByWordid(vo.getVocabulary_id());
//        map.put("wordyj", syllable);

       /* if (StringUtils.isNotBlank(course_id)) {
            if (StringUtils.isBlank(unit_id)) {
                // 根据单词id和课程id查询单元id
                unit_id = unitMapper.getUnitIdByCourseIdAndWordId(course_id, vo.getVocabulary_id());
                map.put("unitId", unit_id);
            }
        }*/

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
//            Integer hard = memoryDifficultyUtil.getMemoryDifficulty(cm, 1);
//            map.put("memoryDifficulty", hard);
        }

        return map;
    }

    @Override
    public Map<String, Object> ReviewSentence_listen(Long stuId, String unit_id, int classify, String course_id) {
        Map<String, Object> map = new HashMap<>();
        // 当前时间
        String dateTime = DateUtil.DateTime();

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
        }

        // 2.通过记忆追踪中的例句id-获取例句信息 (vo.getVocabulary_id()是例句id)
        Sentence sentence = sentenceMapper.selectByPrimaryKey(vo.getVocabulary_id());

        String english = sentence.getCentreExample().replace("#", " "); // 分割例句
        String chinese = sentence.getCentreTranslate().replace("*", ""); // 分割翻译

        // 例句id
        map.put("id", sentence.getId());
        // 例句读音
        map.put("readUrl", baiduSpeak.getLanguagePath(english));
        // 例句翻译
        map.put("word_Chinese", chinese);
        // 例句英文原文
        map.put("sentence", english);
        if (classify != 5) {
            // 例句英文乱序集合
            map.put("orderEnglish", simpleCommonMethod.getOrderEnglishList(sentence.getCentreExample(), sentence.getExampleDisturb()));
            // 例句英文正序集合，含有标点
            map.put("englishList", simpleCommonMethod.getEnglishList(sentence.getCentreExample()));
        }

        if (classify == 5) {
            // 例句翻译中含有乱序的中文解释和干扰项
            map.put("orderChinese", simpleCommonMethod.getOrderChineseList(sentence.getCentreTranslate(), sentence.getTranslateDisturb()));
        }

        return map;
    }

    @Override
    public ServerResponse<List> testcentreindex(int model, String unitId, HttpSession session) {
        // 获取当前学生信息
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Long student_id = student.getId();

        // 返回结果集
        List<Map<String, Object>> list = new ArrayList<>();

        // 单词模块
        if(model <= 3){
            for (int i = 1; i <= 4; i++) {
                Map<String, Object> map = new LinkedHashMap<>();
                String classify = simpleCommonMethod.getTestType(i == 4 ? 0 : i); // 0是单词图鉴
                // 1.已学
                Integer a = capacityMapper.alreadyStudyWord(student_id, unitId, classify);
                // 2.生词
                Integer b = capacityMapper.accrueWord(student_id, unitId, classify);
                // 3.熟词
                Integer c = capacityMapper.ripeWord(student_id, unitId, classify);
                map.put("classify", i == 4 ? 0 : i);
                map.put("alreadyStudy", a);
                map.put("accrue", b);
                map.put("ripe", c);
                list.add(map);
            }
        }else{ // 例句模块
            for (int i = 4; i <= 6; i++) {
                Map<String, Object> map = new LinkedHashMap<>();
                String classify = simpleCommonMethod.getTestType(i);
                // 1.已学
                Integer a = capacityMapper.alreadyStudyWord(student_id, unitId, classify);
                // 2.生词
                Integer b = capacityMapper.accrueWord(student_id, unitId, classify);
                // 3.熟词
                Integer c = capacityMapper.ripeWord(student_id, unitId, classify);
                map.put("classify", i);
                map.put("alreadyStudy", a);
                map.put("accrue", b);
                map.put("ripe", c);
                list.add(map);
            }
        }

        return ServerResponse.createBySuccess(list);
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
    public Map<String, Object> testcentreSentence(String unitId, int select, int classify, HttpSession session) {
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

        Sentence vo = new Sentence();

        // 已学
        if (select == 1) {
            // 查询 例句,例句翻译
            vo = capacityMapper.centreReviewSentence_listen(student_id, unitId, classifyStr);
            // 生词
        } else if (select == 2) {
            vo = capacityMapper.accrueCentreReviewSentence_listen(student_id, unitId, classifyStr);
            // 熟词
        } else if (select == 3) {
            vo = capacityMapper.ripeCentreReviewSentence_listen(student_id, unitId, classifyStr);
        }
        // 1.例句读音
        map.put("readUrl", baiduSpeak.getLanguagePath(vo.getCentreExample()));
        // 2.例句翻译
        map.put("word_Chinese", vo.getCentreTranslate());
        // 3.正确顺序例句
        String word = vo.getCentreExample();
        word = word.replace(".", "");
        String[] justWord = word.split(" ");
        map.put("justWord", justWord);

        // 打乱例句顺序
        List<String> list = new ArrayList<>(Arrays.asList(justWord));
        Collections.shuffle(list);
        Object[] wrongWord = list.toArray();
        map.put("wrongWord", wrongWord);

        /*// 4.打乱顺序例句, 打乱除了例句默写
        if (classify != 6) {
            String[] wrongWord = NumberUtil.randomWord(justWord);
            map.put("wrongWord", wrongWord);
        }*/

        return map;
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
        List<TestResult> testResults_b = testResultUtil.getWordTestesForCourse(type_b, vocabularies.subList(vocabularies.size()/3, (int) BigDecimalUtil.div((double)vocabularies.size(),1.5,0)).size(),
                vocabularies.subList(vocabularies.size()/3, (int) BigDecimalUtil.div((double)vocabularies.size(),1.5,0)), Long.valueOf(course_id));
        List<TestResult> testResults_c = testResultUtil.getWordTestesForCourse(type_c, vocabularies.subList((int) BigDecimalUtil.div((double)vocabularies.size(),1.5,0), vocabularies.size()).size(),
                vocabularies.subList((int) BigDecimalUtil.div((double)vocabularies.size(),1.5,0), vocabularies.size()), Long.valueOf(course_id));

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
       /* ServerResponse<String> serverResponse = saveTestLearnAndCapacity.saveLearnAndCapacity(correctWord, errorWord,
                correctWordId, errorWordId, session, student, unitId, classify);
        if (serverResponse != null) {
            return serverResponse;
        }*/
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
    public ServerResponse<Map<String, Object>> Reviewcapacity_picture(Long studentId, String unitId, int model, String course_id, String judge) {

        // 用于判断哪个题型
        Random random = new Random();
        int i = random.nextInt(3)+1;

        // 1. 根据随机数获取题型, 并查出一道正确的题
        // 1.1 去慧记忆中查询单词图鉴是否有需要复习的单词
        Map<String, Object> correct = null;
        if(judge != null && StringUtils.isNotEmpty(unitId)){
            // 根据单元查询
            correct = capacityPictureMapper.selectNeedReviewWord(Long.valueOf(unitId), studentId, DateUtil.DateTime());
        }else{
            // 根据课程查询 课程复习模块
            correct = capacityPictureMapper.selectNeedReviewWordCourse(course_id, studentId, DateUtil.DateTime());
            unitId = correct.get("unit_id").toString();
        }

        // 没有需要复习的了
        if(correct == null){
            return null;
        }


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
//        Integer hard = memoryDifficultyUtil.getMemoryDifficulty(cp, 1);
//        correct.put("memoryDifficulty", hard);

        try {
            Map<String, String> resultMap = youDaoTranslate.getResultMap(correct.get("word").toString());
            // 音标
            String phonetic = resultMap.get("phonetic");
            if(StringUtils.isNotBlank(phonetic) && !"null".equals(phonetic)) {
                correct.put("soundmark", "[" + phonetic + "]");
            }else {
                correct.put("soundmark", null);
            }
            // 读音url
            correct.put("readUrl", baiduSpeak.getLanguagePath(correct.get("word").toString()));
            // 词性
            // String explains = resultMap.get("explains");
            //String exp = explains.substring(2, explains.indexOf(".") + 1);
            //map.put("exp", exp);
        } catch (Exception e) {
            logger.error("ReviewServiceImpl -> Reviewcapacity_picture (153行)");
        }

        List<Map<String, Object>> mapErrorVocabulary = null;
        // 2. 从课程下随机获取三个题, 三个作为错题, 并且id不等于正确题id
        if(course_id!=null){
            mapErrorVocabulary = vocabularyMapper.getWordIdByCourse(new Long(correct.get("id").toString()),Long.valueOf(course_id), Long.parseLong(unitId));
        }else{
        //  从单元下随机获取三个题, 三个作为错题, 并且id不等于正确题id
            mapErrorVocabulary = vocabularyMapper.getWordIdByUnit(new Long(correct.get("id").toString()), unitId);
        }
        mapErrorVocabulary.add(correct); // 四道题
        Collections.shuffle(mapErrorVocabulary); // 随机打乱顺序

        // 封装四个选项
        Map subject = new HashMap();
        for(Map m : mapErrorVocabulary){

            Boolean b = false;
            if(m.get("word").equals(correct.get("word"))){
                b = true;
            }

            if(i == 1){
                correct.put("type", 1);
                subject.put(m.get("recordpicurl"), b);
            }else if(i == 2){
                correct.put("type", 2);
                subject.put(m.get("word"), b);
            }else {
                correct.put("type", 3);
                subject.put(m.get("recordpicurl"), b);
            }
        }
        // 把四个选项添加到correct正确答案数据中
        correct.put("subject", subject);

        // 3. count单元表单词有多少个查询存在图片的    /.
        Integer count = unitMapper.countWordByUnitidByPic(Long.valueOf(unitId));
        correct.put("wordCount", count);

        // 4. 该单元已学单词  ./
        //Integer count_ = capacityListenMapper.alreadyStudyWord(unit_id, id);
        Long count_ = learnMapper.learnCountWord(studentId, Integer.parseInt(unitId), "单词图鉴");
        correct.put("plan", count_);

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
    public ServerResponse<Object> testReviewWordPic(String unit_id, int classify, HttpSession session) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Long studentId = student.getId();

        // 获取单元下需要复习的单词
        List<Vocabulary> list = vocabularyMapper.getMemoryWordPicAll(Long.parseLong(unit_id), studentId, DateUtil.DateTime());
        // 随机获取带图片的单词, 正确答案的三倍
        List<Vocabulary> listSelect = vocabularyMapper.getWordIdByAll(list.size() * 4);

        // 分题工具类
        Map<String, Object> map = simpleWordPictureUtil.allocationWord(list, listSelect, null);
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
    public ServerResponse<Object> testWordPic(String courseId, String unitId, int select, int classify, Boolean isTrue, HttpSession session) {
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
        Map<String, Object> map = simpleWordPictureUtil.allocationWord(vocabularies, listSelect, longMapMap);

        return ServerResponse.createBySuccess(map);
    }
}
